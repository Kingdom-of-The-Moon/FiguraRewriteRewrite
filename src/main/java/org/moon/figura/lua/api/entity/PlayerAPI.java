package org.moon.figura.lua.api.entity;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.PlayerModelPart;
import org.luaj.vm2.LuaError;
import org.moon.figura.lua.LuaWhitelist;
import org.moon.figura.lua.docs.LuaTypeDoc;

@LuaWhitelist
@LuaTypeDoc(
        name = "PlayerAPI",
        value = "player"
)
public class PlayerAPI extends LivingEntityAPI<Player> {
    public PlayerAPI(Player entity) {
        super(entity);
    }

    private String cachedModelType;

    @LuaWhitelist
    public int getFood() {
        checkEntity();
        return entity.getFoodData().getFoodLevel();
    }

    @LuaWhitelist
    public float getSaturation() {
        checkEntity();
        return entity.getFoodData().getSaturationLevel();
    }

    @LuaWhitelist
    public float getExperienceProgress() {
        checkEntity();
        return entity.experienceProgress;
    }

    @LuaWhitelist
    public float getExperienceLevel() {
        checkEntity();
        return entity.experienceLevel;
    }

    @LuaWhitelist
    public boolean isFlying() {
        checkEntity();
        return entity.getAbilities().flying;
    }

    @LuaWhitelist
    public String getModelType() {
        checkEntity();
        if (cachedModelType == null) {
            if (Minecraft.getInstance().player == null)
                return null;

            PlayerInfo info = Minecraft.getInstance().player.connection.getPlayerInfo(entity.getUUID());
            if (info == null)
                return null;

            cachedModelType = info.getModelName().toUpperCase();
        }
        return cachedModelType;
    }

    @LuaWhitelist
    public String getGamemode() {
        checkEntity();
        if (Minecraft.getInstance().player == null)
            return null;

        PlayerInfo info = Minecraft.getInstance().player.connection.getPlayerInfo(entity.getUUID());
        if (info == null)
            return null;

        return info.getGameMode() == null ? null : info.getGameMode().getName().toUpperCase();
    }

    @LuaWhitelist
    public boolean isSkinLayerVisible(String part) {
        checkEntity();
        try {
            if (part.equalsIgnoreCase("left_pants") || part.equalsIgnoreCase("right_pants"))
                part += "_leg";
            return entity.isModelPartShown(PlayerModelPart.valueOf(part.toUpperCase()));
        } catch (Exception ignored) {
            throw new LuaError("Invalid player model part: " + part);
        }
    }

    @Override
    public String toString() {
        checkEntity();
        return entity.getName().getString() + " (Player)";
    }
}
