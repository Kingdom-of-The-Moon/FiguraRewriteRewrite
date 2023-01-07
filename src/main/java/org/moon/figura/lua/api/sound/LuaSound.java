package org.moon.figura.lua.api.sound;

import com.mojang.blaze3d.audio.Channel;
import com.mojang.blaze3d.audio.Library;
import com.mojang.blaze3d.audio.SoundBuffer;
import net.minecraft.client.resources.sounds.Sound;
import net.minecraft.client.sounds.ChannelAccess;
import net.minecraft.client.sounds.SoundBufferLibrary;
import net.minecraft.sounds.SoundSource;
import org.moon.figura.avatar.Avatar;
import org.moon.figura.lua.LuaWhitelist;
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
    private final SoundBuffer buffer;
    private final Sound sound;

    private ChannelAccess.ChannelHandle handle;
    private boolean playing = false;

    private FiguraVec3 pos = FiguraVec3.of();
    private float pitch = 1f;
    private float volume = 1f;
    private float attenuation = 1f;
    private boolean loop = false;

    public LuaSound(SoundBuffer buffer, String id, Avatar owner) {
        this.owner = owner;
        this.id = id;
        this.buffer = buffer;
        this.sound = null;
    }

    public LuaSound(Sound sound, String id, Avatar owner) {
        this.owner = owner;
        this.id = id;
        this.buffer = null;
        this.sound = sound;
    }

    public ChannelAccess.ChannelHandle getHandle() {
        return handle;
    }

    private float calculateVolume() {
        return volume * SoundAPI.getSoundEngine().figura$getVolume(SoundSource.PLAYERS) * (owner.trust.get(Trust.VOLUME) / 100f);
    }

    @LuaWhitelist
    public LuaSound play() {
        if (this.playing)
            return this;

        if (!owner.soundsRemaining.use()) {
            owner.trustIssues.add(Trust.SOUNDS);
            return this;
        }

        owner.trustIssues.remove(Trust.SOUNDS);

        if (handle != null) {
            handle.execute(Channel::unpause);
            this.playing = true;
        } else if (buffer != null) {
            this.handle = SoundAPI.getSoundEngine().figura$createHandle(owner.owner, id, Library.Pool.STATIC);
            if (handle == null)
                return this;

            SoundAPI.getSoundEngine().figura$addSound(this);

            handle.execute(channel -> {
                channel.setPitch(pitch);
                channel.setVolume(calculateVolume());
                channel.linearAttenuation(attenuation * 16f);
                channel.setLooping(loop);
                channel.setSelfPosition(pos.asVec3());
                channel.setRelative(false);
                channel.attachStaticBuffer(buffer);
                channel.play();
            });

            this.playing = true;
        } else if (sound != null) {
            boolean shouldStream = sound.shouldStream();
            this.handle = SoundAPI.getSoundEngine().figura$createHandle(owner.owner, id, shouldStream ? Library.Pool.STREAMING : Library.Pool.STATIC);
            if (handle == null)
                return this;

            SoundAPI.getSoundEngine().figura$addSound(this);

            handle.execute(channel -> {
                channel.setPitch(pitch);
                channel.setVolume(calculateVolume());
                channel.linearAttenuation(attenuation * 16f);
                channel.setLooping(loop && !shouldStream);
                channel.setSelfPosition(pos.asVec3());
                channel.setRelative(false);
            });

            SoundBufferLibrary lib = SoundAPI.getSoundEngine().figura$getSoundBuffers();
            if (!shouldStream) {
                lib.getCompleteBuffer(sound.getPath()).thenAccept(buffer -> handle.execute(channel -> {
                    channel.attachStaticBuffer(buffer);
                    channel.play();
                }));
            } else {
                lib.getStream(sound.getPath(), loop).thenAccept(stream -> handle.execute(channel -> {
                    channel.attachBufferStream(stream);
                    channel.play();
                }));
            }

            this.playing = true;
        }

        return this;
    }

    @LuaWhitelist
    public boolean isPlaying() {
        if (handle != null)
            handle.execute(channel -> this.playing = channel.playing());
        return this.playing;
    }

    @LuaWhitelist //TODO - no worky
    public LuaSound pause() {
        this.playing = false;
        if (handle != null)
            handle.execute(Channel::pause);
        return this;
    }

    @LuaWhitelist
    public LuaSound stop() {
        this.playing = false;
        if (handle != null)
            handle.execute(Channel::stop);
        handle = null;
        return this;
    }

    @LuaWhitelist
    public FiguraVec3 getPos() {
        return pos;
    }

    @LuaWhitelist
    public void setPos(Double x, Double y, Double z) {
        setPos(LuaUtils.freeVec3("pos", x, y, z));
    }

    @LuaWhitelist
    public void setPos(FiguraVec3 pos) {
        this.pos = pos.copy();
        if (handle != null)
            handle.execute(channel -> channel.setSelfPosition(pos.asVec3()));
    }

    @LuaWhitelist
    public LuaSound pos(Double x, Double y, Double z) {
        return pos(LuaUtils.freeVec3("pos", x, y, z));
    }

    @LuaWhitelist
    public LuaSound pos(FiguraVec3 pos) {
        setPos(pos);
        return this;
    }

    @LuaWhitelist
    public float getVolume() {
        return volume;
    }

    @LuaWhitelist
    public void setVolume(float volume) {
        this.volume = Math.min(volume, 1);
        if (handle != null)
            handle.execute(channel -> channel.setVolume(calculateVolume()));
    }

    @LuaWhitelist
    public LuaSound volume(float volume) {
        setVolume(volume);
        return this;
    }

    @LuaWhitelist
    public float getAttenuation() {
        return attenuation;
    }

    @LuaWhitelist
    public void setAttenuation(float attenuation) {
        this.attenuation = Math.max(attenuation, 1);
        if (handle != null)
            handle.execute(channel -> channel.linearAttenuation(this.attenuation * 16f));
    }

    @LuaWhitelist
    public LuaSound attenuation(float attenuation) {
        setAttenuation(attenuation);
        return this;
    }

    @LuaWhitelist
    public float getPitch() {
        return pitch;
    }

    @LuaWhitelist
    public void setPitch(float pitch) {
        this.pitch = Math.max(pitch, 0);
        if (handle != null)
            handle.execute(channel -> channel.setPitch(this.pitch));
    }

    @LuaWhitelist
    public LuaSound pitch(float pitch) {
        setPitch(pitch);
        return this;
    }

    @LuaWhitelist
    public boolean isLooping() {
        return loop;
    }

    @LuaWhitelist
    public void setLoop(boolean loop) {
        this.loop = loop;
        if (handle != null)
            handle.execute(channel -> channel.setLooping(this.loop));
    }

    @LuaWhitelist
    public LuaSound loop(boolean loop) {
        setLoop(loop);
        return this;
    }

    @Override
    public String toString() {
        return id + " (Sound)";
    }
}
