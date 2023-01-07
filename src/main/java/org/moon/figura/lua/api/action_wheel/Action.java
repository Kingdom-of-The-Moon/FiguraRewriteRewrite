package org.moon.figura.lua.api.action_wheel;

import net.minecraft.world.item.ItemStack;
import org.luaj.vm2.LuaFunction;
import org.moon.figura.avatar.Avatar;
import org.moon.figura.lua.LuaNotNil;
import org.moon.figura.lua.LuaWhitelist;
import org.moon.figura.lua.api.world.ItemStackAPI;
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


    // -- general functions -- //


    @LuaWhitelist
    public String getTitle() {
        return toggled ? toggleTitle == null ? title : toggleTitle : title;
    }

    @LuaWhitelist
    public void setTitle(String title) {
        this.title = title;
    }

    @LuaWhitelist
    public Action title(String title) {
        setTitle(title);
        return this;
    }

    @LuaWhitelist
    public FiguraVec3 getColor() {
        return this.color;
    }

    @LuaWhitelist
    public void setColor(Double r, Double g, Double b) {
        setColor(LuaUtils.freeVec3("setColor", r, g, b));
    }

    @LuaWhitelist
    public void setColor(@LuaNotNil FiguraVec3 color) {
        this.color = color.copy();
    }

    @LuaWhitelist
    public Action color(Double r, Double g, Double b) {
        return color(LuaUtils.freeVec3("color", r, g, b));
    }

    @LuaWhitelist
    public Action color(@LuaNotNil FiguraVec3 color) {
        setColor(color);
        return this;
    }

    @LuaWhitelist
    public FiguraVec3 getHoverColor() {
        return this.hoverColor;
    }

    @LuaWhitelist
    public void setHoverColor(Double r, Double g, Double b) {
        setHoverColor(LuaUtils.freeVec3("setHoverColor", r, g, b));
    }

    @LuaWhitelist
    public void setHoverColor(@LuaNotNil FiguraVec3 color) {
        this.hoverColor = color.copy();
    }

    @LuaWhitelist
    public Action hoverColor(Double r, Double g, Double b) {
        return hoverColor(LuaUtils.freeVec3("hoverColor", r, g, b));
    }

    @LuaWhitelist
    public Action hoverColor(@LuaNotNil FiguraVec3 color) {
        setHoverColor(color);
        return this;
    }

    @LuaWhitelist
    public void setItem(String itemId) {
        item(LuaUtils.parseItemStack("setItem", itemId));
    }

    @LuaWhitelist
    public void setItem(ItemStackAPI item) {
        item(item.itemStack);
    }

    @LuaWhitelist
    public Action item(String itemId) {
        return item(LuaUtils.parseItemStack("item", itemId));
    }

    @LuaWhitelist
    public Action item(ItemStackAPI item) {
        return item(item.itemStack);
    }

    public Action item(ItemStack item) {
        this.item = item;
        return this;
    }

    @LuaWhitelist
    public void setHoverItem(String itemId) {
        hoverItem(LuaUtils.parseItemStack("setHoverItem", itemId));
    }

    @LuaWhitelist
    public void setHoverItem(ItemStackAPI item) {
        hoverItem(item.itemStack);
    }

    @LuaWhitelist
    public Action hoverItem(String itemId) {
        return hoverItem(LuaUtils.parseItemStack("hoverItem", itemId));
    }

    @LuaWhitelist
    public Action hoverItem(ItemStackAPI item) {
        return hoverItem(item.itemStack);
    }

    public Action hoverItem(ItemStack item) {
        this.hoverItem = item;
        return this;
    }

    @LuaWhitelist
    public void setTexture(@LuaNotNil FiguraTexture texture) {
        setTexture(texture, 0, 0, 0, 0, 0d);
    }

    @LuaWhitelist
    public void setTexture(@LuaNotNil FiguraTexture texture, double u, double v) {
        setTexture(texture, u, v, 0, 0, 0d);
    }

    @LuaWhitelist
    public void setTexture(@LuaNotNil FiguraTexture texture, double u, double v, Integer width, Integer height) {
        setTexture(texture, u, v, width, height, 0d);
    }

    @LuaWhitelist
    public void setTexture(@LuaNotNil FiguraTexture texture, double u, double v, Integer width, Integer height, Double scale) {
        this.texture = new TextureData(texture, u, v, width, height, scale);
    }

    @LuaWhitelist
    public Action texture(@LuaNotNil FiguraTexture texture) {
        return texture(texture, 0, 0, 0, 0, 0d);
    }

    @LuaWhitelist
    public Action texture(@LuaNotNil FiguraTexture texture, double u, double v) {
        return texture(texture, u, v, 0, 0, 0d);
    }

    @LuaWhitelist
    public Action texture(@LuaNotNil FiguraTexture texture, double u, double v, Integer width, Integer height) {
        return texture(texture, u, v, width, height, 0d);
    }

    @LuaWhitelist
    public Action texture(@LuaNotNil FiguraTexture texture, double u, double v, Integer width, Integer height, Double scale) {
        setTexture(texture, u, v, width, height, scale);
        return this;
    }

    @LuaWhitelist
    public void setHoverTexture(@LuaNotNil FiguraTexture texture) {
        setHoverTexture(texture, 0, 0, 0, 0, 0d);
    }

    @LuaWhitelist
    public void setHoverTexture(@LuaNotNil FiguraTexture texture, double u, double v) {
        setHoverTexture(texture, u, v, 0, 0, 0d);
    }

    @LuaWhitelist
    public void setHoverTexture(@LuaNotNil FiguraTexture texture, double u, double v, Integer width, Integer height) {
        setHoverTexture(texture, u, v, width, height, 0d);
    }

    @LuaWhitelist
    public void setHoverTexture(@LuaNotNil FiguraTexture texture, double u, double v, Integer width, Integer height, Double scale) {
        this.hoverTexture = new TextureData(texture, u, v, width, height, scale);
    }

    @LuaWhitelist
    public Action hoverTexture(@LuaNotNil FiguraTexture texture) {
        return hoverTexture(texture, 0, 0, 0, 0, 0d);
    }

    @LuaWhitelist
    public Action hoverTexture(@LuaNotNil FiguraTexture texture, double u, double v) {
        return hoverTexture(texture, u, v, 0, 0, 0d);
    }

    @LuaWhitelist
    public Action hoverTexture(@LuaNotNil FiguraTexture texture, double u, double v, Integer width, Integer height) {
        return hoverTexture(texture, u, v, width, height, 0d);
    }

    @LuaWhitelist
    public Action hoverTexture(@LuaNotNil FiguraTexture texture, double u, double v, Integer width, Integer height, Double scale) {
        setHoverTexture(texture, u, v, width, height, scale);
        return this;
    }


    // -- set functions -- //


    @LuaWhitelist
    public void setOnLeftClick(LuaFunction function) {
        this.leftClick = function;
    }

    @LuaWhitelist
    public Action onLeftClick(LuaFunction function) {
        setOnLeftClick(function);
        return this;
    }

    @LuaWhitelist
    public void setOnRightClick(LuaFunction function) {
        this.rightClick = function;
    }

    @LuaWhitelist
    public Action onRightClick(LuaFunction function) {
        setOnRightClick(function);
        return this;
    }

    @LuaWhitelist
    public void setOnToggle(LuaFunction function) {
        this.toggle = function;
    }

    @LuaWhitelist
    public Action onToggle(LuaFunction function) {
        setOnToggle(function);
        return this;
    }

    @LuaWhitelist
    public void setOnUntoggle(LuaFunction function) {
        this.untoggle = function;
    }

    @LuaWhitelist
    public Action onUntoggle(LuaFunction function) {
        setOnUntoggle(function);
        return this;
    }

    @LuaWhitelist
    public void setOnScroll(LuaFunction function) {
        this.scroll = function;
    }

    @LuaWhitelist
    public Action onScroll(LuaFunction function) {
        setOnScroll(function);
        return this;
    }


    // -- toggle specific stuff -- //


    @LuaWhitelist
    public String getToggleTitle() {
        return this.toggleTitle;
    }

    @LuaWhitelist
    public void setToggleTitle(String title) {
        this.toggleTitle = title;
    }

    @LuaWhitelist
    public Action toggleTitle(String title) {
        setToggleTitle(title);
        return this;
    }

    @LuaWhitelist
    public FiguraVec3 getToggleColor() {
        return this.toggleColor;
    }

    @LuaWhitelist
    public void setToggleColor(Double r, Double g, Double b) {
        setToggleColor(LuaUtils.freeVec3("toggleColor", r, g, b));
    }

    @LuaWhitelist
    public void setToggleColor(FiguraVec3 color) {
        this.toggleColor = color.copy();
    }

    @LuaWhitelist
    public Action toggleColor(Double r, Double g, Double b) {
        return toggleColor(LuaUtils.freeVec3("toggleColor", r, g, b));
    }

    @LuaWhitelist
    public Action toggleColor(FiguraVec3 color) {
        setToggleColor(color);
        return this;
    }

    @LuaWhitelist
    public void setToggleItem(String itemId) {
        toggleItem(LuaUtils.parseItemStack("setToggleItem", itemId));
    }

    @LuaWhitelist
    public void setToggleItem(ItemStackAPI item) {
        toggleItem(item.itemStack);
    }

    @LuaWhitelist
    public Action toggleItem(String itemId) {
        return toggleItem(LuaUtils.parseItemStack("toggleItem", itemId));
    }

    @LuaWhitelist
    public Action toggleItem(ItemStackAPI item) {
        return toggleItem(item.itemStack);
    }

    public Action toggleItem(ItemStack item) {
        this.hoverItem = item;
        return this;
    }

    @LuaWhitelist
    public void setToggleTexture(@LuaNotNil FiguraTexture texture) {
        setToggleTexture(texture, 0, 0, 0, 0, 0d);
    }

    @LuaWhitelist
    public void setToggleTexture(@LuaNotNil FiguraTexture texture, double u, double v) {
        setToggleTexture(texture, u, v, 0, 0, 0d);
    }

    @LuaWhitelist
    public void setToggleTexture(@LuaNotNil FiguraTexture texture, double u, double v, Integer width, Integer height) {
        setToggleTexture(texture, u, v, width, height, 0d);
    }

    @LuaWhitelist
    public void setToggleTexture(@LuaNotNil FiguraTexture texture, double u, double v, Integer width, Integer height, Double scale) {
        this.toggleTexture = new TextureData(texture, u, v, width, height, scale);
    }

    @LuaWhitelist
    public Action toggleTexture(@LuaNotNil FiguraTexture texture) {
        return toggleTexture(texture, 0, 0, 0, 0, 0d);
    }

    @LuaWhitelist
    public Action toggleTexture(@LuaNotNil FiguraTexture texture, double u, double v) {
        return toggleTexture(texture, u, v, 0, 0, 0d);
    }

    @LuaWhitelist
    public Action toggleTexture(@LuaNotNil FiguraTexture texture, double u, double v, Integer width, Integer height) {
        return toggleTexture(texture, u, v, width, height, 0d);
    }

    @LuaWhitelist
    public Action toggleTexture(@LuaNotNil FiguraTexture texture, double u, double v, Integer width, Integer height, Double scale) {
        setToggleTexture(texture, u, v, width, height, scale);
        return this;
    }

    @LuaWhitelist
    public boolean isToggled() {
        return this.toggled;
    }

    @LuaWhitelist
    public void setToggled(boolean bool) {
        this.toggled = bool;
    }

    @LuaWhitelist
    public Action toggled(boolean bool) {
        setToggled(bool);
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
