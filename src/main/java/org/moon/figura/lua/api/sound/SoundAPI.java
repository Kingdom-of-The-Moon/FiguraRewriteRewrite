package org.moon.figura.lua.api.sound;

import net.minecraft.client.Minecraft;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaTable;
import org.moon.figura.avatar.Avatar;
import org.moon.figura.ducks.SoundEngineAccessor;
import org.moon.figura.lua.LuaNotNil;
import org.moon.figura.lua.LuaWhitelist;
import org.moon.figura.lua.docs.LuaMetamethodDoc;
import org.moon.figura.lua.docs.LuaMetamethodDoc.LuaMetamethodOverload;
import org.moon.figura.lua.docs.LuaTypeDoc;
import org.moon.figura.math.vector.FiguraVec3;
import org.moon.figura.mixin.sound.SoundManagerAccessor;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@LuaWhitelist
@LuaTypeDoc(
        name = "SoundAPI",
        value = "sounds"
)
public class SoundAPI {

    private final Avatar owner;
    private final Map<String, LuaSound> luaSounds;

    public SoundAPI(Avatar owner) {
        this.owner = owner;
        luaSounds = new HashMap<>();
    }

    public static SoundEngineAccessor getSoundEngine() {
        return (SoundEngineAccessor) ((SoundManagerAccessor) Minecraft.getInstance().getSoundManager()).getSoundEngine();
    }

    @LuaWhitelist
    @LuaMetamethodDoc(
            overloads = @LuaMetamethodOverload(
                    types = {LuaSound.class, String.class}
            )
    )
    public LuaSound __index(String id) {
        return luaSounds.computeIfAbsent(id, str -> new LuaSound(str, owner));
    }

    @LuaWhitelist
    public void playSound(String id, double x, double y, double z){
        playSound(id, x, y, z, 1, 1, false);
    }

    @LuaWhitelist
    public void playSound(@LuaNotNil String id, @LuaNotNil FiguraVec3 pos){
        playSound(id, pos.x, pos.y, pos.z, 1, 1, false);
    }

    @LuaWhitelist
    public void playSound(@LuaNotNil String id, @LuaNotNil FiguraVec3 pos, float volume, float pitch, Boolean loop){
        playSound(id, pos.x, pos.y, pos.z, volume, pitch, loop);
    }

    @LuaWhitelist
    public void playSound(@LuaNotNil String id, double x, double y, double z, float volume, float pitch, boolean loop) {
        __index(id).play(x, y, z, volume, pitch, loop);
    }

    @LuaWhitelist
    public void stopSound(){
        getSoundEngine().figura$stopSound(owner.owner, null);
    }

    @LuaWhitelist
    public void stopSound(String id) {
        getSoundEngine().figura$stopSound(owner.owner, id);
    }

    @LuaWhitelist
    public void addSound(String name, String base64Text){
        addSound(name, Base64.getDecoder().decode(base64Text));
    }

    @LuaWhitelist
    public void addSound(String name, byte[] byteArray){
        try {
            owner.loadSound(name, byteArray);
        } catch (Exception e) {
            throw new LuaError("Failed to add custom sound \"" + name + "\"");
        }
    }

    @Override
    public String toString() {
        return "SoundAPI";
    }
}
