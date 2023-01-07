package org.moon.figura.model;

import com.mojang.datafixers.util.Pair;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.texture.OverlayTexture;
import org.jetbrains.annotations.NotNull;
import org.luaj.vm2.LuaError;
import org.moon.figura.lua.LuaNotNil;
import org.moon.figura.lua.LuaWhitelist;
import org.moon.figura.lua.docs.LuaTypeDoc;
import org.moon.figura.math.matrix.FiguraMat3;
import org.moon.figura.math.matrix.FiguraMat4;
import org.moon.figura.math.vector.FiguraVec2;
import org.moon.figura.math.vector.FiguraVec3;
import org.moon.figura.model.rendering.ImmediateAvatarRenderer;
import org.moon.figura.model.rendering.texture.FiguraTexture;
import org.moon.figura.model.rendering.texture.FiguraTextureSet;
import org.moon.figura.model.rendering.texture.RenderTypes;
import org.moon.figura.model.rendertasks.BlockTask;
import org.moon.figura.model.rendertasks.ItemTask;
import org.moon.figura.model.rendertasks.RenderTask;
import org.moon.figura.model.rendertasks.TextTask;
import org.moon.figura.utils.LuaUtils;
import org.moon.figura.utils.ui.UIHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@LuaWhitelist
@LuaTypeDoc(
        name = "ModelPart",
        value = "model_part"
)
public class FiguraModelPart implements Comparable<FiguraModelPart> {

    public final String name;
    public FiguraModelPart parent;

    public final PartCustomization customization;
    public ParentType parentType = ParentType.None;
    private Boolean vanillaVisible = null;

    private final Map<String, FiguraModelPart> childCache = new HashMap<>();
    public final List<FiguraModelPart> children;

    public List<Integer> facesByTexture;

    public Map<String, RenderTask> renderTasks = new HashMap<>();

    public List<FiguraTextureSet> textures;
    public int textureWidth, textureHeight; //If the part has multiple textures, then these are -1.

    public boolean animated = false;
    public int animationOverride = 0;
    public int lastAnimationPriority = Integer.MIN_VALUE;

    public final FiguraMat4 savedPartToWorldMat = FiguraMat4.of().scale(1 / 16d, 1 / 16d, 1 / 16d);

    public FiguraModelPart(String name, PartCustomization customization, List<FiguraModelPart> children) {
        this.name = name;
        this.customization = customization;
        this.children = children;
        for (FiguraModelPart child : children)
            child.parent = this;
    }

    public boolean pushVerticesImmediate(ImmediateAvatarRenderer avatarRenderer, int[] remainingComplexity) {
        for (int i = 0; i < facesByTexture.size(); i++) {
            if (remainingComplexity[0] <= 0)
                return false;
            remainingComplexity[0] -= facesByTexture.get(i);
            avatarRenderer.pushFaces(i, facesByTexture.get(i) + Math.min(remainingComplexity[0], 0), remainingComplexity);
        }
        return true;
    }

    public void advanceVerticesImmediate(ImmediateAvatarRenderer avatarRenderer) {
        for (int i = 0; i < facesByTexture.size(); i++)
            avatarRenderer.advanceFaces(i, facesByTexture.get(i));

        for (FiguraModelPart child : this.children)
            child.advanceVerticesImmediate(avatarRenderer);
    }

    public void applyVanillaTransforms(VanillaModelData vanillaModelData) {
        if (vanillaModelData == null)
            return;

        //get part data
        VanillaModelData.PartData partData = vanillaModelData.partMap.get(this.parentType);
        if (partData == null)
            return;

        //apply vanilla transforms
        vanillaVisible = partData.visible;

        FiguraVec3 defaultPivot = parentType.offset.copy();

        defaultPivot.sub(partData.pos);

        if (!overrideVanillaScale())
            defaultPivot.mul(partData.scale);

        if (!overrideVanillaPos()) {
            customization.offsetPivot(defaultPivot);
            customization.offsetPos(defaultPivot);
        }

        //customization.offsetPivot(pivot);
        if (!overrideVanillaRot())
            customization.offsetRot(partData.rot);

        defaultPivot.free();
    }

