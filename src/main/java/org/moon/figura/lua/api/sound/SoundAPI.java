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
import org.moon.figura.trust.Trust;

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
    public void stopSound(String id) {
        getSoundEngine().figura$stopSound(owner.owner, id);
    }

    @LuaWhitelist
    public void newSound(String name, String base64Text) {
        newSound(name, Base64.getDecoder().decode(base64Text));
    }

    @LuaWhitelist
    public void newSound(String name, byte[] byteArray) {
        try {
            owner.loadSound(name, byteArray);
        } catch (Exception e) {
            throw new LuaError("Failed to add custom sound \"" + name + "\"");
        }
    }

    @LuaWhitelist
    public LuaSound __index(String id) {
        SoundBuffer buffer = owner.customSounds.get(id);
        if (buffer != null) {
            if (owner.trust.get(Trust.CUSTOM_SOUNDS) == 1) {
                return new LuaSound(buffer, id, owner);
            } else {
                owner.trustIssues.add(Trust.CUSTOM_SOUNDS);
            }
        }

        try {
            WeighedSoundEvents events = Minecraft.getInstance().getSoundManager().getSoundEvent(new ResourceLocation(id));
            if (events != null) {
                Sound sound = events.getSound(RandomSource.create(WorldAPI.getCurrentWorld().random.nextLong()));
                if (sound != SoundManager.EMPTY_SOUND) {
                    owner.trustIssues.remove(Trust.CUSTOM_SOUNDS);
                    return new LuaSound(sound, id, owner);
                }
            }
            throw new LuaError("Unable to find sound \"" + id + "\"");
        } catch (Exception e) {
            throw new LuaError(e.getMessage());
        }
    }

    @Override
    public String toString() {
        return "SoundAPI";
    }
}
