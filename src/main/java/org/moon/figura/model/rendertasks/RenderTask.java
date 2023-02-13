package org.moon.figura.model.rendertasks;

import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.OverlayTexture;
import org.moon.figura.lua.LuaNotNil;
import org.moon.figura.lua.LuaWhitelist;
import org.moon.figura.lua.docs.LuaMethodDoc;
import org.moon.figura.lua.docs.LuaTypeDoc;
import org.moon.figura.math.matrix.FiguraMat3;
import org.moon.figura.math.matrix.FiguraMat4;
import org.moon.figura.math.vector.FiguraVec2;
import org.moon.figura.math.vector.FiguraVec3;
import org.moon.figura.model.PartCustomization;
import org.moon.figura.utils.LuaUtils;

@LuaWhitelist
@LuaTypeDoc(
        name = "RenderTask",
        value = "render_task"
)
public abstract class RenderTask {

    protected final String name;

    protected boolean enabled = true;
    protected Integer light = null;
    protected Integer overlay = null;

    private final PartCustomization customization = PartCustomization.of();

    public RenderTask(String name) {
        this.name = name;
    }

    //Return true if something was rendered, false if the function cancels for some reason
    public abstract boolean render(PartCustomization.Stack stack, MultiBufferSource buffer, int light, int overlay);
    public abstract int getComplexity();

    public void pushOntoStack(PartCustomization.Stack stack) {
        customization.recalculate();
        stack.push(customization);
    }


    // -- lua stuff -- //


    @LuaWhitelist
    public String getName() {
        return this.name;
    }

    @LuaWhitelist
    public boolean isEnabled() {
        return this.enabled;
    }

    @LuaWhitelist
    @LuaMethodDoc("enabled")
    public RenderTask setEnabled(boolean enabled) {
        this.enabled = enabled;
        return this;
    }

    @LuaWhitelist
    public FiguraVec2 getLight() {
        return light == null ? null : FiguraVec2.of(LightTexture.block(light), LightTexture.sky(light));
    }

    @LuaWhitelist
    public RenderTask setLight(@LuaNotNil FiguraVec2 light) {
        return setLight((int) light.x, (int) light.y);
    }

    @LuaWhitelist
    @LuaMethodDoc("light")
    public RenderTask setLight(Integer blockLight, int skyLight) {
        this.light = blockLight == null ? null : LightTexture.pack(blockLight, skyLight);
        return this;
    }

    @LuaWhitelist
    public FiguraVec2 getOverlay() {
        return overlay == null ? null : FiguraVec2.of(overlay & 0xFFFF, overlay >> 16);
    }

    @LuaWhitelist
    public RenderTask setOverlay(@LuaNotNil FiguraVec2 overlay) {
        return setOverlay((int) overlay.x, (int) overlay.y);
    }

    @LuaWhitelist
    @LuaMethodDoc("overlay")
    public RenderTask setOverlay(Integer whiteOverlay, int hurtOverlay) {
        this.overlay = whiteOverlay == null ? null : OverlayTexture.pack(whiteOverlay, hurtOverlay);
        return this;
    }

    @LuaWhitelist
    public FiguraVec3 getPos() {
        return this.customization.getPos();
    }

    @LuaWhitelist
    public RenderTask setPos(double x, double y, double z) {
        return setPos(FiguraVec3.oneUse(x, y, z));
    }

    @LuaWhitelist
    @LuaMethodDoc("pos")
    public RenderTask setPos(@LuaNotNil FiguraVec3 pos) {
        this.customization.setPos(pos);
        return this;
    }

    @LuaWhitelist
    public FiguraVec3 getRot() {
        return this.customization.getRot();
    }

    @LuaWhitelist
    public RenderTask setRot(double x, double y, double z) {
        return setRot(FiguraVec3.oneUse(x, y, z));
    }

    @LuaWhitelist
    @LuaMethodDoc("rot")
    public RenderTask setRot(@LuaNotNil FiguraVec3 rot) {
        this.customization.setRot(rot);
        return this;
    }

    @LuaWhitelist
    public FiguraVec3 getScale() {
        return this.customization.getScale();
    }

    @LuaWhitelist
    public RenderTask setScale (Double x, Double y, Double z) {
        return setScale(LuaUtils.freeVec3("setScale", x, y, z, 1, 1, 1));
    }

    @LuaWhitelist
    @LuaMethodDoc("scale")
    public RenderTask setScale(@LuaNotNil FiguraVec3 scale) {
        this.customization.setScale(scale);
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
    @LuaMethodDoc("matrix")
    public RenderTask setMatrix(@LuaNotNil FiguraMat4 matrix) {
        this.customization.setMatrix(matrix);
        return this;
    }

    @Override
    public String toString() {
        return name + " (Render Task)";
    }
}