    public void resetVanillaTransforms() {
        if (parentType.provider != null) {
            if (!overrideVanillaPos()) {
                customization.offsetPivot(0, 0, 0);
                customization.offsetPos(0, 0, 0);
            }
            if (!overrideVanillaRot())
                customization.offsetRot(0, 0, 0);
        }
    }

    public void applyExtraTransforms(FiguraMat4 currentTransforms) {
        if (parentType != ParentType.Camera)
            return;

        FiguraMat4 prevPartToView = currentTransforms.inverted();
        double s = 1 / 16d;
        if (UIHelper.paperdoll) {
            s *= -UIHelper.dollScale;
        } else {
            prevPartToView.rightMultiply(FiguraMat4.of().rotateY(180));
        }
        prevPartToView.scale(s, s, s);
        FiguraVec3 piv = customization.getPivot();
        FiguraVec3 piv2 = customization.getOffsetPivot().add(piv);
        prevPartToView.v14 = prevPartToView.v24 = prevPartToView.v34 = 0;
        prevPartToView.translateFirst(-piv2.x, -piv2.y, -piv2.z);
        prevPartToView.translate(piv2.x, piv2.y, piv2.z);
        customization.setMatrix(prevPartToView);
        prevPartToView.free();
        piv.free();
        piv2.free();
    }

    public void clean() {
        customization.free();
        for (FiguraModelPart child : children)
            child.clean();
    }

    // -- animations -- //

    public void animPosition(FiguraVec3 vec, boolean merge) {
        if (merge) {
            FiguraVec3 pos = customization.getAnimPos();
            pos.add(vec);
            customization.setAnimPos(pos);
            pos.free();
        } else {
            customization.setAnimPos(vec);
        }
    }
    public void animRotation(FiguraVec3 vec, boolean merge) {
        if (merge) {
            FiguraVec3 rot = customization.getAnimRot();
            rot.add(vec);
            customization.setAnimRot(rot);
            rot.free();
        } else {
            customization.setAnimRot(vec);
        }
    }
    public void globalAnimRot(FiguraVec3 vec, boolean merge) {
        /*FiguraModelPart part = parent;
        while (part != null) {
            FiguraVec3 rot = part.getAnimRot();
            vec.subtract(rot);
            rot.free();
            part = part.parent;
        }*/
        animRotation(vec, merge);
    }
    public void animScale(FiguraVec3 vec, boolean merge) {
        if (merge) {
            FiguraVec3 scale = customization.getAnimScale();
            scale.mul(vec);
            customization.setAnimScale(scale);
            scale.free();
        } else {
            customization.setAnimScale(vec);
        }
    }

    //-- LUA BUSINESS --//

    @LuaWhitelist
    public String getName() {
        return this.name;
    }

    @LuaWhitelist
    public FiguraModelPart getParent() {
        return this.parent;
    }

    @LuaWhitelist
    public Map<Integer, FiguraModelPart> getChildren() {
        Map<Integer, FiguraModelPart> map = new HashMap<>();
        for (int i = 0; i < this.children.size(); i++)
            map.put(i + 1, this.children.get(i));
        return map;
    }

    @LuaWhitelist
    public boolean isChildOf(@LuaNotNil FiguraModelPart part) {
        FiguraModelPart p = parent;
        while (p != null) {
            if (p == part)
                return true;
            p = p.parent;
        }

        return false;
    }

    @LuaWhitelist
    public FiguraVec3 getPos() {
        return this.customization.getPos();
    }

    @LuaWhitelist
    public void setPos(double x, double y, double z) {
        setPos(LuaUtils.freeVec3("setPos", x, y, z));
    }

    @LuaWhitelist
    public void setPos(@LuaNotNil FiguraVec3 pos) {
        this.customization.setPos(pos);
    }

