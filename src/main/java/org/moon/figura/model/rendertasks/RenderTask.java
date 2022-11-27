package org.moon.figura.model.rendertasks;

import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import org.moon.figura.lua.LuaNotNil;
import net.minecraft.client.renderer.texture.OverlayTexture;
import org.moon.figura.lua.LuaWhitelist;
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

    protected boolean enabled = true;
    protected Integer light = null;
    protected Integer overlay = null;
    protected final FiguraVec3 pos = FiguraVec3.of();
    protected final FiguraVec3 rot = FiguraVec3.of();
    protected final FiguraVec3 scale = FiguraVec3.of(1, 1, 1);

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
    public RenderTask enabled(boolean enabled) {
        this.enabled = enabled;
        return this;
    }

    @LuaWhitelist
    public FiguraVec2 getLight() {
        return light == null ? null : FiguraVec2.of(LightTexture.block(light), LightTexture.sky(light));
    }

    @LuaWhitelist
    public RenderTask light(FiguraVec2 light){
        return light((int) light.x, (int) light.y);
    }

    @LuaWhitelist
    public RenderTask light(int blockLiht, int skyLight) {
        this.light = LightTexture.pack(blockLiht, skyLight);
        return this;
    }

    @LuaWhitelist
    public FiguraVec2 getOverlay() {
        return light == null ? null : FiguraVec2.of(LightTexture.block(light), LightTexture.sky(light));
    }

    @LuaWhitelist
    public RenderTask overlay(FiguraVec2 overlay){
        return overlay((int) overlay.x, (int) overlay.y);
    }

    @LuaWhitelist
    public RenderTask overlay(int whiteOverlay, int hurtOverlay) {
        this.light = OverlayTexture.pack(whiteOverlay, hurtOverlay);
        return this;
    }

    @LuaWhitelist
    public FiguraVec3 getPos() {
        return this.pos;
    }

    @LuaWhitelist
    public RenderTask pos(Double x, Double y, Double z){
        return pos(LuaUtils.freeVec3("pos", x, y, z));
    }

    @LuaWhitelist
    public RenderTask pos(@LuaNotNil FiguraVec3 pos) {
        this.pos.set(pos);
        return this;
    }

    @LuaWhitelist
    public FiguraVec3 getRot() {
        return this.rot;
    }

    @LuaWhitelist
    public RenderTask rot(Double x, Double y, Double z){
        return rot(LuaUtils.freeVec3("rot", x, y, z));
    }

    @LuaWhitelist
    public RenderTask rot(@LuaNotNil FiguraVec3 rot) {
        this.rot.set(rot);
        return this;
    }

    @LuaWhitelist
    public FiguraVec3 getScale() {
        return this.scale;
    }

    @LuaWhitelist
    public RenderTask scale(Double x, Double y, Double z){
        return scale(LuaUtils.freeVec3("scale", x, y, z, 1, 1, 1));
    }

    @LuaWhitelist
    public RenderTask scale(@LuaNotNil FiguraVec3 scale) {
        this.scale.set(scale);
        return this;
    }

    @Override
    public String toString() {
        return "Render Task";
    }
}
