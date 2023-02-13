package org.moon.figura.lua.newdocswip;

import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaUserdata;
import org.luaj.vm2.LuaValue;
import org.moon.figura.animation.Animation;
import org.moon.figura.lua.LuaWhitelist;
import org.moon.figura.lua.MethodWrapper;
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
import org.moon.figura.lua.api.vanilla_model.VanillaModelAPI;
import org.moon.figura.lua.api.world.WorldAPI;
import org.moon.figura.lua.docs.LuaMethodDoc;
import org.moon.figura.lua.docs.LuaTypeDoc;
import org.moon.figura.model.FiguraModelPart;

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

    @LuaWhitelist
    public MethodWrapper vec;
    @LuaWhitelist
    public ActionWheelAPI action_wheel;
    @LuaWhitelist
    public Map<String, Animation> animations;
    @LuaWhitelist
    public Map<String, LuaTable> figuraMetatables;
    @LuaWhitelist
    public NameplateAPI nameplate;
    @LuaWhitelist
    public WorldAPI world;
    @LuaWhitelist
    public VanillaModelAPI vanilla_model;
    @LuaWhitelist
    public Map<String, FiguraModelPart> models;
    @LuaWhitelist
    public PlayerAPI player;
    @LuaWhitelist
    public EventsAPI events;
    @LuaWhitelist
    public KeybindAPI keybinds;
    @LuaWhitelist
    public VectorsAPI vectors;
    @LuaWhitelist
    public MatricesAPI matrices;
    @LuaWhitelist
    public ClientAPI client;
    @LuaWhitelist
    public HostAPI host;
    @LuaWhitelist
    public AvatarAPI avatar;
    @LuaWhitelist
    public ParticleAPI particles;
    @LuaWhitelist
    public SoundAPI sounds;
    @LuaWhitelist
    public RendererAPI renderer;
    @LuaWhitelist
    public EntityAPI<?> user;
    @LuaWhitelist
    public Map<String, PingAPI> pings;
    @LuaWhitelist
    public TextureAPI textures;
    @LuaWhitelist
    public NewMathDocs math;

    @LuaWhitelist
    public static String type(LuaValue value) {
        return null;
    }

    @LuaWhitelist
    public static LuaValue require(String scriptName) {
        return null;
    }

    @LuaWhitelist
    public static LuaTable listFiles() {
        return null;
    }

    @LuaWhitelist
    public static LuaTable listFiles(String folder) {return null;}

    @LuaWhitelist
    public static LuaTable listFiles(String folder, boolean subFolders) {return null;}

    @LuaWhitelist
    @LuaMethodDoc("log")
    public static String print(Object arg) {
        return null;
    }

    @LuaWhitelist
    public static String printTable(LuaTable table, int maxDepth, boolean silent) {
        return null;
    }

    @LuaWhitelist
    @LuaMethodDoc("logTable")
    public static String printTable(LuaUserdata object, int maxDepth, boolean silent) {
        return null;
    }

    @LuaWhitelist
    @LuaMethodDoc("logJson")
    public static String printJson(String json) {
        return null;
    }

}