    @LuaWhitelist
    public FiguraModelPart pos(double x, double y, double z) {
        return pos(FiguraVec3.oneUse(x, y, z));
    }

    @LuaWhitelist
    public FiguraModelPart pos(@LuaNotNil FiguraVec3 pos) {
        setPos(pos);
        return this;
    }

    @LuaWhitelist
    public FiguraVec3 getAnimPos() {
        return this.customization.getAnimPos();
    }

    @LuaWhitelist
    public FiguraVec3 getRot() {
        return this.customization.getRot();
    }

    @LuaWhitelist
    public void setRot(double x, double y, double z) {
        setRot(FiguraVec3.oneUse(x, y, z));
    }

    @LuaWhitelist
    public void setRot(@LuaNotNil FiguraVec3 rot) {
        this.customization.setRot(rot);
    }

    @LuaWhitelist
    public FiguraModelPart rot(double x, double y, double z) {
        return rot(FiguraVec3.oneUse(x, y, z));
    }

    @LuaWhitelist
    public FiguraModelPart rot(@LuaNotNil FiguraVec3 rot) {
        setRot(rot);
        return this;
    }

    @LuaWhitelist
    public FiguraVec3 getOffsetRot() {
        return this.customization.getOffsetRot();
    }

    @LuaWhitelist
    public void setOffsetRot(double x, double y, double z) {
        setOffsetRot(FiguraVec3.oneUse(x, y, z));
    }

    @LuaWhitelist
    public void setOffsetRot(@LuaNotNil FiguraVec3 offsetRot) {
        this.customization.offsetRot(offsetRot);
    }

    @LuaWhitelist
    public FiguraModelPart offsetRot(double x, double y, double z) {
        return offsetRot(FiguraVec3.oneUse(x, y, z));
    }

    @LuaWhitelist
    public FiguraModelPart offsetRot(@LuaNotNil FiguraVec3 offsetRot) {
        setOffsetRot(offsetRot);
        return this;
    }

    @LuaWhitelist
    public FiguraVec3 getAnimRot() {
        return this.customization.getAnimRot();
    }

    @LuaWhitelist
    public FiguraVec3 getScale() {
        return this.customization.getScale();
    }

    @LuaWhitelist
    public void setScale(Double x, Double y, Double z) {
        setScale(LuaUtils.freeVec3("setScale", x, y, z, 1, 1, 1));
    }

    @LuaWhitelist
    public void setScale(@LuaNotNil FiguraVec3 scale) {
        this.customization.setScale(scale);
    }

    @LuaWhitelist
    public FiguraModelPart scale(Double x, Double y, Double z) {
        return scale(LuaUtils.freeVec3("setScale", x, y, z, 1, 1, 1));
    }

    @LuaWhitelist
    public FiguraModelPart scale(@LuaNotNil FiguraVec3 scale) {
        setScale(scale);
        return this;
    }

    @LuaWhitelist
    public FiguraVec3 getAnimScale() {
        return this.customization.getAnimScale();
    }

    @LuaWhitelist
    public FiguraVec3 getPivot() {
        return this.customization.getPivot();
    }

    @LuaWhitelist
    public void setPivot (double x, double y, double z) {
        setPivot(FiguraVec3.oneUse(x, y, z));
    }

    @LuaWhitelist
    public void setPivot(@LuaNotNil FiguraVec3 pivot) {
        this.customization.setPivot(pivot);
    }

    @LuaWhitelist
    public FiguraModelPart pivot(double x, double y, double z) {
        return pivot(FiguraVec3.oneUse(x, y, z));
    }

    @LuaWhitelist
    public FiguraModelPart pivot(@LuaNotNil FiguraVec3 pivot) {
        setPivot(pivot);
        return this;
    }

    @LuaWhitelist
    public FiguraVec3 getOffsetPivot() {
        return this.customization.getOffsetPivot();
    }

