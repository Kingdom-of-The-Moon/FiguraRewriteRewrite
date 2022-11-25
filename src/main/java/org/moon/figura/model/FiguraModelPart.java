package org.moon.figura.model;

import com.mojang.datafixers.util.Pair;
import net.minecraft.client.renderer.LightTexture;
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
    public void setPos(Double x, Double y, Double z){
        setPos(LuaUtils.freeVec3("setPos", x, y, z));
    }

    @LuaWhitelist
    public void setPos(@LuaNotNil FiguraVec3 pos) {
        this.customization.setPos(pos);
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
    public void setRot(Double x, Double y, Double z){
        setRot(LuaUtils.freeVec3("setRot", x, y, z));
    }

    @LuaWhitelist
    public void setRot(@LuaNotNil FiguraVec3 rot) {
        this.customization.setRot(rot);
    }

    @LuaWhitelist
    public FiguraVec3 getOffsetRot() {
        return this.customization.getOffsetRot();
    }

    @LuaWhitelist
    public void offsetRot(Double x, Double y, Double z){
        offsetRot(LuaUtils.freeVec3("offsetRot", x, y, z));
    }

    @LuaWhitelist
    public void offsetRot(@LuaNotNil FiguraVec3 offsetRot) {
        this.customization.offsetRot(offsetRot);
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
    public void setScale(Double x, Double y, Double z){
        setScale(LuaUtils.freeVec3("setScale", x, y, z, 1, 1, 1));
    }

    @LuaWhitelist
    public void setScale(@LuaNotNil FiguraVec3 scale) {
        this.customization.setScale(scale);
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
    public void setPivot (Double x, Double y, Double z){
        setPivot(LuaUtils.freeVec3("setPivot", x, y, z));
    }

    @LuaWhitelist
    public void setPivot(@LuaNotNil FiguraVec3 pivot) {
        this.customization.setPivot(pivot);
    }

    @LuaWhitelist
    public FiguraVec3 getOffsetPivot() {
        return this.customization.getOffsetPivot();
    }

    @LuaWhitelist
    public void offsetPivot(Double x, Double y, Double z){
        offsetPivot(LuaUtils.freeVec3("offsetPivot", x, y, z));
    }

    @LuaWhitelist
    public void offsetPivot(@LuaNotNil FiguraVec3 vec) {
        this.customization.offsetPivot(vec);
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
    public String getPrimaryRenderType() {
        RenderTypes renderType = this.customization.getPrimaryRenderType();
        return renderType == null ? null : renderType.name();
    }

    @LuaWhitelist
    public String getSecondaryRenderType() {
        RenderTypes renderType = this.customization.getSecondaryRenderType();
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
    public void setSecondaryRenderType(String renderType) {
        try {
            this.customization.setSecondaryRenderType(renderType == null ? null : RenderTypes.valueOf(renderType.toUpperCase()));
        } catch (Exception ignored) {
            throw new LuaError("Illegal RenderType: \"" + renderType + "\".");
        }
    }

    @LuaWhitelist
    public void setPrimaryTexture(String textureType){
        setPrimaryTextureDumb(textureType, null);
    }

    @LuaWhitelist
    public void setPrimaryTexture(@LuaNotNil String textureType, @LuaNotNil String path){
        setPrimaryTextureDumb(textureType, path);
    }

    @LuaWhitelist
    public void setPrimaryTexture(@LuaNotNil String textureType, @LuaNotNil FiguraTexture texture){
        setPrimaryTextureDumb(textureType, texture);
    }

    public void setPrimaryTextureDumb(String type, Object x) {
        try {
            this.customization.primaryTexture = type == null ? null : Pair.of(FiguraTextureSet.OverrideType.valueOf(type.toUpperCase()), x);
        } catch (Exception ignored) {
            throw new LuaError("Invalid texture override type: " + type);
        }
    }


    @LuaWhitelist
    public void setSecondaryTexture(String textureType){
        setSecondaryTextureDumb(textureType, null);
    }

    @LuaWhitelist
    public void setSecondaryTexture(@LuaNotNil String textureType, @LuaNotNil String path){
        setSecondaryTextureDumb(textureType, path);
    }

    @LuaWhitelist
    public void setSecondaryTexture(@LuaNotNil String textureType, @LuaNotNil FiguraTexture texture){
        setSecondaryTextureDumb(textureType, texture);
    }

    public void setSecondaryTextureDumb(String type, Object x) {
        try {
            this.customization.secondaryTexture = type == null ? null : Pair.of(FiguraTextureSet.OverrideType.valueOf(type.toUpperCase()), x);
        } catch (Exception ignored) {
            throw new LuaError("Invalid texture override type: " + type);
        }
    }

    @LuaWhitelist
    public List<FiguraTexture> getTextures() {
        List<FiguraTexture> list = new ArrayList<>();

        for (FiguraTextureSet set : textures) {
            FiguraTexture texture = set.mainTex;
            if (texture != null)
                list.add(texture);

            texture = set.emissiveTex;
            if (texture != null)
                list.add(texture);
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
    public void setUV(Double u, Double v){
        setUV(LuaUtils.freeVec2("setUV", u, v));
    }

    @LuaWhitelist
    public void setUV(@LuaNotNil FiguraVec2 uv) {
        this.customization.uvMatrix.reset();
        this.customization.uvMatrix.translate(uv.x, uv.y);
    }

    @LuaWhitelist
    public FiguraVec2 getUV() {
        return this.customization.uvMatrix.apply(0d, 0d);
    }

    @LuaWhitelist
    public void setUVPixels(Double u, Double v){
        setUVPixels(LuaUtils.freeVec2("setUVPixels", u, v));
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
    public void setUVMatrix(@LuaNotNil FiguraMat3 matrix) {
        this.customization.uvMatrix.set(matrix);
    }

    @LuaWhitelist
    public FiguraMat3 getUVMatrix() {
        return this.customization.uvMatrix;
    }

    @LuaWhitelist
    public void setColor(Double r, Double g, Double b){
        setColor(LuaUtils.freeVec3("setColor", r, g, b, 1, 1, 1));
    }

    @LuaWhitelist
    public void setColor(@LuaNotNil FiguraVec3 color) {
        this.customization.color = color.copy();
    }

    @LuaWhitelist
    public FiguraVec3 getColor() {
        return this.customization.color.copy();
    }

    @LuaWhitelist
    public void setOpacity(Float opacity) {
        this.customization.alpha = opacity;
    }

    @LuaWhitelist
    public Float getOpacity() {
        return this.customization.alpha;
    }

    @LuaWhitelist
    public void setLight(){
        this.customization.light = null;
    }

    @LuaWhitelist
    public void setLight(@LuaNotNil FiguraVec2 light){
        setLight(light.x, light.y);
    }

    @LuaWhitelist
    public void setLight(double blockLight, double skyLight) {
        this.customization.light = LightTexture.pack((int) blockLight, (int) skyLight);
    }

    @LuaWhitelist
    public FiguraVec2 getLight() {
        Integer light = this.customization.light;
        return light == null ? null : FiguraVec2.of(LightTexture.block(light), LightTexture.sky(light));
    }

    @LuaWhitelist
    public void setParentType(@LuaNotNil String parent) {
        this.parentType = ParentType.get(parent);
        this.customization.needsMatrixRecalculation = true;
    }

    @LuaWhitelist
    public String getParentType() {
        return this.parentType == null ? null : this.parentType.name();
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
        RenderTask task = new TextTask();
        this.renderTasks.put(taskName, task);
        return task;
    }

    @LuaWhitelist
    public RenderTask newItem(@LuaNotNil String taskName) {
        RenderTask task = new ItemTask();
        this.renderTasks.put(taskName, task);
        return task;
    }

    @LuaWhitelist
    public RenderTask newBlock(@LuaNotNil String taskName) {
        RenderTask task = new BlockTask();
        this.renderTasks.put(taskName, task);
        return task;
    }

    @LuaWhitelist
    public Map<String, RenderTask> getTask(){
        return renderTasks;
    }

    @LuaWhitelist
    public RenderTask getTask(@LuaNotNil String taskName) {
        return this.renderTasks.get(taskName);
    }

    @LuaWhitelist
    public void removeTask(){
        this.renderTasks.clear();
    }

    @LuaWhitelist
    public void removeTask(@LuaNotNil String taskName) {
        this.renderTasks.remove(taskName);
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
    public int compareTo(FiguraModelPart o) {
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
