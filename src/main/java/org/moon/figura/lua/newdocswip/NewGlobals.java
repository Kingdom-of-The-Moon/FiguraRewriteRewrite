package org.moon.figura.lua.newdocswip;

import org.luaj.vm2.LuaFunction;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaUserdata;
import org.luaj.vm2.LuaValue;
import org.moon.figura.animation.Animation;
import org.moon.figura.lua.LuaWhitelist;
import org.moon.figura.lua.MethodWrapper;
import org.moon.figura.lua.docs.LuaTypeDoc;
import org.moon.figura.model.FiguraModelPart;
import org.moon.figura.lua.api.*;
import org.moon.figura.lua.api.action_wheel.ActionWheelAPI;
import org.moon.figura.lua.api.entity.EntityAPI;
import org.moon.figura.lua.api.entity.PlayerAPI;
import org.moon.figura.lua.api.event.EventsAPI;
import org.moon.figura.lua.api.keybind.KeybindAPI;
import org.moon.figura.lua.api.math.MatricesAPI;
import org.moon.figura.lua.api.math.VectorsAPI;
import org.moon.figura.lua.api.nameplate.NameplateAPI;
import org.moon.figura.lua.api.particle.ParticleAPI;
import org.moon.figura.lua.api.ping.PingAPI;
import org.moon.figura.lua.api.sound.SoundAPI;
import org.moon.figura.lua.api.TextureAPI;
import org.moon.figura.lua.api.vanilla_model.VanillaModelAPI;
import org.moon.figura.lua.api.world.WorldAPI;

import java.util.Map;

/**
 * Class only exists to have docs for the global figura
 * functions/fields! This class should never end up in
 * anyone's code. It's not even whitelisted!
 */
@LuaTypeDoc(
        name = "globals",
        value = "globals"
)
@LuaWhitelist
abstract class NewGlobals {

    public MethodWrapper vec;
    public ActionWheelAPI action_wheel;
    public Map<String, Animation> animations;
    public LuaTable figuraMetatables;
    public NameplateAPI nameplate;
    public WorldAPI world;
    public VanillaModelAPI vanilla_model;
    public Map<String, FiguraModelPart> models;
    public PlayerAPI player;
    public EventsAPI events;
    public KeybindAPI keybind;
    public VectorsAPI vectors;
    public MatricesAPI matrices;
    public ClientAPI client;
    public HostAPI host;
    public AvatarAPI avatar;
    public ParticleAPI particles;
    public SoundAPI sounds;
    public RendererAPI renderer;
    public EntityAPI<?> user;
    public Map<String, PingAPI> pings;
    public TextureAPI textures;
    public LuaFunction type;

    @LuaWhitelist
    public static LuaValue require(String scriptName) {return null;}

    @LuaWhitelist
    public static LuaTable listFiles(){return null;}

    @LuaWhitelist
    public static LuaTable listFiles(String folder) {return null;}

    @LuaWhitelist
    public static LuaTable listFiles(String folder, boolean subFolders) {return null;}

    @LuaWhitelist
    public static String print(Object arg) {return null;}

    @LuaWhitelist
    public static String log(Object arg) {return null;}

    @LuaWhitelist
    public static String printTable(LuaTable table, int maxDepth, boolean silent){return null;}

    @LuaWhitelist
    public static String printTable(LuaUserdata object, int maxDepth, boolean silent){return null;}

    @LuaWhitelist
    public static String logTable(LuaTable table, int maxDepth, boolean silent){return null;}

    @LuaWhitelist
    public static String logTable(LuaUserdata object, int maxDepth, boolean silent){return null;}

    @LuaWhitelist
    public static String printJson(String json) {
        return null;
    }

    @LuaWhitelist
    public static String logJson(String json) {
        return null;
    }

}
