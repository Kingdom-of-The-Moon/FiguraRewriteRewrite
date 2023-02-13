package org.moon.figura.lua.api.action_wheel;

import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaFunction;
import org.luaj.vm2.Varargs;
import org.moon.figura.avatar.Avatar;
import org.moon.figura.gui.ActionWheel;
import org.moon.figura.lua.LuaNotNil;
import org.moon.figura.lua.LuaWhitelist;
import org.moon.figura.lua.docs.LuaTypeDoc;

import java.util.HashMap;

@LuaWhitelist
@LuaTypeDoc(
        name = "ActionWheelAPI",
        value = "action_wheel"
)
public class ActionWheelAPI {

    public Page currentPage;
    private final HashMap<String, Page> pages = new HashMap<>();
    private final boolean isHost;

    @LuaWhitelist
    public LuaFunction leftClick;
    @LuaWhitelist
    public LuaFunction rightClick;
    @LuaWhitelist
    public LuaFunction scroll;

    public ActionWheelAPI(Avatar owner) {
        this.isHost = owner.isHost;
    }

    @LuaWhitelist
    public ActionWheelAPI execute(Integer index, boolean right) {
        if (index != null && (index < 1 || index > 8))
            throw new LuaError("index must be between 1 and 8");
        if (this.isHost) ActionWheel.execute(index == null ? ActionWheel.getSelected() : index - 1, !right);
        return this;
    }

    @LuaWhitelist
    public boolean isEnabled() {
        return this.isHost && ActionWheel.isEnabled();
    }

    @LuaWhitelist
    public int getSelected() {
        return this.isHost ? ActionWheel.getSelected() + 1 : 0;
    }

    @LuaWhitelist
    public Action getSelectedAction() {
        if (!this.isHost || this.currentPage == null)
            return null;

        int selected = ActionWheel.getSelected();
        if (selected < 0 || selected > 7)
            return null;

        return this.currentPage.slots()[selected];
    }

    @LuaWhitelist
    public Action newAction() {
        return new Action();
    }

    @LuaWhitelist
    public Page newPage(String title) {
        Page page = new Page(title);
        if (title != null) this.pages.put(title, page);
        return page;
    }

    @LuaWhitelist
    public void setPage(Page page) {
        currentPage = page;
    }

    @LuaWhitelist
    public void setPage(@LuaNotNil String pageTitle) {
        if (pages.containsKey(pageTitle))
            currentPage = pages.get(pageTitle);
        else
            throw new LuaError("Page \"" + pageTitle + "\" not found");
    }

    @LuaWhitelist
    public Page getPage(String pageTitle) {
        return this.pages.get(pageTitle);
    }

    @LuaWhitelist
    public Page getCurrentPage() {
        return this.currentPage;
    }

    public boolean execute(Avatar avatar, boolean left) {
        LuaFunction function = left ? leftClick : rightClick;

        //execute
        if (function != null) {
            Varargs result = avatar.run(function, avatar.tick);
            return result != null && result.arg(1).isboolean() && result.arg(1).checkboolean();
        }

        return false;
    }

    public boolean mouseScroll(Avatar avatar, double delta) {
        if (scroll != null) {
            Varargs result = avatar.run(scroll, avatar.tick, delta);
            return result != null && result.arg(1).isboolean() && result.arg(1).checkboolean();
        }

        return false;
    }

    @LuaWhitelist
    public Object __index(String arg) {
        if (arg == null) return null;
        return switch (arg) {
            case "leftClick" -> leftClick;
            case "rightClick" -> rightClick;
            case "scroll" -> scroll;
            default -> null;
        };
    }

    @LuaWhitelist
    public void __newindex(@LuaNotNil String key, Object value) {
        LuaFunction val = value instanceof LuaFunction f ? f : null;
        switch (key) {
            case "leftClick" -> leftClick = val;
            case "rightClick" -> rightClick = val;
            case "scroll" -> scroll = val;
            default -> throw new LuaError("Cannot assign value on key \"" + key + "\"");
        }
    }

    @Override
    public String toString() {
        return "ActionWheelAPI";
    }
}
