package org.moon.figura.lua.api.keybind;

import net.minecraft.client.KeyMapping;
import org.luaj.vm2.LuaError;
import org.moon.figura.avatar.Avatar;
import org.moon.figura.lua.LuaNotNil;
import org.moon.figura.lua.LuaWhitelist;
import org.moon.figura.lua.docs.LuaTypeDoc;
import org.moon.figura.mixin.input.KeyMappingAccessor;

import java.util.ArrayList;

@LuaWhitelist
@LuaTypeDoc(
        name = "KeybindAPI",
        value = "keybinds"
)
public class KeybindAPI {

    public final ArrayList<FiguraKeybind> keyBindings = new ArrayList<>();
    public final Avatar owner;

    public KeybindAPI(Avatar owner) {
        this.owner = owner;
    }

    @LuaWhitelist
    public FiguraKeybind newKeybind(@LuaNotNil String name) {
        return newKeybind(name, "key.keyboard.unknown");
    }

    @LuaWhitelist
    public FiguraKeybind newKeybind(@LuaNotNil String name, String key) {
        return newKeybind(name, key, false);
    }

    @LuaWhitelist
    public FiguraKeybind newKeybind(@LuaNotNil String name, String key, boolean gui) {
        FiguraKeybind binding = new FiguraKeybind(this.owner, name, FiguraKeybind.parseStringKey(key)).gui(gui);
        this.keyBindings.add(binding);
        return binding;
    }

    @LuaWhitelist
    public FiguraKeybind of(@LuaNotNil String name) {
        return newKeybind(name);
    }

    @LuaWhitelist
    public FiguraKeybind of(@LuaNotNil String name, String key) {
        return newKeybind(name, key);
    }

    @LuaWhitelist
    public FiguraKeybind of(@LuaNotNil String name, String key, boolean gui) {
        return newKeybind(name, key, gui);
    }

    @LuaWhitelist
    public FiguraKeybind fromVanilla(@LuaNotNil String id) {
        KeyMapping key = KeyMappingAccessor.getAll().get(id);
        if (key == null)
            throw new LuaError("Failed to find key: \"" + id + "\"");

        return newKeybind("[Vanilla] " + key.getTranslatedKeyMessage().getString(), key.saveString(), false);
    }

    @LuaWhitelist
    public String getVanillaKey(@LuaNotNil String id) {
        KeyMapping key = KeyMappingAccessor.getAll().get(id);
        return key == null ? null : key.saveString();
    }

    @Override
    public String toString() {
        return "KeybindAPI";
    }
}
