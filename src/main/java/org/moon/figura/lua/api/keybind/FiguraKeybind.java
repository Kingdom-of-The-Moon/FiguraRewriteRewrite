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
        boolean overrided = false;
        for (FiguraKeybind keybind : bindings) {
            if (keybind.key == key && keybind.enabled && (keybind.gui || Minecraft.getInstance().screen == null))
                overrided = keybind.setDown(pressed) || overrided;
        }
        return overrided;
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

    @LuaWhitelist
    public void setOnPress(LuaFunction function) {
        this.press = function;
    }

    @LuaWhitelist
    public FiguraKeybind onPress(LuaFunction function) {
        setOnPress(function);
        return this;
    }

    @LuaWhitelist
    public void setOnRelease(LuaFunction function) {
        this.release = function;
    }

    @LuaWhitelist
    public FiguraKeybind onRelease(LuaFunction function) {
        setOnRelease(function);
        return this;
    }

    @LuaWhitelist
    public void setKey(@LuaNotNil String key) {
        this.key = parseStringKey(key);
    }

    @LuaWhitelist
    public FiguraKeybind key(@LuaNotNil String key) {
        setKey(key);
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
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @LuaWhitelist
    public FiguraKeybind enabled(boolean enabled) {
        setEnabled(enabled);
        return this;
    }

    @LuaWhitelist
    public boolean hasGUI() {
        return this.gui;
    }

    @LuaWhitelist
    public void setGUI(boolean enabled) {
        this.gui = enabled;
    }

    @LuaWhitelist
    public FiguraKeybind gui(boolean enabled) {
        setGUI(enabled);
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
    public void __newindex(String key, LuaFunction function) {
        if (key == null) return;
        switch (key) {
            case "press" -> press = function;
            case "release" -> release = function;
        }
    }

    @Override
    public String toString() {
        return this.name + " (" + key.getName() + ") (Keybind)";
    }
}
