package org.moon.figura.model.rendertasks;

import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.OverlayTexture;
import org.moon.figura.lua.LuaNotNil;
import org.moon.figura.lua.LuaWhitelist;
import org.moon.figura.lua.docs.LuaMethodDoc;
import org.moon.figura.lua.docs.LuaTypeDoc;
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
    protected final FiguraVec3 pos = FiguraVec3.of();
    protected final FiguraVec3 rot = FiguraVec3.of();
    protected final FiguraVec3 scale = FiguraVec3.of(1, 1, 1);

    public RenderTask(String name) {
        this.name = name;
    }

    //Return true if something was rendered, false if the function cancels for some reason
    public abstract boolean render(PartCustomization.Stack stack, MultiBufferSource buffer, int light, int overlay);
    public abstract int getComplexity();
    private static final PartCustomization dummyCustomization = PartCustomization.of();
    public void pushOntoStack(PartCustomization.Stack stack) {
        dummyCustomization.setScale(scale);
        dummyCustomization.setPos(pos);
        dummyCustomization.setRot(rot);
        dummyCustomization.recalculate();
        stack.push(dummyCustomization);
    }

    @LuaWhitelist
    public boolean isEnabled() {
        return this.enabled;
    }

    @LuaWhitelist
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @LuaWhitelist
    @LuaMethodDoc("setEnabled")
    public RenderTask enabled(boolean enabled) {
        setEnabled(enabled);
        return this;
    }

    @LuaWhitelist
    public FiguraVec2 getLight() {
        return light == null ? null : FiguraVec2.of(LightTexture.block(light), LightTexture.sky(light));
    }

    @LuaWhitelist
    public void setLight(@LuaNotNil FiguraVec2 light) {
        setLight((int) light.x, (int) light.y);
    }

    @LuaWhitelist
    public void setLight(int blockLight, int skyLight) {
        this.light = LightTexture.pack(blockLight, skyLight);
    }

    @LuaWhitelist
    public RenderTask light(@LuaNotNil FiguraVec2 light) {
        return light((int) light.x, (int) light.y);
    }

    @LuaWhitelist
    @LuaMethodDoc("setLight")
    public RenderTask light(int blockLight, int skyLight) {
        setLight(blockLight, skyLight);
        return this;
    }

    @LuaWhitelist
    public FiguraVec2 getOverlay() {
        return overlay == null ? null : FiguraVec2.of(overlay & 0xFFFF, overlay >> 16);
    }

    @LuaWhitelist
    public void setOverlay(FiguraVec2 overlay) {
        setOverlay((int) overlay.x, (int) overlay.y);
    }

    @LuaWhitelist
    public void setOverlay(int whiteOverlay, int hurtOverlay) {
        this.overlay = OverlayTexture.pack(whiteOverlay, hurtOverlay);
    }

    @LuaWhitelist
    public RenderTask overlay(FiguraVec2 overlay) {
        return overlay((int) overlay.x, (int) overlay.y);
    }

    @LuaWhitelist
    @LuaMethodDoc("setOverlay")
    public RenderTask overlay(int whiteOverlay, int hurtOverlay) {
        setOverlay(whiteOverlay, hurtOverlay);
        return this;
    }

    @LuaWhitelist
    public FiguraVec3 getPos() {
        return this.pos;
    }

    @LuaWhitelist
    public void setPos(double x, double y, double z) {
        setPos(FiguraVec3.oneUse(x, y, z));
    }

    @LuaWhitelist
    public void setPos(@LuaNotNil FiguraVec3 pos) {
        this.pos.set(pos);
    }

    @LuaWhitelist
    @LuaMethodDoc("setPos")
    public RenderTask pos(double x, double y, double z) {
        return pos(FiguraVec3.oneUse(x, y, z));
    }

    @LuaWhitelist
    public RenderTask pos(@LuaNotNil FiguraVec3 pos) {
        setPos(pos);
        return this;
    }

    @LuaWhitelist
    public FiguraVec3 getRot() {
        return this.rot;
    }

    @LuaWhitelist
    public void setRot(double x, double y, double z) {
        setRot(FiguraVec3.oneUse(x, y, z));
    }

    @LuaWhitelist
    public void setRot(@LuaNotNil FiguraVec3 rot) {
        this.rot.set(rot);
    }

    @LuaWhitelist
    @LuaMethodDoc("setRot")
    public RenderTask rot(double x, double y, double z) {
        return rot(FiguraVec3.oneUse(x, y, z));
    }

    @LuaWhitelist
    public RenderTask rot(@LuaNotNil FiguraVec3 rot) {
        setRot(rot);
        return this;
    }

    @LuaWhitelist
    public FiguraVec3 getScale() {
        return this.scale;
    }

    @LuaWhitelist
    public void setScale (Double x, Double y, Double z) {
        setScale(LuaUtils.freeVec3("setScale", x, y, z, 1, 1, 1));
    }

    @LuaWhitelist
    public void setScale(@LuaNotNil FiguraVec3 scale) {
        this.scale.set(scale);
    }

    @LuaWhitelist
    public RenderTask scale(Double x, Double y, Double z) {
        return scale(LuaUtils.freeVec3("scale", x, y, z, 1, 1, 1));
    }

    @LuaWhitelist
    @LuaMethodDoc("setScale")
    public RenderTask scale(@LuaNotNil FiguraVec3 scale) {
        setScale(scale);
        return this;
    }

    @Override
    public String toString() {
        return name + " (Render Task)";
    }
}
