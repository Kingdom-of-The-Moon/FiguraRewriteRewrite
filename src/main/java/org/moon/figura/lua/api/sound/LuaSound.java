package org.moon.figura.lua.api.sound;


import com.mojang.blaze3d.audio.SoundBuffer;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import org.luaj.vm2.LuaError;
import org.moon.figura.avatar.Avatar;
import org.moon.figura.lua.LuaNotNil;
import org.moon.figura.lua.LuaWhitelist;
import org.moon.figura.lua.api.world.WorldAPI;
import org.moon.figura.lua.docs.LuaTypeDoc;
import org.moon.figura.math.vector.FiguraVec3;
import org.moon.figura.trust.Trust;
import org.moon.figura.utils.LuaUtils;

@LuaWhitelist
@LuaTypeDoc(
        name = "Sound",
        value = "sound"
)
public class LuaSound {

    private final Avatar owner;
    private final String id;

    public LuaSound(String id, Avatar owner) {
        this.owner = owner;
        this.id = id;
    }

    @LuaWhitelist
    public void play(double x, double y, double z){
        play(x, y, z, 1, 1, false);
    }

    @LuaWhitelist
    public void play(@LuaNotNil FiguraVec3 pos){
        play(pos.x, pos.y, pos.z, 1, 1, false);
    }

    @LuaWhitelist
    public void play(@LuaNotNil FiguraVec3 pos, float volume, float pitch, boolean loop){
        play(pos.x, pos.y, pos.z, volume, pitch, loop);
    }

    @LuaWhitelist
    public void play(double x, double y, double z, float volume, float pitch, boolean loop) {
        if (!owner.soundsRemaining.use())
            return;

        volume *= (owner.trust.get(Trust.VOLUME) / 100f);

        SoundBuffer buffer = owner.customSounds.get(id);
        if (buffer != null && owner.trust.get(Trust.CUSTOM_SOUNDS) == 1) {
            SoundAPI.getSoundEngine().figura$playCustomSound(
                    owner.owner,
                    id,
                    buffer,
                    x, y, z,
                    volume, pitch,
                    loop);
        } else {
            try {
                SoundEvent event = new SoundEvent(new ResourceLocation(id));
                SimpleSoundInstance instance = new SimpleSoundInstance(
                        event, SoundSource.PLAYERS,
                        volume, pitch,
                        RandomSource.create(WorldAPI.getCurrentWorld().random.nextLong()),
                        x, y, z);

                SoundAPI.getSoundEngine().figura$playSound(
                        owner.owner, id, instance, loop
                );
            } catch (Exception ignored) {}
        }
    }

    public String toString() {
        return id + " (Sound)";
    }

}
