package org.moon.figura.lua.api.action_wheel;

import net.minecraft.world.item.ItemStack;
import org.luaj.vm2.LuaFunction;
import org.moon.figura.avatar.Avatar;
import org.moon.figura.lua.LuaNotNil;
import org.moon.figura.lua.LuaWhitelist;
import org.moon.figura.lua.api.world.ItemStackAPI;
import org.moon.figura.lua.docs.LuaFieldDoc;
import org.moon.figura.lua.docs.LuaMethodDoc;
import org.moon.figura.lua.docs.LuaMethodOverload;
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
    public Action title(String title) {
        this.title = title;
        return this;
    }

    @LuaWhitelist
    public FiguraVec3 getColor() {
        return this.color;
    }

    @LuaWhitelist
    public Action color(Double r, Double g, Double b){
        return color(LuaUtils.freeVec3("hoverColor", r, g, b));
    }

    @LuaWhitelist
    public Action color(@LuaNotNil FiguraVec3 color) {
        this.color = color.copy();
        return this;
    }

    @LuaWhitelist
    public FiguraVec3 getHoverColor() {
        return this.hoverColor;
    }

    @LuaWhitelist
    public Action hoverColor(Double r, Double g, Double b){
        return hoverColor(LuaUtils.freeVec3("hoverColor", r, g, b));
    }

    @LuaWhitelist
    public Action hoverColor(@LuaNotNil FiguraVec3 color) {
        this.hoverColor = color.copy();
        return this;
    }

    @LuaWhitelist
    public Action item(String itemId){
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
    public Action hoverItem(String itemId){
        return hoverItem(LuaUtils.parseItemStack("hoverItem", itemId));
    }

    @LuaWhitelist
    public Action hoverItem(ItemStackAPI item) {
        return item(item.itemStack);
    }

    public Action hoverItem(ItemStack item) {
        this.item = item;
        return this;
    }

    @LuaWhitelist
    public Action texture(@LuaNotNil FiguraTexture texture) {
        return texture(texture, 0, 0, 0, 0, 0D);
    }

    @LuaWhitelist
    public Action texture(@LuaNotNil FiguraTexture texture, double u, double v) {
        return texture(texture, u, v, 0, 0, 0D);
    }

    @LuaWhitelist
    public Action texture(@LuaNotNil FiguraTexture texture, double u, double v, Integer width, Integer height) {
        return texture(texture, u, v, width, height, 0D);
    }

    @LuaWhitelist
    public Action texture(@LuaNotNil FiguraTexture texture, double u, double v, Integer width, Integer height, Double scale) {
        this.texture = new TextureData(texture, u, v, width, height, scale);
        return this;
    }

    @LuaWhitelist
    public Action hoverTexture(@LuaNotNil FiguraTexture texture) {
        return hoverTexture(texture, 0, 0, 0, 0, 0D);
    }

    @LuaWhitelist
    public Action hoverTexture(@LuaNotNil FiguraTexture texture, double u, double v) {
        return hoverTexture(texture, u, v, 0, 0, 0D);
    }

    @LuaWhitelist
    public Action hoverTexture(@LuaNotNil FiguraTexture texture, double u, double v, Integer width, Integer height) {
        return hoverTexture(texture, u, v, width, height, 0D);
    }

    @LuaWhitelist
    public Action hoverTexture(@LuaNotNil FiguraTexture texture, double u, double v, Integer width, Integer height, Double scale) {
        this.hoverTexture = new TextureData(texture, u, v, width, height, scale);
        return this;
    }


    // -- set functions -- //


    @LuaWhitelist
    public Action onLeftClick(LuaFunction leftFunction) {
        this.leftClick = leftFunction;
        return this;
    }

    @LuaWhitelist
    public Action onRightClick(LuaFunction rightFunction) {
        this.rightClick = rightFunction;
        return this;
    }

    @LuaWhitelist
    public Action onToggle(LuaFunction toggleFunction) {
        this.toggle = toggleFunction;
        return this;
    }

    @LuaWhitelist
    public Action onUntoggle(LuaFunction untoggleFunction) {
        this.untoggle = untoggleFunction;
        return this;
    }

    @LuaWhitelist
    public Action onScroll(LuaFunction scrollFunction) {
        this.scroll = scrollFunction;
        return this;
    }


    // -- toggle specific stuff -- //


    @LuaWhitelist
    public String getToggleTitle() {
        return this.toggleTitle;
    }

    @LuaWhitelist
    public Action toggleTitle(String title) {
        this.toggleTitle = title;
        return this;
    }

    @LuaWhitelist
    public FiguraVec3 getToggleColor() {
        return this.toggleColor;
    }

    @LuaWhitelist
    public Action toggleColor(Double r, Double g, Double b){
        return toggleColor(LuaUtils.freeVec3("toggleColor", r, g, b));
    }

    @LuaWhitelist
    public Action toggleColor(FiguraVec3 color) {
        this.toggleColor = color.copy();
        return this;
    }

    @LuaWhitelist
    public Action toggleItem(String itemId) {
        return toggleItem(LuaUtils.parseItemStack("toggleItem", itemId));
    }

    @LuaWhitelist
    public Action toggleItem(ItemStack item) {
        this.toggleItem = item;
        return this;
    }

    @LuaWhitelist
    public Action toggleTexture(@LuaNotNil FiguraTexture texture) {
        return toggleTexture(texture, 0, 0, 0, 0, 0D);
    }

    @LuaWhitelist
    public Action toggleTexture(@LuaNotNil FiguraTexture texture, double u, double v) {
        return toggleTexture(texture, u, v, 0, 0, 0D);
    }

    @LuaWhitelist
    public Action toggleTexture(@LuaNotNil FiguraTexture texture, double u, double v, Integer width, Integer height) {
        return toggleTexture(texture, u, v, width, height, 0D);
    }

    @LuaWhitelist
    public Action toggleTexture(@LuaNotNil FiguraTexture texture, double u, double v, Integer width, Integer height, Double scale) {
        this.toggleTexture = new TextureData(texture, u, v, width, height, scale);
        return this;
    }

    @LuaWhitelist
    public boolean isToggled() {
        return this.toggled;
    }

    @LuaWhitelist
    public Action toggled(boolean bool) {
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
