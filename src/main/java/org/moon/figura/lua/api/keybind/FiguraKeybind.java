package org.moon.figura.lua.api.keybind;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaFunction;
import org.luaj.vm2.Varargs;
import org.moon.figura.avatar.Avatar;
import org.moon.figura.lua.LuaNotNil;
import org.moon.figura.lua.LuaWhitelist;
import org.moon.figura.lua.docs.LuaMethodDoc;
import org.moon.figura.lua.docs.LuaTypeDoc;

import java.util.List;

@LuaWhitelist
@LuaTypeDoc(
        name = "Keybind",
        value = "keybind"
)
public class FiguraKeybind {

    private final Avatar owner;
    private final String name;
    private final InputConstants.Key defaultKey;

    private InputConstants.Key key;
    private boolean isDown, override;
    private boolean enabled = true;
    private boolean gui;

    @LuaWhitelist
    public LuaFunction press;

    @LuaWhitelist
    public LuaFunction release;

    public FiguraKeybind(Avatar owner, String name, InputConstants.Key key) {
        this.owner = owner;
        this.name = name;
        this.defaultKey = key;
        this.key = key;
    }

    public void resetDefaultKey() {
        this.key = this.defaultKey;
    }

    public boolean setDown(boolean bl) {
        //events
        if (isDown != bl) {
            Varargs result = null;

            if (bl) {
                if (press != null)
                    result = owner.run(press, owner.tick, this);
            } else if (release != null) {
                result = owner.run(release, owner.tick, this);
            }

            override = result != null && result.arg(1).isboolean() && result.checkboolean(1);
        }

        this.isDown = bl;
        return override;
    }

    public void setKey(InputConstants.Key key) {
        this.key = key;
    }

    public Component getTranslatedKeyMessage() {
        return this.key.getDisplayName();
    }

    // -- static -- //

    public static InputConstants.Key parseStringKey(String key) {
        try {
            return InputConstants.getKey(key);
        } catch (Exception passed) {
            throw new LuaError("Invalid key: " + key);
        }
    }

    public static boolean set(List<FiguraKeybind> bindings, InputConstants.Key key, boolean pressed) {
        boolean overridden = false;
        for (FiguraKeybind keybind : bindings) {
            if (keybind.key == key && keybind.enabled && (keybind.gui || Minecraft.getInstance().screen == null))
                overridden = keybind.setDown(pressed) || overridden;
        }
        return overridden;
    }

    public static void releaseAll(List<FiguraKeybind> bindings) {
        for (FiguraKeybind keybind : bindings)
            keybind.setDown(false);
    }

    public static void updateAll(List<FiguraKeybind> bindings) {
        for (FiguraKeybind keybind : bindings) {
            int value = keybind.key.getValue();
            if (keybind.enabled && keybind.key.getType() == InputConstants.Type.KEYSYM && value != InputConstants.UNKNOWN.getValue())
                keybind.setDown(InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), value));
        }
    }

    public static boolean overridesKey(List<FiguraKeybind> bindings, InputConstants.Key key) {
        for (FiguraKeybind binding : bindings)
            if (binding.key == key && binding.enabled && binding.override)
                return true;
        return false;
    }


    // -- lua -- //


    @LuaWhitelist
    @LuaMethodDoc("onPress")
    public FiguraKeybind setOnPress(LuaFunction function) {
        this.press = function;
        return this;
    }

    @LuaWhitelist
    @LuaMethodDoc("onRelease")
    public FiguraKeybind setOnRelease(LuaFunction function) {
        this.release = function;
        return this;
    }

    @LuaWhitelist
    @LuaMethodDoc("key")
    public FiguraKeybind setKey(@LuaNotNil String key) {
        this.key = parseStringKey(key);
        return this;
    }

    @LuaWhitelist
    public boolean isDefault() {
        return this.key.equals(this.defaultKey);
    }

    @LuaWhitelist
    public String getKey() {
        return this.key.getName();
    }

    @LuaWhitelist
    public String getKeyName() {
        return this.key.getDisplayName().getString();
    }

    @LuaWhitelist
    public String getName() {
        return this.name;
    }

    @LuaWhitelist
    public boolean isPressed() {
        return (this.gui || Minecraft.getInstance().screen == null) && this.isDown;
    }

    @LuaWhitelist
    public boolean isEnabled() {
        return this.enabled;
    }

    @LuaWhitelist
    @LuaMethodDoc("enabled")
    public FiguraKeybind setEnabled(boolean enabled) {
        this.enabled = enabled;
        return this;
    }

    @LuaWhitelist
    public boolean isGuiEnabled() {
        return this.gui;
    }

    @LuaWhitelist
    @LuaMethodDoc("gui")
    public FiguraKeybind setGUI(boolean enabled) {
        this.gui = enabled;
        return this;
    }

    @LuaWhitelist
    public Object __index(String arg) {
        if (arg == null) return null;
        return switch (arg) {
            case "press" -> press;
            case "release" -> release;
            default -> null;
        };
    }

    @LuaWhitelist
    public void __newindex(@LuaNotNil String key, LuaFunction function) {
        switch (key) {
            case "press" -> press = function;
            case "release" -> release = function;
            default -> throw new LuaError("Cannot assign value on key \"" + key + "\"");
        }
    }

    @Override
    public String toString() {
        return this.name + " (" + key.getName() + ") (Keybind)";
    }
}
