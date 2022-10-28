package org.moon.figura.lua.api.sound;

import com.mojang.blaze3d.audio.SoundBuffer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.Sound;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.client.sounds.WeighedSoundEvents;
import net.minecraft.resources.ResourceLocation;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaTable;
import org.moon.figura.avatar.Avatar;
import org.moon.figura.ducks.SoundEngineAccessor;
import org.moon.figura.lua.LuaNotNil;
import org.moon.figura.lua.LuaWhitelist;
import org.moon.figura.lua.api.world.WorldAPI;
import org.moon.figura.lua.docs.LuaMethodDoc;
import org.moon.figura.lua.docs.LuaMethodOverload;
import org.moon.figura.lua.docs.LuaTypeDoc;
import org.moon.figura.math.vector.FiguraVec3;
import org.moon.figura.mixin.sound.SoundManagerAccessor;
import org.moon.figura.trust.Trust;
import org.moon.figura.utils.LuaUtils;

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
    @LuaMethodDoc(
            overloads = {
                    @LuaMethodOverload(
                            argumentTypes = {String.class, FiguraVec3.class},
                            argumentNames = {"sound", "pos"}
                    ),
                    @LuaMethodOverload(
                            argumentTypes = {String.class, Double.class, Double.class, Double.class},
                            argumentNames = {"sound", "posX", "posY", "posZ"}
                    ),
                    @LuaMethodOverload(
                            argumentTypes = {String.class, FiguraVec3.class, Double.class, Double.class, Boolean.class},
                            argumentNames = {"sound", "pos", "volume", "pitch", "loop"}
                    ),
                    @LuaMethodOverload(
                            argumentTypes = {String.class, Double.class, Double.class, Double.class, Double.class, Double.class, Boolean.class},
                            argumentNames = {"sound", "posX", "posY", "posZ", "volume", "pitch", "loop"}
                    )
            },
            value = "sounds.play_sound"
    )
    public LuaSound playSound(@LuaNotNil String id, Object x, Double y, Double z, Object w, Double t, boolean loop) {
        LuaSound sound = __index(id);
        FiguraVec3 pos;
        float volume = 1f;
        float pitch = 1f;

        if (x instanceof FiguraVec3) {
            pos = ((FiguraVec3) x).copy();
            if (y != null) volume = y.floatValue();
            if (z != null) pitch = z.floatValue();
            if (w != null) {
                if (!(w instanceof Boolean))
                    throw new LuaError("Illegal argument to playSound(): " + w);
                loop = (boolean) w;
            }
        } else if (x == null || x instanceof Number) {
            pos = LuaUtils.parseVec3("playSound", x, y, z);
            if (w != null) {
                if (!(w instanceof Double))
                    throw new LuaError("Illegal argument to playSound(): " + w);
                volume = ((Double) w).floatValue();
            }
            if (t != null) pitch = t.floatValue();
        } else {
            throw new LuaError("Illegal argument to playSound(): " + x);
        }

        sound.pos(pos, null, null);
        sound.volume(volume);
        sound.pitch(pitch);
        sound.loop(loop);
        sound.play();

        return sound;
    }

    @LuaWhitelist
    @LuaMethodDoc(
            overloads = {
                    @LuaMethodOverload,
                    @LuaMethodOverload(
                            argumentTypes = String.class,
                            argumentNames = "id"
                    )
            },
            value = "sounds.stop_sound"
    )
    public void stopSound(String id) {
        getSoundEngine().figura$stopSound(owner.owner, id);
    }

    @LuaWhitelist
    @LuaMethodDoc(
            overloads = {
                    @LuaMethodOverload(
                            argumentTypes = {String.class, LuaTable.class},
                            argumentNames = {"name", "byteArray"}
                    ),
                    @LuaMethodOverload(
                            argumentTypes = {String.class, String.class},
                            argumentNames = {"name", "base64Text"}
                    )
            },
            value = "sounds.add_sound"
    )
    public void addSound(@LuaNotNil String name, @LuaNotNil Object object) {
        byte[] bytes;
        if (object instanceof LuaTable table) {
            bytes = new byte[table.length()];
            for(int i = 0; i < bytes.length; i++)
                bytes[i] = (byte) table.get(i + 1).checkint();
        } else if (object instanceof String s) {
            bytes = Base64.getDecoder().decode(s);
        } else {
            throw new LuaError("Invalid type for addSound \"" + object.getClass().getSimpleName() + "\"");
        }

        try {
            owner.loadSound(name, bytes);
        } catch (Exception e) {
            throw new LuaError("Failed to add custom sound \"" + name + "\"");
        }
    }

    @LuaWhitelist
    public LuaSound __index(String id) {
        SoundBuffer buffer = owner.customSounds.get(id);
        if (buffer != null && owner.trust.get(Trust.CUSTOM_SOUNDS) == 1)
            return new LuaSound(buffer, id, owner);

        try {
            ResourceLocation res = new ResourceLocation(id);
            WeighedSoundEvents event = ((SoundManagerAccessor) Minecraft.getInstance().getSoundManager()).getSoundEvent(res);
            if (event == null)
                throw new LuaError("Could not find sound \"" + id + "\"");

            Sound sound = event.getSound(WorldAPI.getCurrentWorld().random);
            if (sound == SoundManager.EMPTY_SOUND)
                return null;

            buffer = getSoundEngine().figura$getBuffer(sound.getPath());
            return new LuaSound(buffer, id, owner);
        } catch (Exception e) {
            throw new LuaError(e.getMessage());
        }
    }

    @Override
    public String toString() {
        return "SoundAPI";
    }
}
