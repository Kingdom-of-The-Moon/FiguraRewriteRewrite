package org.moon.figura.animation;

import com.mojang.datafixers.util.Pair;
import org.luaj.vm2.LuaError;
import org.moon.figura.avatar.Avatar;
import org.moon.figura.lua.LuaNotNil;
import org.moon.figura.lua.LuaWhitelist;
import org.moon.figura.lua.docs.LuaMethodDoc;
import org.moon.figura.lua.docs.LuaTypeDoc;
import org.moon.figura.model.FiguraModelPart;

import java.util.*;

@LuaWhitelist
@LuaTypeDoc(
        name = "Animation",
        value = "animation"
)
public class Animation {

    private final Avatar owner;
    public final String modelName;

    @LuaWhitelist
    public final String name;

    // -- keyframes -- //

    protected final List<Map.Entry<FiguraModelPart, List<Animation.AnimationChannel>>> animationParts = new ArrayList<>();
    private final Map<Float, String> codeFrames = new HashMap<>();

    // -- player variables -- //

    private final TimeController controller = new TimeController();
    public PlayState playState = PlayState.STOPPED;
    private float time = 0f;
    private boolean inverted = false;
    private float lastTime = 0f;
    protected float frameTime = 0f;

    // -- data variables -- //

    protected float length, blend, offset;
    protected float speed = 1f;
    protected float startDelay, loopDelay;
    protected int override;
    protected int priority = 0;
    protected LoopMode loop;

    // -- java methods -- //

    public Animation(Avatar owner, String modelName, String name, LoopMode loop, boolean override, float length, float offset, float blend, float startDelay, float loopDelay) {
        this.owner = owner;
        this.modelName = modelName;
        this.name = name;
        this.loop = loop;
        this.override = override ? 7 : 0;
        this.length = length;
        this.offset = offset;
        this.blend = blend;
        this.startDelay = startDelay;
        this.loopDelay = loopDelay;
    }

    public void addAnimation(FiguraModelPart part, AnimationChannel anim) {
        Map.Entry<FiguraModelPart, List<AnimationChannel>> entry = null;
        for (Map.Entry<FiguraModelPart, List<AnimationChannel>> listEntry : this.animationParts) {
            if (listEntry.getKey() == part) {
                entry = listEntry;
                break;
            }
        }

        if (entry == null) {
            entry = new AbstractMap.SimpleEntry<>(part, new ArrayList<>());
            this.animationParts.add(entry);
        }

        entry.getValue().add(anim);
        this.animationParts.sort(Map.Entry.comparingByKey());
    }

    public void tick() {
        //tick time
        this.controller.tick();

        this.time += controller.getDiff() * speed;

        //loop checks
        switch (this.loop) {
            case ONCE -> {
                if ((!inverted && time >= length) || (inverted && time <= 0))
                    stop();
            }
            case LOOP -> {
                if (!inverted && time > length + loopDelay)
                    time -= length + loopDelay - offset;
                else if (inverted && time < offset - loopDelay)
                    time += length + loopDelay - offset;
            }
        }

        this.lastTime = this.frameTime;
        this.frameTime = Math.max(this.time, this.offset);

        //code events
        if (inverted)
            playCode(this.frameTime, this.lastTime);
        else
            playCode(this.lastTime, this.frameTime);
    }

    public void playCode(float minTime, float maxTime) {
        if (codeFrames.keySet().isEmpty())
            return;

        if (maxTime < minTime) {
            float len = length + 0.001f;
            playCode(Math.min(minTime, len), len);
            minTime = offset;
        }

        for (Float codeTime : codeFrames.keySet()) {
            if (codeTime >= minTime && codeTime < maxTime)
                owner.run(Pair.of("animations." + modelName + "." + name, codeFrames.get(codeTime)), owner.tick, this);
        }
    }


    // -- lua methods -- //


    @LuaWhitelist
    public boolean isPlaying() {
        return this.playState == PlayState.PLAYING;
    }

    @LuaWhitelist
    public boolean isPaused() {
        return this.playState == PlayState.PAUSED;
    }

    @LuaWhitelist
    public boolean isStopped() {
        return this.playState == PlayState.STOPPED;
    }

    @LuaWhitelist
    public Animation play() {
        switch (playState) {
            case PAUSED -> controller.resume();
            case STOPPED -> {
                controller.init();
                time = inverted ? (length + startDelay) : (offset - startDelay);
                lastTime = time;
                frameTime = 0f;
            }
            default -> {return this;}
        }

        playState = PlayState.PLAYING;
        return this;
    }

    @LuaWhitelist
    public Animation pause() {
        controller.pause();
        playState = PlayState.PAUSED;
        return this;
    }

    @LuaWhitelist
    public Animation stop() {
        controller.reset();
        playState = PlayState.STOPPED;
        return this;
    }

