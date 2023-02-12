package org.moon.figura.lua.api.nameplate;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import org.moon.figura.lua.LuaNotNil;
import org.moon.figura.lua.LuaWhitelist;
import org.moon.figura.lua.docs.LuaMethodDoc;
import org.moon.figura.lua.docs.LuaTypeDoc;
import org.moon.figura.math.vector.FiguraVec2;
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

    private FiguraVec3 pivot, position, scale;
    public Integer background, outlineColor, light;

    public boolean visible = true;
    public boolean shadow, outline;

    @LuaWhitelist
    public FiguraVec3 getPivot() {
        return this.pivot;
    }

    @LuaWhitelist
    @LuaMethodDoc("pivot")
    public EntityNameplateCustomization setPivot(Object x, Double y, Double z) {
        this.pivot = x == null ? null : LuaUtils.parseVec3("setPivot", x, y, z);
        return this;
    }

    @LuaWhitelist
    public EntityNameplateCustomization pivot(Object x, Double y, Double z) {
        return setPivot(x, y, z);
    }

    @LuaWhitelist
    public FiguraVec3 getPos() {
        return this.position;
    }

    @LuaWhitelist
    public EntityNameplateCustomization setPos(@LuaNotNil double x, double y, double z) {
        return setPos(LuaUtils.freeVec3("setPos", x, y, z));
    }

    @LuaWhitelist
    @LuaMethodDoc("pos")
    public EntityNameplateCustomization setPos(FiguraVec3 pos) {
        this.position = pos == null? null : pos.copy();
        return this;
    }

    @LuaWhitelist
    public FiguraVec3 getScale() {
        return this.scale;
    }

    @LuaWhitelist
    public EntityNameplateCustomization setScale(@LuaNotNil Double x, Double y, Double z) {
        return setScale(LuaUtils.freeVec3("setScale", x, y, z, 1, 1, 1));
    }

    @LuaWhitelist
    @LuaMethodDoc("scale")
    public EntityNameplateCustomization setScale(FiguraVec3 scale) {
        this.scale = scale == null ? null : scale.copy();
        return this;
    }

    @LuaWhitelist
    public FiguraVec4 getBackgroundColor() {
        return this.background == null ? null : ColorUtils.intToARGB(this.background);
    }

    @LuaWhitelist
    public EntityNameplateCustomization setBackgroundColor(@LuaNotNil FiguraVec3 rgb, Double a) {
        return setBackgroundColor(rgb.x, rgb.y, rgb.z, a);
    }

    @LuaWhitelist
    public EntityNameplateCustomization setBackgroundColor(@LuaNotNil double r, double g, double b, Double a) {
        return setBackgroundColor(FiguraVec4.oneUse(r, g, b, a == null? Minecraft.getInstance().options.getBackgroundOpacity(0.25f) : a));
    }

    @LuaWhitelist
    @LuaMethodDoc({"backgroundColor", "setBackgroundColour", "backgroundColour"})
    public EntityNameplateCustomization setBackgroundColor(FiguraVec4 rgba) {
        this.background = rgba == null ? null : ColorUtils.rgbaToIntABGR(rgba);
        return this;
    }

    @LuaWhitelist
    public EntityNameplateCustomization setOutlineColor(double r, double g, double b) {
        return setOutlineColor(FiguraVec3.oneUse(r, g, b));
    }

    @LuaWhitelist
    @LuaMethodDoc("outlineColor")
    public EntityNameplateCustomization setOutlineColor(@LuaNotNil FiguraVec3 rgb) {
        this.outlineColor = ColorUtils.rgbToInt(rgb);
        return this;
    }

    @LuaWhitelist
    public FiguraVec2 getLight() {
        return light == null ? null : FiguraVec2.of(LightTexture.block(light), LightTexture.sky(light));
    }

    @LuaWhitelist
    public EntityNameplateCustomization setLight(@LuaNotNil FiguraVec2 light) {
        return setLight(light.x, light.y);
    }

    @LuaWhitelist
    @LuaMethodDoc("light")
    public EntityNameplateCustomization setLight(Double blockLight, double skyLight) {
        this.light = blockLight == null ? null : LightTexture.pack(blockLight.intValue(), (int) skyLight);
        return this;
    }

    @LuaWhitelist
    public boolean isVisible() {
        return visible;
    }

    @LuaWhitelist
    @LuaMethodDoc("visible")
    public EntityNameplateCustomization setVisible(boolean visible) {
        this.visible = visible;
        return this;
    }

    @LuaWhitelist
    public boolean hasOutline() {
        return outline;
    }

    @LuaWhitelist
    @LuaMethodDoc("outline")
    public EntityNameplateCustomization setOutline(boolean outline) {
        this.outline = outline;
        return this;
    }

    @LuaWhitelist
    public boolean hasShadow() {
        return shadow;
    }

    @LuaWhitelist
    @LuaMethodDoc("shadow")
    public EntityNameplateCustomization setShadow(boolean shadow) {
        this.shadow = shadow;
        return this;
    }

    @Override
    public String toString() {
        return "EntityNameplateCustomization";
    }
}