    @LuaWhitelist
    public void setOffsetPivot(double x, double y, double z) {
        setOffsetPivot(FiguraVec3.oneUse(x, y, z));
    }

    @LuaWhitelist
    public void setOffsetPivot(@LuaNotNil FiguraVec3 vec) {
        this.customization.offsetPivot(vec);
    }

    @LuaWhitelist
    public FiguraModelPart offsetPivot(double x, double y, double z) {
        return offsetPivot(FiguraVec3.oneUse(x, y, z));
    }

    @LuaWhitelist
    public FiguraModelPart offsetPivot(@LuaNotNil FiguraVec3 pivotOffset) {
        setOffsetPivot(pivotOffset);
        return this;
    }

    @LuaWhitelist
    public FiguraMat4 getPositionMatrix() {
        this.customization.recalculate();
        return this.customization.getPositionMatrix();
    }

    @LuaWhitelist
    public FiguraMat4 getPositionMatrixRaw() {
        return this.customization.getPositionMatrix();
    }

    @LuaWhitelist
    public FiguraMat3 getNormalMatrix() {
        this.customization.recalculate();
        return this.customization.getNormalMatrix();
    }

    @LuaWhitelist
    public FiguraMat3 getNormalMatrixRaw() {
        return this.customization.getNormalMatrix();
    }

    @LuaWhitelist
    public void setMatrix(@LuaNotNil FiguraMat4 matrix) {
        this.customization.setMatrix(matrix);
    }

    @LuaWhitelist
    public FiguraModelPart matrix(@LuaNotNil FiguraMat4 matrix) {
        setMatrix(matrix);
        return this;
    }

    public boolean getVanillaVisible() {
        FiguraModelPart part = this;
        while (part != null && part.vanillaVisible == null)
            part = part.parent;
        return part == null || part.vanillaVisible;
    }

    @LuaWhitelist
    public boolean getVisible() {
        FiguraModelPart part = this;
        while (part != null && part.customization.visible == null)
            part = part.parent;
        return part == null || part.customization.visible;
    }

    @LuaWhitelist
    public void setVisible(Boolean visible) {
        this.customization.visible = visible;
    }

    @LuaWhitelist
    public FiguraModelPart visible(Boolean bool) {
        setVisible(bool);
        return this;
    }

    @LuaWhitelist
    public String getPrimaryRenderType() {
        RenderTypes renderType = this.customization.getPrimaryRenderType();
        return renderType == null ? null : renderType.name();
    }

    @LuaWhitelist
    public void setPrimaryRenderType(String renderType) {
        try {
            this.customization.setPrimaryRenderType(renderType == null ? null : RenderTypes.valueOf(renderType.toUpperCase()));
        } catch (Exception ignored) {
            throw new LuaError("Illegal RenderType: \"" + renderType + "\".");
        }
    }

    @LuaWhitelist
    public FiguraModelPart primaryRenderType(String type) {
        setPrimaryRenderType(type);
        return this;
    }

    @LuaWhitelist
    public String getSecondaryRenderType() {
        RenderTypes renderType = this.customization.getSecondaryRenderType();
        return renderType == null ? null : renderType.name();
    }

    @LuaWhitelist
    public void setSecondaryRenderType(String renderType) {
        try {
            this.customization.setSecondaryRenderType(renderType == null ? null : RenderTypes.valueOf(renderType.toUpperCase()));
        } catch (Exception ignored) {
            throw new LuaError("Illegal RenderType: \"" + renderType + "\".");
        }
    }

    @LuaWhitelist
    public FiguraModelPart secondaryRenderType(String type) {
        setSecondaryRenderType(type);
        return this;
    }

    @LuaWhitelist
    public void setPrimaryTexture(String textureType) {
        setTextureDumb(textureType, null, false);
    }

    @LuaWhitelist
    public void setPrimaryTexture(@LuaNotNil String textureType, @LuaNotNil String path) {
        setTextureDumb(textureType, path, false);
    }