    @LuaWhitelist
    public Animation restart() {
        stop();
        play();
        return this;
    }

    @LuaWhitelist
    @LuaMethodDoc("playing")
    public Animation setPlaying(boolean playing) {
        if (playing)
            play();
        else
            stop();
        return this;
    }

    @LuaWhitelist
    public float getTime() {
        return time;
    }

    @LuaWhitelist
    @LuaMethodDoc("time")
    public Animation setTime(float time) {
        this.time = time;
        this.lastTime = time;
        this.frameTime = Math.max(time, this.offset);
        return this;
    }

    @LuaWhitelist
    public String getPlayState() {
        return playState.name();
    }

    @LuaWhitelist
    @LuaMethodDoc("code")
    public Animation newCode(float time, @LuaNotNil String data) {
        codeFrames.put(Math.max(time, 0f), data);
        return this;
    }

    @LuaWhitelist
    public float getBlend() {
        return this.blend;
    }

    @LuaWhitelist
    @LuaMethodDoc("blend")
    public Animation setBlend(float blend) {
        this.blend = blend;
        return this;
    }

    @LuaWhitelist
    public float getOffset() {
        return this.offset;
    }

    @LuaWhitelist
    @LuaMethodDoc("offset")
    public Animation setOffset(float offset) {
        this.offset = offset;
        return this;
    }

    @LuaWhitelist
    public float getStartDelay() {
        return this.startDelay;
    }

    @LuaWhitelist
    @LuaMethodDoc("startDelay")
    public Animation setStartDelay(float delay) {
        this.startDelay = delay;
        return this;
    }

    @LuaWhitelist
    public float getLoopDelay() {
        return this.loopDelay;
    }

    @LuaWhitelist
    @LuaMethodDoc("loopDelay")
    public Animation setLoopDelay(float delay) {
        this.loopDelay = delay;
        return this;
    }

    @LuaWhitelist
    public float getLength() {
        return this.length;
    }

    @LuaWhitelist
    @LuaMethodDoc("length")
    public Animation setLength(float length) {
        this.length = length;
        return this;
    }

    @LuaWhitelist
    @LuaMethodDoc("override")
    public Animation setOverride(boolean override) {
        this.override = override ? 7 : 0;
        return this;
    }

    @LuaWhitelist
    public boolean getOverrideRot() {
        return (override & 1) == 1;
    }

    @LuaWhitelist
    public boolean getOverridePos() {
        return (override & 2) == 2;
    }

    @LuaWhitelist
    public boolean getOverrideScale() {
        return (override & 4) == 4;
    }

    @LuaWhitelist
    @LuaMethodDoc("overrideRot")
    public Animation setOverrideRot(boolean override) {
        this.override = override ? this.override | 1 : this.override & 6;
        return this;
    }

    @LuaWhitelist
    @LuaMethodDoc("overridePos")
    public Animation setOverridePos(boolean override) {
        this.override = override ? this.override | 2 : this.override & 5;
        return this;
    }

    @LuaWhitelist
    @LuaMethodDoc("overrideScale")
    public Animation setOverrideScale(boolean override) {
        this.override = override ? this.override | 4 : this.override & 3;
        return this;
    }

    @LuaWhitelist
    public String getLoop() {
        return this.loop.name();
    }

    @LuaWhitelist
    @LuaMethodDoc("loop")
    public Animation setLoop(@LuaNotNil String loop) {
        try {
            this.loop = LoopMode.valueOf(loop.toUpperCase());
            return this;
        } catch (Exception ignored) {
            throw new LuaError("Illegal LoopMode: \"" + loop + "\".");
        }
    }

    @LuaWhitelist
    public int getPriority() {
        return this.priority;
    }

    @LuaWhitelist
    @LuaMethodDoc("priority")
    public Animation setPriority(int priority) {
        this.priority = priority;
        return this;
    }

    @LuaWhitelist
    public float getSpeed() {
        return this.speed;
    }

    @LuaWhitelist
    @LuaMethodDoc("speed")
    public Animation setSpeed(Float speed) {
        if (speed == null) speed = 1f;
        this.speed = speed;
        this.inverted = speed < 0;
        return this;
    }

    @LuaWhitelist
    public String getName() {
        return name;
    }

    @LuaWhitelist
    public Object __index(String arg) {
        if (arg == null) return null;
        if (arg.equals("name"))
            return name;
        return null;
    }

    @Override
    public String toString() {
        return name + " (Animation)";
    }

    // -- other classes -- //

    public enum PlayState {
        STOPPED,
        PAUSED,
        PLAYING
    }

    public enum LoopMode {
        LOOP,
        ONCE,
        HOLD
    }

    public record AnimationChannel(TransformType type, Keyframe... keyframes) {}
}
