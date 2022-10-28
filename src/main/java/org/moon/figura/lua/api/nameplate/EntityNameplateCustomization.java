package org.moon.figura.lua.api.nameplate;

import org.moon.figura.lua.LuaWhitelist;
import org.moon.figura.lua.docs.LuaTypeDoc;
import org.moon.figura.math.vector.FiguraVec3;
import org.moon.figura.math.vector.FiguraVec4;
import org.moon.figura.utils.ColorUtils;
import org.moon.figura.utils.LuaUtils;

@LuaWhitelist
@LuaTypeDoc(
        name = "EntityNameplateCustomization",
        value = "nameplate_entity"
)
public class EntityNameplateCustomization extends NameplateCustomization {

    private FiguraVec3 position;
    private FiguraVec3 scale;
    public Integer background;
    public Double alpha;

    @LuaWhitelist
    public boolean visible = true;
    @LuaWhitelist
    public boolean shadow;
    @LuaWhitelist
    public boolean outline;

    @LuaWhitelist
    public FiguraVec3 getPos() {
        return this.position;
    }

    @LuaWhitelist
    public void setPos() {
        this.position = null;
    }

    @LuaWhitelist
    public void setPos(Double x, Double y, Double z) {
        setPos(LuaUtils.freeVec3("setPos", x, y, z));
    }

    @LuaWhitelist
    public void setPos(FiguraVec3 pos) {
        this.position = pos.copy();
    }

    @LuaWhitelist
    public FiguraVec3 getScale() {
        return this.scale;
    }

    @LuaWhitelist
    public void setScale() {
        this.scale = null;
    }

    @LuaWhitelist
    public void setScale(Double x, Double y, Double z) {
        setScale(LuaUtils.freeVec3("setScale", x, y, z, 1, 1, 1));
    }

    @LuaWhitelist
    public void setScale(FiguraVec3 scale) {
        this.scale = scale.copy();
    }

    @LuaWhitelist
    public void setBackgroundColor() {
        this.background = null;
        this.alpha = null;
    }

    @LuaWhitelist
    public void setBackgroundColor(Double r, Double g, Double b) {
        setBackgroundColor(LuaUtils.freeVec3("setBackgroundColor", r, g, b));
    }

    @LuaWhitelist
    public void setBackgroundColor(FiguraVec3 rgb) {
        this.background = ColorUtils.rgbToInt(rgb);
        this.alpha = null;
    }

    @LuaWhitelist
    public void setBackgroundColor(Double r, Double g, Double b, Double a) {
        setBackgroundColor(LuaUtils.freeVec3("setBackgroundColor", r, g, b), a);
    }

    @LuaWhitelist
    public void setBackgroundColor(FiguraVec4 rgba) {
        setBackgroundColor(FiguraVec3.oneUse(rgba.x, rgba.y, rgba.z), rgba.w);
    }

    @LuaWhitelist
    public void setBackgroundColor(FiguraVec3 rgb, Double a) {
        this.background = ColorUtils.rgbToInt(rgb);
        this.alpha = a;
    }

    @LuaWhitelist
    public Object __index(String arg) {
        if (arg == null) return null;
        return switch (arg) {
            case "visible" -> visible;
            case "shadow" -> shadow;
            case "outline" -> outline;
            default -> null;
        };
    }

    @LuaWhitelist
    public void __newindex(String key, boolean value) {
        if (key == null) return;
        switch (key) {
            case "visible" -> visible = value;
            case "shadow" -> shadow = value;
            case "outline" -> outline = value;
        }
    }

    @Override
    public String toString() {
        return "EntityNameplateCustomization";
    }
}
