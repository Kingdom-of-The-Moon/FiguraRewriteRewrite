package org.moon.figura.lua.api.entity;

import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.PlayerModelPart;
import net.minecraft.world.level.GameType;
import org.luaj.vm2.LuaError;
import org.moon.figura.lua.LuaNotNil;
import org.moon.figura.lua.LuaWhitelist;
import org.moon.figura.lua.docs.LuaTypeDoc;
import org.moon.figura.utils.EntityUtils;

@LuaWhitelist
@LuaTypeDoc(
        name = "PlayerAPI",
        value = "player"
)
public class PlayerAPI extends LivingEntityAPI<Player> {

    private PlayerInfo playerInfo;

    public PlayerAPI(Player entity) {
        super(entity);
    }

    private boolean checkPlayerInfo() {
        if (playerInfo != null)
            return true;

        PlayerInfo info = EntityUtils.getPlayerInfo(entity.getUUID());
        if (info == null)
            return false;

        playerInfo = info;
        return true;
    }

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
        return checkPlayerInfo() ? playerInfo.getModelName().toUpperCase() : DefaultPlayerSkin.getSkinModelName(entity.getUUID());
    }

    @LuaWhitelist
    public String getGamemode() {
        checkEntity();
        if (!checkPlayerInfo())
            return null;

        GameType gamemode = playerInfo.getGameMode();
        return gamemode == null ? null : gamemode.getName().toUpperCase();
    }

    @LuaWhitelist
    public boolean hasCape() {
        checkEntity();
        return checkPlayerInfo() && playerInfo.isCapeLoaded();
    }

    @LuaWhitelist
    public boolean hasSkin() {
        checkEntity();
        return checkPlayerInfo() && playerInfo.isSkinLoaded();
    }

    @LuaWhitelist
    public boolean isSkinLayerVisible(@LuaNotNil String part) {
        checkEntity();
        try {
            if (part.equalsIgnoreCase("left_pants") || part.equalsIgnoreCase("right_pants"))
                part += "_leg";
            return entity.isModelPartShown(PlayerModelPart.valueOf(part.toUpperCase()));
        } catch (Exception ignored) {
            throw new LuaError("Invalid player model part: " + part);
        }
    }

    @LuaWhitelist
    public boolean isFishing() {
        checkEntity();
        return entity.fishing != null;
    }

    @LuaWhitelist
    public float getChargedAttackDelay() {
        checkEntity();
        return entity.getCurrentItemAttackStrengthDelay();
    }

    @Override
    public String toString() {
        checkEntity();
        return entity.getName().getString() + " (Player)";
    }
}