    @LuaWhitelist
    public void setPrimaryTexture(@LuaNotNil String textureType, @LuaNotNil FiguraTexture texture) {
        setTextureDumb(textureType, texture, false);
    }

    @LuaWhitelist
    public FiguraModelPart primaryTexture(String textureType) {
        return setTextureDumb(textureType, null, false);
    }

    @LuaWhitelist
    public FiguraModelPart primaryTexture(@LuaNotNil String textureType, @LuaNotNil String path) {
        return setTextureDumb(textureType, path, false);
    }

    @LuaWhitelist
    public FiguraModelPart primaryTexture(@LuaNotNil String textureType, @LuaNotNil FiguraTexture texture) {
        return setTextureDumb(textureType, texture, false);
    }

    @LuaWhitelist
    public void setSecondaryTexture(String textureType) {
        setTextureDumb(textureType, null, true);
    }

    @LuaWhitelist
    public void setSecondaryTexture(@LuaNotNil String textureType, @LuaNotNil String path) {
        setTextureDumb(textureType, path, true);
    }

    @LuaWhitelist
    public void setSecondaryTexture(@LuaNotNil String textureType, @LuaNotNil FiguraTexture texture) {
        setTextureDumb(textureType, texture, true);
    }

    @LuaWhitelist
    public FiguraModelPart secondaryTexture(String textureType) {
        return setTextureDumb(textureType, null, true);
    }

    @LuaWhitelist
    public FiguraModelPart secondaryTexture(@LuaNotNil String textureType, @LuaNotNil String path) {
        return setTextureDumb(textureType, path, true);
    }

    @LuaWhitelist
    public FiguraModelPart secondaryTexture(@LuaNotNil String textureType, @LuaNotNil FiguraTexture texture) {
        return setTextureDumb(textureType, texture, true);
    }

    public FiguraModelPart setTextureDumb(String type, Object x, boolean secondary) {
        try {
            if (secondary)
                this.customization.secondaryTexture = type == null ? null : Pair.of(FiguraTextureSet.OverrideType.valueOf(type.toUpperCase()), x);
            else
                this.customization.primaryTexture = type == null ? null : Pair.of(FiguraTextureSet.OverrideType.valueOf(type.toUpperCase()), x);
            return this;
        } catch (Exception ignored) {
            throw new LuaError("Invalid texture override type: " + type);
        }
    }

    @LuaWhitelist
    public List<FiguraTexture> getTextures() {
        List<FiguraTexture> list = new ArrayList<>();

        for (FiguraTextureSet set : textures) {
            for (FiguraTexture texture : set.textures) {
                if (texture != null)
                    list.add(texture);
            }
        }

        return list;
    }

    @LuaWhitelist
    public FiguraMat4 partToWorldMatrix() {
        return this.savedPartToWorldMat.copy();
    }

    @LuaWhitelist
    public FiguraVec2 getTextureSize() {
        if (this.textureWidth == -1 || this.textureHeight == -1) {
            if (this.customization.partType == PartCustomization.PartType.GROUP)
                throw new LuaError("Cannot get the texture size of groups!");
            else
                throw new LuaError("Cannot get texture size of part, it has multiple different-sized textures!");
        }

        return FiguraVec2.of(this.textureWidth, this.textureHeight);
    }

    @LuaWhitelist
    public FiguraVec2 getUV() {
        return this.customization.uvMatrix.apply(0d, 0d);
    }

    @LuaWhitelist
    public void setUV(double u, double v) {
        setUV(FiguraVec2.oneUse(u, v));
    }

    @LuaWhitelist
    public void setUV(@LuaNotNil FiguraVec2 uv) {
        this.customization.uvMatrix.reset();
        this.customization.uvMatrix.translate(uv.x % 1, uv.y % 1);
    }

    @LuaWhitelist
    public FiguraModelPart uv(@LuaNotNil FiguraVec2 uv) {
        setUV(uv);
        return this;
    }

