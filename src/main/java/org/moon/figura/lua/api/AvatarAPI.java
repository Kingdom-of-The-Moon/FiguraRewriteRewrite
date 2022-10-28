package org.moon.figura.lua.api;

import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.moon.figura.avatar.Avatar;
import org.moon.figura.lua.LuaNotNil;
import org.moon.figura.lua.LuaWhitelist;
import org.moon.figura.lua.docs.LuaTypeDoc;
import org.moon.figura.math.vector.FiguraVec3;
import org.moon.figura.trust.Trust;
import org.moon.figura.utils.ColorUtils;
import org.moon.figura.utils.LuaUtils;

@LuaWhitelist
@LuaTypeDoc(
        name = "AvatarAPI",
        value = "avatar"
)
public class AvatarAPI {

    private final Avatar avatar;
    public final LuaTable storedStuff = new LuaTable();

    public AvatarAPI(Avatar avatar) {
        this.avatar = avatar;
    }

    @LuaWhitelist
    public void store(@LuaNotNil String key, LuaValue value) {
        storedStuff.set(key, value == null ? LuaValue.NIL : value);
    }

    @LuaWhitelist
    public int getCurrentInstructions() {
        return avatar.luaRuntime == null ? 0 : avatar.luaRuntime.getInstructions();
    }

    @LuaWhitelist
    public String getColor() {
        return avatar.color;
    }

    @LuaWhitelist
    public void setColor(Double r, Double g, Double b){
        setColor(LuaUtils.freeVec3("setColor", r, g, b, 1, 1, 1));
    }

    @LuaWhitelist
    public void setColor(FiguraVec3 color) {
        avatar.color = ColorUtils.rgbToHex(color);
    }

    @LuaWhitelist
    public String getVersion() {
        return avatar.version.toString();
    }

    @LuaWhitelist
    public String getAuthors() {
        return avatar.authors;
    }

    @LuaWhitelist
    public String getName() {
        return avatar.name;
    }

    @LuaWhitelist
    public double getSize() {
        return avatar.fileSize;
    }

    @LuaWhitelist
    public boolean hasTexture() {
        return avatar.hasTexture;
    }

    @LuaWhitelist
    public boolean hasScriptError() {
        //useless I know
        return avatar.scriptError;
    }

    @LuaWhitelist
    public int getInitCount() {
        return avatar.init.pre;
    }

    @LuaWhitelist
    public int getEntityInitCount() {
        return avatar.init.post;
    }

    @LuaWhitelist
    public int getMaxInitCount() {
        return avatar.trust.get(Trust.INIT_INST);
    }

    @LuaWhitelist
    public int getTickCount() {
        return avatar.tick.getTotal();
    }

    @LuaWhitelist
    public int getMaxTickCount() {
        return avatar.trust.get(Trust.TICK_INST);
    }

    @LuaWhitelist
    public int getRenderCount() {
        return avatar.render.getTotal();
    }

    @LuaWhitelist
    public int getMaxRenderCount() {
        return avatar.trust.get(Trust.RENDER_INST);
    }

    @LuaWhitelist
    public int getWorldTickCount() {
        return avatar.worldTick.getTotal();
    }

    @LuaWhitelist
    public int getMaxWorldTickCount() {
        return avatar.trust.get(Trust.WORLD_TICK_INST);
    }

    @LuaWhitelist
    public int getWorldRenderCount() {
        return avatar.worldRender.getTotal();
    }

    @LuaWhitelist
    public int getMaxWorldRenderCount() {
        return avatar.trust.get(Trust.WORLD_RENDER_INST);
    }

    @LuaWhitelist
    public int getComplexity() {
        return avatar.complexity.pre;
    }

    @LuaWhitelist
    public int getMaxComplexity() {
        return avatar.trust.get(Trust.COMPLEXITY);
    }

    @LuaWhitelist
    public int getRemainingParticles() {
        return avatar.particlesRemaining.peek();
    }

    @LuaWhitelist
    public int getMaxParticles() {
        return avatar.trust.get(Trust.PARTICLES);
    }

    @LuaWhitelist
    public int getRemainingSounds() {
        return avatar.soundsRemaining.peek();
    }

    @LuaWhitelist
    public int getMaxSounds() {
        return avatar.trust.get(Trust.SOUNDS);
    }

    @LuaWhitelist
    public int getVolume() {
        return avatar.trust.get(Trust.VOLUME);
    }

    @LuaWhitelist
    public int getAnimationComplexity() {
        return avatar.animationComplexity;
    }

    @LuaWhitelist
    public int getMaxAnimationComplexity() {
        return avatar.trust.get(Trust.BB_ANIMATIONS);
    }

    @LuaWhitelist
    public boolean canEditVanillaModel() {
        Trust trust = Trust.VANILLA_MODEL_EDIT;
        return trust.asBoolean(avatar.trust.get(trust));
    }

    @LuaWhitelist
    public boolean canEditNameplate() {
        Trust trust = Trust.NAMEPLATE_EDIT;
        return trust.asBoolean(avatar.trust.get(trust));
    }

    @LuaWhitelist
    public boolean canRenderOffscreen() {
        Trust trust = Trust.OFFSCREEN_RENDERING;
        return trust.asBoolean(avatar.trust.get(trust));
    }

    @LuaWhitelist
    public boolean canUseCustomSounds() {
        Trust trust = Trust.CUSTOM_SOUNDS;
        return trust.asBoolean(avatar.trust.get(trust));
    }

    @Override
    public String toString() {
        return "AvatarAPI";
    }
}
