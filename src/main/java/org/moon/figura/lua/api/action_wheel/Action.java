package org.moon.figura.lua.api.action_wheel;

import net.minecraft.world.item.ItemStack;
import org.luaj.vm2.LuaFunction;
import org.moon.figura.avatar.Avatar;
import org.moon.figura.lua.LuaNotNil;
import org.moon.figura.lua.LuaWhitelist;
import org.moon.figura.lua.api.world.ItemStackAPI;
import org.moon.figura.lua.docs.LuaMethodDoc;
import org.moon.figura.lua.docs.LuaTypeDoc;
import org.moon.figura.math.vector.FiguraVec3;
import org.moon.figura.model.rendering.texture.FiguraTexture;
import org.moon.figura.utils.LuaUtils;

@LuaWhitelist
@LuaTypeDoc(
        name = "Action",
        value = "wheel_action"
)
public class Action {

    public static final FiguraVec3 HOVER_COLOR = FiguraVec3.of(1, 1, 1);
    public static final FiguraVec3 TOGGLE_COLOR = FiguraVec3.of(0, 1, 0);

    protected String title, toggleTitle;
    protected ItemStack item, hoverItem, toggleItem;
    protected FiguraVec3 color, hoverColor, toggleColor;
    protected TextureData texture, hoverTexture, toggleTexture;
    protected boolean toggled = false;


    // -- function fields -- //


    @LuaWhitelist
    public LuaFunction leftClick;
    @LuaWhitelist
    public LuaFunction rightClick;

    @LuaWhitelist
    public LuaFunction toggle;
    @LuaWhitelist
    public LuaFunction untoggle;

    @LuaWhitelist
    public LuaFunction scroll;


    // -- java functions -- //


    public void execute(Avatar avatar, boolean left) {
        //click action
        LuaFunction function = left ? leftClick : rightClick;
        if (function != null)
            avatar.run(function, avatar.tick, this);

        if (!left)
            return;

        //toggle action
        function = toggled ? untoggle == null ? toggle : untoggle : toggle;
        if (function != null) {
            toggled = !toggled;
            avatar.run(function, avatar.tick, toggled, this);
        }
    }

    public void mouseScroll(Avatar avatar, double delta) {
        //scroll action
        if (scroll != null)
            avatar.run(scroll, avatar.tick, delta, this);
    }

    public ItemStack getItem(boolean selected) {
        ItemStack ret = null;
        if (selected)
            ret = hoverItem;
        if (ret == null && toggled)
            ret = toggleItem;
        if (ret == null)
            ret = item;
        return ret;
    }

    public FiguraVec3 getColor(boolean selected) {
        if (selected)
            return hoverColor == null ? HOVER_COLOR : hoverColor;
        else if (toggled)
            return toggleColor == null ? TOGGLE_COLOR : toggleColor;
        else
            return color;
    }

    public TextureData getTexture(boolean selected) {
        TextureData ret = null;
        if (selected)
            ret = hoverTexture;
        if (ret == null && toggled)
            ret = toggleTexture;
        if (ret == null)
            ret = texture;
        return ret;
    }

    public Action setItem(ItemStack item, String slot) {
        switch (slot){
            case "hover" -> hoverItem = item;
            case "toggle" -> toggleItem = item;
            default -> this.item = item;
        }
        return this;
    }


    // -- general functions -- //


    @LuaWhitelist
    public String getTitle() {
        return toggled ? toggleTitle == null ? title : toggleTitle : title;
    }

    @LuaWhitelist
    @LuaMethodDoc("title")
    public Action setTitle(String title) {
        this.title = title;
        return this;
    }

    @LuaWhitelist
    public FiguraVec3 getColor() {
        return this.color;
    }

    @LuaWhitelist
    public Action setColor(Double r, Double g, Double b) {
        return setColor(LuaUtils.freeVec3("setColor", r, g, b));
    }

    @LuaWhitelist
    @LuaMethodDoc("color")
    public Action setColor(@LuaNotNil FiguraVec3 color) {
        this.color = color.copy();
        return this;
    }

    @LuaWhitelist
    public FiguraVec3 getHoverColor() {
        return this.hoverColor;
    }

    @LuaWhitelist
    public Action setHoverColor(Double r, Double g, Double b) {
        return setHoverColor(LuaUtils.freeVec3("setHoverColor", r, g, b));
    }

    @LuaWhitelist
    @LuaMethodDoc("hoverColor")
    public Action setHoverColor(@LuaNotNil FiguraVec3 color) {
        this.hoverColor = color.copy();
        return this;
    }

    @LuaWhitelist
    public Action setItem(String itemId) {
        return setItem(LuaUtils.parseItemStack("setItem", itemId), "item");
    }

    @LuaWhitelist
    @LuaMethodDoc("item")
    public Action setItem(ItemStackAPI item) {
        return setItem(item.itemStack, "item");
    }

    @LuaWhitelist
    public Action setHoverItem(String itemId) {
        return setItem(LuaUtils.parseItemStack("setHoverItem", itemId), "hover");
    }

    @LuaWhitelist
    @LuaMethodDoc("hoverItem")
    public Action setHoverItem(ItemStackAPI item) {
        return setItem(item.itemStack, "hover");
    }

    @LuaWhitelist
    public Action setTexture(@LuaNotNil FiguraTexture texture) {
        return setTexture(texture, 0, 0, 0, 0, 0d);
    }

    @LuaWhitelist
    public Action setTexture(@LuaNotNil FiguraTexture texture, double u, double v) {
        return setTexture(texture, u, v, 0, 0, 0d);
    }

    @LuaWhitelist
    public Action setTexture(@LuaNotNil FiguraTexture texture, double u, double v, Integer width, Integer height) {
        return setTexture(texture, u, v, width, height, 0d);
    }

