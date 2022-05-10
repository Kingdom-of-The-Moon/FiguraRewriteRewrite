package org.moon.figura.lua.api.entity;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.world.entity.player.Player;
import org.moon.figura.avatars.Avatar;
import org.moon.figura.avatars.AvatarManager;
import org.moon.figura.lua.LuaWhitelist;
import org.moon.figura.lua.docs.LuaFunctionOverload;
import org.moon.figura.lua.docs.LuaMethodDoc;
import org.moon.figura.lua.docs.LuaTypeDoc;

import java.util.UUID;

@LuaWhitelist
@LuaTypeDoc(
        name = "Player",
        description = "player"
)
public class PlayerEntityWrapper extends LivingEntityWrapper<Player> {

    public PlayerEntityWrapper(UUID uuid) {
        super(uuid);
    }

    @LuaWhitelist
    @LuaMethodDoc(
            overloads = @LuaFunctionOverload(
                    argumentTypes = PlayerEntityWrapper.class,
                    argumentNames = "entity"
            ),
            description = "player.get_food"
    )
    public static int getFood(PlayerEntityWrapper entity) {
        return getEntity(entity).getFoodData().getFoodLevel();
    }

    @LuaWhitelist
    @LuaMethodDoc(
            overloads = @LuaFunctionOverload(
                    argumentTypes = PlayerEntityWrapper.class,
                    argumentNames = "entity"
            ),
            description = "player.get_saturation"
    )
    public static float getSaturation(PlayerEntityWrapper entity) {
        return getEntity(entity).getFoodData().getSaturationLevel();
    }

    @LuaWhitelist
    @LuaMethodDoc(
            overloads = @LuaFunctionOverload(
                    argumentTypes = PlayerEntityWrapper.class,
                    argumentNames = "entity"
            ),
            description = "player.get_experience_progress"
    )
    public static float getExperienceProgress(PlayerEntityWrapper entity) {
        return getEntity(entity).experienceProgress;
    }

    @LuaWhitelist
    @LuaMethodDoc(
            overloads = @LuaFunctionOverload(
                    argumentTypes = PlayerEntityWrapper.class,
                    argumentNames = "entity"
            ),
            description = "player.get_experience_level"
    )
    public static int getExperienceLevel(PlayerEntityWrapper entity) {
        return getEntity(entity).experienceLevel;
    }

    @LuaWhitelist
    @LuaMethodDoc(
            overloads = @LuaFunctionOverload(
                    argumentTypes = PlayerEntityWrapper.class,
                    argumentNames = "entity"
            ),
            description = "player.get_model_type"
    )
    public static String getModelType(PlayerEntityWrapper entity) {
        if (Minecraft.getInstance().player == null)
            return null;

        PlayerInfo info = Minecraft.getInstance().player.connection.getPlayerInfo(entity.savedUUID);
        if (info == null)
            return null;

        return info.getModelName().toUpperCase();
    }

    @LuaWhitelist
    @LuaMethodDoc(
            overloads = @LuaFunctionOverload(
                    argumentTypes = PlayerEntityWrapper.class,
                    argumentNames = "entity"
            ),
            description = "player.get_gamemode"
    )
    public static String getGamemode(PlayerEntityWrapper entity) {
        if (Minecraft.getInstance().player == null)
            return null;

        PlayerInfo info = Minecraft.getInstance().player.connection.getPlayerInfo(entity.savedUUID);
        if (info == null)
            return null;

        return info.getGameMode() == null ? null : info.getGameMode().getName().toUpperCase();
    }

    @LuaWhitelist
    @LuaMethodDoc(
            overloads = @LuaFunctionOverload(
                    argumentTypes = PlayerEntityWrapper.class,
                    argumentNames = "entity"
            ),
            description = "player.is_flying"
    )
    public static boolean isFlying(PlayerEntityWrapper entity) {
        return getEntity(entity).getAbilities().flying;
    }

    @LuaWhitelist
    @LuaMethodDoc(
            overloads = @LuaFunctionOverload(
                    argumentTypes = {PlayerEntityWrapper.class, String.class},
                    argumentNames = {"entity", "key"}
            ),
            description = "player.get_variable"
    )
    public static Object getVariable(PlayerEntityWrapper entity, String key) {
        Avatar a = AvatarManager.getAvatarForPlayer(entity.savedUUID);
        if (a == null || a.luaState == null)
            return null;

        return a.luaState.meta.get(key);
    }

    @Override
    public String toString() {
        return savedUUID + " (Player)";
    }
}