    @LuaWhitelist
    public FiguraModelPart uv(double u, double v) {
        return uv(FiguraVec2.oneUse(u, v));
    }

    @LuaWhitelist
    public FiguraVec2 getUVPixels() {
        if (this.textureWidth == -1 || this.textureHeight == -1) {
            if (this.customization.partType == PartCustomization.PartType.GROUP)
                throw new LuaError("Cannot call getUVPixels on groups!");
            else
                throw new LuaError("Cannot call getUVPixels on parts with multiple texture sizes!");
        }

        return getUV().mul(this.textureWidth, this.textureHeight);
    }

    @LuaWhitelist
    public void setUVPixels(double u, double v) {
        setUVPixels(FiguraVec2.oneUse(u, v));
    }

    @LuaWhitelist
    public void setUVPixels(@LuaNotNil FiguraVec2 uv) {
        if (this.textureWidth == -1 || this.textureHeight == -1) {
            if (this.customization.partType == PartCustomization.PartType.GROUP)
                throw new LuaError("Cannot call setUVPixels on groups!");
            else
                throw new LuaError("Cannot call setUVPixels on parts with multiple texture sizes!");
        }

        this.customization.uvMatrix.reset();
        uv.div(this.textureWidth, this.textureHeight);
        this.customization.uvMatrix.translate(uv.x, uv.y);
    }

    @LuaWhitelist
    public FiguraModelPart uvPixels(double u, double v) {
        setUVPixels(FiguraVec2.oneUse(u, v));
        return this;
    }

    @LuaWhitelist
    public FiguraModelPart uvPixels(@LuaNotNil FiguraVec2 uv) {
        setUVPixels(uv);
        return this;
    }

    @LuaWhitelist
    public FiguraMat3 getUVMatrix() {
        return this.customization.uvMatrix;
    }

    @LuaWhitelist
    public void setUVMatrix(@LuaNotNil FiguraMat3 matrix) {
        this.customization.uvMatrix.set(matrix);
    }

    @LuaWhitelist
    public FiguraModelPart uvMatrix(@LuaNotNil FiguraMat3 matrix) {
        setUVMatrix(matrix);
        return this;
    }

    @LuaWhitelist
    public FiguraVec3 getColor() {
        return this.customization.color.copy();
    }

    @LuaWhitelist
    public void setColor(Double r, Double g, Double b) {
        setColor(LuaUtils.freeVec3("setColor", r, g, b, 1, 1, 1));
    }

    @LuaWhitelist
    public void setColor(@LuaNotNil FiguraVec3 color) {
        this.customization.color = color.copy();
    }

    @LuaWhitelist
    public FiguraModelPart color(Double r, Double g, Double b) {
        return color(LuaUtils.freeVec3("setColor", r, g, b, 1, 1, 1));
    }

    public FiguraModelPart color(@LuaNotNil FiguraVec3 color) {
        setColor(color);
        return this;
    }

    @LuaWhitelist
    public Float getOpacity() {
        return this.customization.alpha;
    }

    @LuaWhitelist
    public void setOpacity(Float opacity) {
        this.customization.alpha = opacity;
    }

    @LuaWhitelist
    public FiguraModelPart opacity(Float opacity) {
        setOpacity(opacity);
        return this;
    }

    @LuaWhitelist
    public FiguraVec2 getLight() {
        Integer light = this.customization.light;
        return light == null ? null : FiguraVec2.of(LightTexture.block(light), LightTexture.sky(light));
    }

    @LuaWhitelist
    public void setLight(@LuaNotNil FiguraVec2 light) {
        setLight(light.x, light.y);
    }

    @LuaWhitelist
    public void setLight(Double blockLight, double skyLight) {
        this.customization.light = blockLight == null ? null : LightTexture.pack(blockLight.intValue(), (int) skyLight);
    }

    @LuaWhitelist
    public FiguraModelPart light(@LuaNotNil FiguraVec2 light) {
        return light(light.x, light.y);
    }