    @LuaWhitelist
    @LuaMethodDoc("texture")
    public Action setTexture(@LuaNotNil FiguraTexture texture, double u, double v, Integer width, Integer height, Double scale) {
        this.texture = new TextureData(texture, u, v, width, height, scale);
        return this;
    }

    @LuaWhitelist
    public Action setHoverTexture(@LuaNotNil FiguraTexture texture) {
        return setHoverTexture(texture, 0, 0, 0, 0, 0d);
    }

    @LuaWhitelist
    public Action setHoverTexture(@LuaNotNil FiguraTexture texture, double u, double v) {
        return setHoverTexture(texture, u, v, 0, 0, 0d);
    }

    @LuaWhitelist
    public Action setHoverTexture(@LuaNotNil FiguraTexture texture, double u, double v, Integer width, Integer height) {
        return setHoverTexture(texture, u, v, width, height, 0d);
    }

    @LuaWhitelist
    @LuaMethodDoc("hoverTexture")
    public Action setHoverTexture(@LuaNotNil FiguraTexture texture, double u, double v, Integer width, Integer height, Double scale) {
        this.hoverTexture = new TextureData(texture, u, v, width, height, scale);
        return this;
    }


    // -- set functions -- //


    @LuaWhitelist
    @LuaMethodDoc("onRightClick")
    public Action setOnLeftClick(LuaFunction function) {
        this.leftClick = function;
        return this;
    }

    @LuaWhitelist
    @LuaMethodDoc("onLeftClick")
    public Action setOnRightClick(LuaFunction function) {
        this.rightClick = function;
        return this;
    }

    @LuaWhitelist
    @LuaMethodDoc("onToggle")
    public Action setOnToggle(LuaFunction function) {
        this.toggle = function;
        return this;
    }

    @LuaWhitelist
    @LuaMethodDoc("onUntoggle")
    public Action setOnUntoggle(LuaFunction function) {
        this.untoggle = function;
        return this;
    }

    @LuaWhitelist
    @LuaMethodDoc("onScroll")
    public Action setOnScroll(LuaFunction function) {
        this.scroll = function;
        return this;
    }


    // -- toggle specific stuff -- //


    @LuaWhitelist
    public String getToggleTitle() {
        return this.toggleTitle;
    }

    @LuaWhitelist
    @LuaMethodDoc("toggleTitle")
    public Action setToggleTitle(String title) {
        this.toggleTitle = title;
        return this;
    }

    @LuaWhitelist
    public FiguraVec3 getToggleColor() {
        return this.toggleColor;
    }

    @LuaWhitelist
    public Action setToggleColor(Double r, Double g, Double b) {
        return setToggleColor(LuaUtils.freeVec3("toggleColor", r, g, b));
    }

    @LuaWhitelist
    @LuaMethodDoc("toggleColor")
    public Action setToggleColor(FiguraVec3 color) {
        this.toggleColor = color.copy();
        return this;
    }

    @LuaWhitelist
    public Action setToggleItem(String itemId) {
        return setItem(LuaUtils.parseItemStack("setToggleItem", itemId), "toggle");
    }

    @LuaWhitelist
    @LuaMethodDoc("toggleItem")
    public Action setToggleItem(ItemStackAPI item) {
        return setItem(item.itemStack, "toggle");
    }

    @LuaWhitelist
    public Action setToggleTexture(@LuaNotNil FiguraTexture texture) {
        return setToggleTexture(texture, 0, 0, 0, 0, 0d);
    }

    @LuaWhitelist
    public Action setToggleTexture(@LuaNotNil FiguraTexture texture, double u, double v) {
        return setToggleTexture(texture, u, v, 0, 0, 0d);
    }

    @LuaWhitelist
    public Action setToggleTexture(@LuaNotNil FiguraTexture texture, double u, double v, Integer width, Integer height) {
        return setToggleTexture(texture, u, v, width, height, 0d);
    }

    @LuaWhitelist
    public Action setToggleTexture(@LuaNotNil FiguraTexture texture, double u, double v, Integer width, Integer height, Double scale) {
        this.toggleTexture = new TextureData(texture, u, v, width, height, scale);
        return this;
    }

    @LuaWhitelist
    public boolean isToggled() {
        return this.toggled;
    }

    @LuaWhitelist
    @LuaMethodDoc("toggled")
    public Action setToggled(boolean bool) {
        this.toggled = bool;
        return this;
    }


    // -- metamethods -- //


    @LuaWhitelist
    public Object __index(String arg) {
        if (arg == null) return null;
        return switch (arg) {
            case "leftClick" -> leftClick;
            case "rightClick" -> rightClick;
            case "toggle" -> toggle;
            case "untoggle" -> untoggle;
            case "scroll" -> scroll;
            default -> null;
        };
    }

    @LuaWhitelist
    public void __newindex(String key, Object value) {
        if (key == null) return;
        LuaFunction func = value instanceof LuaFunction f ? f : null;
        switch (key) {
            case "leftClick" -> leftClick = func;
            case "rightClick" -> rightClick = func;
            case "toggle" -> toggle = func;
            case "untoggle" -> untoggle = func;
            case "scroll" -> scroll = func;
        }
    }

    @Override
    public String toString() {
        return title == null ? "Action Wheel Action" : "Action Wheel Action (" + title + ")";
    }

    public static class TextureData {

        public final FiguraTexture texture;
        public final double u, v, scale;
        public final int width, height;

        public TextureData(FiguraTexture texture, double u, double v, Integer width, Integer height, Double scale) {
            this.texture = texture;
            this.u = u;
            this.v = v;
            this.width = width == null ? texture.getWidth() : width;
            this.height = height == null ? texture.getHeight() : height;
            this.scale = scale == null ? 1d : scale;
        }
    }
}
