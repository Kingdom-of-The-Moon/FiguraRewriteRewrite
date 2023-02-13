package org.moon.figura.lua.api;

import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.moon.figura.avatar.Avatar;
import org.moon.figura.lua.LuaNotNil;
import org.moon.figura.lua.LuaWhitelist;
import org.moon.figura.lua.docs.LuaMethodDoc;
import org.moon.figura.lua.docs.LuaTypeDoc;
import org.moon.figura.math.vector.FiguraVec3;
import org.moon.figura.permissions.Permissions;
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

    private boolean bool(Permissions permissions) {
        return permissions.asBoolean(avatar.permissions.get(permissions));
    }

    @LuaWhitelist
    public AvatarAPI store(@LuaNotNil String key, LuaValue value) {
        storedStuff.set(key, value == null ? LuaValue.NIL : value);
        return this;
    }

    @LuaWhitelist
    public String getUUID() {
        return avatar.owner.toString();
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
    public AvatarAPI setColor(Double r, Double g, Double b) {
        return setColor(LuaUtils.freeVec3("setColor", r, g, b, 1, 1, 1));
    }

    @LuaWhitelist
    @LuaMethodDoc("color")
    public AvatarAPI setColor(@LuaNotNil FiguraVec3 color) {
        avatar.color = ColorUtils.rgbToHex(color);
        return this;
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
    public String getPermissionLevel() {
        return avatar.permissions.getCategory().name();
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
        return avatar.permissions.get(Permissions.INIT_INST);
    }

    @LuaWhitelist
    public int getTickCount() {
        return avatar.tick.getTotal();
    }

    @LuaWhitelist
    public int getMaxTickCount() {
        return avatar.permissions.get(Permissions.TICK_INST);
    }

    @LuaWhitelist
    public int getRenderCount() {
        return avatar.render.getTotal();
    }

    @LuaWhitelist
    public int getMaxRenderCount() {
        return avatar.permissions.get(Permissions.RENDER_INST);
    }

    @LuaWhitelist
    public int getWorldTickCount() {
        return avatar.worldTick.getTotal();
    }

    @LuaWhitelist
    public int getMaxWorldTickCount() {
        return avatar.permissions.get(Permissions.WORLD_TICK_INST);
    }

    @LuaWhitelist
    public int getWorldRenderCount() {
        return avatar.worldRender.getTotal();
    }

    @LuaWhitelist
    public int getMaxWorldRenderCount() {
        return avatar.permissions.get(Permissions.WORLD_RENDER_INST);
    }

    @LuaWhitelist
    public int getComplexity() {
        return avatar.complexity.pre;
    }

    @LuaWhitelist
    public int getMaxComplexity() {
        return avatar.permissions.get(Permissions.COMPLEXITY);
    }

    @LuaWhitelist
    public int getRemainingParticles() {
        return avatar.particlesRemaining.peek();
    }

    @LuaWhitelist
    public int getMaxParticles() {
        return avatar.permissions.get(Permissions.PARTICLES);
    }

    @LuaWhitelist
    public int getRemainingSounds() {
        return avatar.soundsRemaining.peek();
    }

    @LuaWhitelist
    public int getMaxSounds() {
        return avatar.permissions.get(Permissions.SOUNDS);
    }

    @LuaWhitelist
    public int getVolume() {
        return avatar.permissions.get(Permissions.VOLUME);
    }

    @LuaWhitelist
    public int getAnimationComplexity() {
        return avatar.animationComplexity;
    }

    @LuaWhitelist
    public int getMaxAnimationComplexity() {
        return avatar.permissions.get(Permissions.BB_ANIMATIONS);
    }

    @LuaWhitelist
    public int getMaxTextureSize() {
        return avatar.permissions.get(Permissions.TEXTURE_SIZE);
    }

    @LuaWhitelist
    public boolean canEditVanillaModel() {
        return bool(Permissions.VANILLA_MODEL_EDIT);
    }

    @LuaWhitelist
    public boolean canEditNameplate() {
        return bool(Permissions.NAMEPLATE_EDIT);
    }

    @LuaWhitelist
    public boolean canRenderOffscreen() {
        return bool(Permissions.OFFSCREEN_RENDERING);
    }

    @LuaWhitelist
    public boolean canUseCustomSounds() {
        return bool(Permissions.CUSTOM_SOUNDS);
    }

    @LuaWhitelist
    public boolean canHaveCustomHeads() {
        return bool(Permissions.CUSTOM_HEADS);
    }

    @Override
    public String toString() {
        return "AvatarAPI";
    }
}