    @LuaWhitelist
    public FiguraModelPart light(Double light, double skyLight) {
        setLight(light, skyLight);
        return this;
    }

    @LuaWhitelist
    public FiguraVec2 getOverlay() {
        Integer overlay = this.customization.overlay;
        return overlay == null ? null : FiguraVec2.of(overlay & 0xFFFF, overlay >> 16);
    }

    @LuaWhitelist
    public void setOverlay(@LuaNotNil FiguraVec2 overlay) {
        setOverlay(overlay.x, overlay.y);
    }

    @LuaWhitelist
    public void setOverlay(Double whiteOverlay, double hurtOverlay) {
        this.customization.overlay = whiteOverlay == null ? null : OverlayTexture.pack(whiteOverlay.intValue(), (int) hurtOverlay);
    }

    @LuaWhitelist
    public FiguraModelPart overlay(@LuaNotNil FiguraVec2 overlay) {
        return light(overlay.x, overlay.y);
    }

    @LuaWhitelist
    public FiguraModelPart overlay(Double whiteOverlay, double hurtOverlay) {
        setOverlay(whiteOverlay, hurtOverlay);
        return this;
    }

    @LuaWhitelist
    public String getParentType() {
        return this.parentType == null ? null : this.parentType.name();
    }

    @LuaWhitelist
    public void setParentType(@LuaNotNil String parent) {
        this.parentType = ParentType.get(parent);
        this.customization.needsMatrixRecalculation = true;
    }

    @LuaWhitelist
    public FiguraModelPart parentType(@LuaNotNil String parent) {
        setParentType(parent);
        return this;
    }

    @LuaWhitelist
    public String getType() {
        return this.customization.partType.name();
    }

    @LuaWhitelist
    public boolean overrideVanillaRot() {
        return (animationOverride & 1) == 1;
    }

    @LuaWhitelist
    public boolean overrideVanillaPos() {
        return (animationOverride & 2) == 2;
    }

    @LuaWhitelist
    public boolean overrideVanillaScale() {
        return (animationOverride & 4) == 4;
    }

    @LuaWhitelist
    public RenderTask newText(@LuaNotNil String taskName) {
        RenderTask task = new TextTask(name);
        this.renderTasks.put(taskName, task);
        return task;
    }

    @LuaWhitelist
    public RenderTask newItem(@LuaNotNil String taskName) {
        RenderTask task = new ItemTask(name);
        this.renderTasks.put(taskName, task);
        return task;
    }

    @LuaWhitelist
    public RenderTask newBlock(@LuaNotNil String taskName) {
        RenderTask task = new BlockTask(name);
        this.renderTasks.put(taskName, task);
        return task;
    }

    @LuaWhitelist
    public Map<String, RenderTask> getTask() {
        return renderTasks;
    }

    @LuaWhitelist
    public RenderTask getTask(@LuaNotNil String taskName) {
        return this.renderTasks.get(taskName);
    }

    @LuaWhitelist
    public FiguraModelPart removeTask() {
        this.renderTasks.clear();
        return this;
    }

    @LuaWhitelist
    public FiguraModelPart removeTask(@LuaNotNil String taskName) {
        this.renderTasks.remove(taskName);
        return this;
    }

    //-- METAMETHODS --//
    @LuaWhitelist
    public Object __index(String key) {
        if (key == null) return null;

        if (this.childCache.containsKey(key))
            return this.childCache.get(key);

        for (FiguraModelPart child : this.children)
            if (child.name.equals(key)) {
                this.childCache.put(key, child);
                return child;
            }

        this.childCache.put(key, null);
        return null;
    }

    @Override
    public int compareTo(@NotNull FiguraModelPart o) {
        if (this.isChildOf(o))
            return 1;
        else if (o.isChildOf(this))
            return -1;
        else
            return 0;
    }

    @Override
    public String toString() {
        return name + " (ModelPart)";
    }
}
