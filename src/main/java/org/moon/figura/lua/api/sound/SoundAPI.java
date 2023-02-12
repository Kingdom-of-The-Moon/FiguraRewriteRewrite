package org.moon.figura.lua.api.sound;

import com.mojang.blaze3d.audio.SoundBuffer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.Sound;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.client.sounds.WeighedSoundEvents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import org.luaj.vm2.LuaError;
import org.moon.figura.avatar.Avatar;
import org.moon.figura.ducks.SoundEngineAccessor;
import org.moon.figura.lua.LuaNotNil;
import org.moon.figura.lua.LuaWhitelist;
import org.moon.figura.lua.api.world.WorldAPI;
import org.moon.figura.lua.docs.LuaTypeDoc;
import org.moon.figura.math.vector.FiguraVec3;
import org.moon.figura.mixin.sound.SoundManagerAccessor;
import org.moon.figura.permissions.Permissions;

import java.util.Base64;

@LuaWhitelist
@LuaTypeDoc(
        name = "SoundAPI",
        value = "sounds"
)
public class SoundAPI {

    private final Avatar owner;

    public SoundAPI(Avatar owner) {
        this.owner = owner;
    }

    public static SoundEngineAccessor getSoundEngine() {
        return (SoundEngineAccessor) ((SoundManagerAccessor) Minecraft.getInstance().getSoundManager()).getSoundEngine();
    }


    @LuaWhitelist
    public LuaSound playSound(String id, double x, double y, double z) {
        return playSound(id, x, y, z, 1, 1, false);
    }

    @LuaWhitelist
    public LuaSound playSound(@LuaNotNil String id, @LuaNotNil FiguraVec3 pos) {
        return playSound(id, pos.x, pos.y, pos.z, 1, 1, false);
    }

    @LuaWhitelist
    public LuaSound playSound(@LuaNotNil String id, @LuaNotNil FiguraVec3 pos, float volume, float pitch, boolean loop) {
        return playSound(id, pos.x, pos.y, pos.z, volume, pitch, loop);
    }

    @LuaWhitelist
    public LuaSound playSound(@LuaNotNil String id, double x, double y, double z, float volume, float pitch, boolean loop) {
        return __index(id).setPos(x, y, z).setVolume(volume).setPitch(pitch).setLoop(loop).play();
    }

    @LuaWhitelist
    public SoundAPI stopSound(String id) {
        getSoundEngine().figura$stopSound(owner.owner, id);
        return this;
    }

    @LuaWhitelist
    public SoundAPI newSound(String name, String base64Text) {
        return newSound(name, Base64.getDecoder().decode(base64Text));
    }

    @LuaWhitelist
    public SoundAPI newSound(String name, byte[] byteArray) {
        try {
            owner.loadSound(name, byteArray);
            return this;
        } catch (Exception e) {
            throw new LuaError("Failed to add custom sound \"" + name + "\"");
        }
    }

    @LuaWhitelist
    public boolean isPresent(String id) {
        if (id == null)
            return false;
        if (owner.customSounds.get(id) != null)
            return true;
        try {
            return Minecraft.getInstance().getSoundManager().getSoundEvent(new ResourceLocation(id)) != null;
        } catch (Exception ignored) {
            return false;
        }
    }

    @LuaWhitelist
    public LuaSound __index(String id) {
        SoundBuffer buffer = owner.customSounds.get(id);
        if (buffer != null) {
            if (owner.permissions.get(Permissions.CUSTOM_SOUNDS) == 1) {
                return new LuaSound(buffer, id, owner);
            } else {
                owner.noPermissions.add(Permissions.CUSTOM_SOUNDS);
            }
        }

        try {
            WeighedSoundEvents events = Minecraft.getInstance().getSoundManager().getSoundEvent(new ResourceLocation(id));
            if (events != null) {
                Sound sound = events.getSound(RandomSource.create(WorldAPI.getCurrentWorld().random.nextLong()));
                if (sound != SoundManager.EMPTY_SOUND) {
                    owner.noPermissions.remove(Permissions.CUSTOM_SOUNDS);
                    return new LuaSound(sound, id, owner);
                }
            }
            return new LuaSound((SoundBuffer) null, id, owner);
        } catch (Exception e) {
            throw new LuaError(e.getMessage());
        }
    }

    @Override
    public String toString() {
        return "SoundAPI";
    }
}
