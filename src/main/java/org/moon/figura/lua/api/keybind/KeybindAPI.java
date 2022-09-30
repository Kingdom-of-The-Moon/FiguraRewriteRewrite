package org.moon.figura.lua.api.keybind;

import net.minecraft.client.KeyMapping;
import org.moon.figura.avatars.Avatar;
import org.moon.figura.lua.LuaNotNil;
import org.moon.figura.lua.LuaWhitelist;
import org.moon.figura.lua.docs.LuaMethodDoc;
import org.moon.figura.lua.docs.LuaMethodDoc.LuaMethodOverload;
import org.moon.figura.lua.docs.LuaTypeDoc;
import org.moon.figura.mixin.input.KeyMappingAccessor;

import java.util.ArrayList;

@LuaWhitelist
@LuaTypeDoc(
        name = "KeybindAPI",
        value = "keybind_api"
)
public class KeybindAPI {

    public final ArrayList<FiguraKeybind> keyBindings = new ArrayList<>();
    public final Avatar owner;

    public KeybindAPI(Avatar owner) {
        this.owner = owner;
    }

    @LuaWhitelist
    @LuaMethodDoc(
            overloads = {
                    @LuaMethodOverload(
                            argumentTypes = {String.class, String.class},
                            argumentNames = {"name", "key"}
                    ),
                    @LuaMethodOverload(
                            argumentTypes = {String.class, String.class, Boolean.class},
                            argumentNames = {"name", "key", "gui"}
                    ),
                    @LuaMethodOverload(
                            argumentTypes = {String.class, String.class, Boolean.class, Boolean.class},
                            argumentNames = {"name", "key", "gui", "override"}
                    )
            },
            value = "keybind_api.create"
    )
    public FiguraKeybind create(@LuaNotNil String name, @LuaNotNil String key, boolean gui, boolean override) {
        this.keyBindings.removeIf(binding -> binding.getName().equals(name));

        FiguraKeybind binding = new FiguraKeybind(this.owner, name, FiguraKeybind.parseStringKey(key));
        binding.gui = gui;
        binding.override = override;

        this.keyBindings.add(binding);
        return binding;
    }

    @LuaWhitelist
    @LuaMethodDoc(
            overloads = @LuaMethodOverload(
                    argumentTypes = String.class,
                    argumentNames = "id"
            ),
            value = "keybind_api.get_vanilla_key"
    )
    public String getVanillaKey(@LuaNotNil String id) {
        KeyMapping key = KeyMappingAccessor.getAll().get(id);
        return key == null ? null : key.saveString();
    }

    @Override
    public String toString() {
        return "KeybindAPI";
    }
}
