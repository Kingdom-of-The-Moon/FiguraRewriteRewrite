package org.moon.figura.lua.api.nameplate;

import net.minecraft.client.renderer.LightTexture;
import org.moon.figura.lua.LuaNotNil;
import org.moon.figura.lua.LuaWhitelist;
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

    private FiguraVec3 position;
    private FiguraVec3 scale;
    public Integer background, outlineColor, light;
    public Double alpha;

    public boolean visible = true;
    public boolean shadow, outline;

    @LuaWhitelist
    public FiguraVec3 getPos() {
        return this.position;
    }

    @LuaWhitelist
    public void setPos(@LuaNotNil double x, double y, double z) {
        setPos(LuaUtils.freeVec3("setPos", x, y, z));
    }

    @LuaWhitelist
    public void setPos(FiguraVec3 pos) {
        this.position = pos.copy();
    }

    @LuaWhitelist
    public EntityNameplateCustomization pos(@LuaNotNil double x, double y, double z) {
        return pos(FiguraVec3.oneUse(x, y, z));
    }

    @LuaWhitelist
    public EntityNameplateCustomization pos(FiguraVec3 pos) {
        setPos(pos);
        return this;
    }

    @LuaWhitelist
    public FiguraVec3 getScale() {
        return this.scale;
    }

    @LuaWhitelist
    public void setScale(@LuaNotNil Double x, Double y, Double z) {
        setScale(LuaUtils.freeVec3("setScale", x, y, z, 1, 1, 1));
    }

    @LuaWhitelist
    public void setScale(FiguraVec3 scale) {
        this.scale = scale == null ? null : scale.copy();
    }

    @LuaWhitelist
    public EntityNameplateCustomization scale(@LuaNotNil Double x, Double y, Double z) {
        return scale(LuaUtils.freeVec3("scale", x, y, z, 1, 1, 1));
    }

    @LuaWhitelist
    public EntityNameplateCustomization scale(FiguraVec3 scale) {
        setScale(scale);
        return this;
    }

    @LuaWhitelist
    public void setBackgroundColor(Double r, double g, double b, Double a) {
        setBackgroundColor(r == null ? null : FiguraVec3.oneUse(r, g, b), a);
    }

    @LuaWhitelist
    public void setBackgroundColor(@LuaNotNil FiguraVec4 rgba) {
        setBackgroundColor(FiguraVec3.oneUse(rgba.x, rgba.y, rgba.z), rgba.w);
    }

    @LuaWhitelist
    public void setBackgroundColor(@LuaNotNil FiguraVec3 rgb, Double a) {
        this.background = rgb == null ? null : ColorUtils.rgbToInt(rgb);
        this.alpha = a;
    }

    @LuaWhitelist
    public EntityNameplateCustomization backgroundColor(Double r, double g, double b, Double a) {
        return backgroundColor(r == null ? null : FiguraVec3.oneUse(r, g, b), a);
    }

    @LuaWhitelist
    public EntityNameplateCustomization backgroundColor(@LuaNotNil FiguraVec4 rgba) {
        return backgroundColor(FiguraVec3.oneUse(rgba.x, rgba.y, rgba.z), rgba.w);
    }

    @LuaWhitelist
    public EntityNameplateCustomization backgroundColor(@LuaNotNil FiguraVec3 rgb, Double a) {
        setBackgroundColor(rgb, a);
        return this;
    }

    @LuaWhitelist
    public void setOutlineColor(Double r, double g, double b) {
        setOutlineColor(FiguraVec3.oneUse(r, g, b));
    }

    @LuaWhitelist
    public void setOutlineColor(@LuaNotNil FiguraVec3 rgb) {
        this.outlineColor = ColorUtils.rgbToInt(rgb);
    }

    @LuaWhitelist
    public EntityNameplateCustomization outlineColor(double r, double g, double b) {
        return outlineColor(FiguraVec3.oneUse(r, g, b));
    }

    @LuaWhitelist
    public EntityNameplateCustomization outlineColor(@LuaNotNil FiguraVec3 rgb) {
        setOutlineColor(rgb);
        return this;
    }

    @LuaWhitelist
    public FiguraVec2 getLight() {
        return light == null ? null : FiguraVec2.of(LightTexture.block(light), LightTexture.sky(light));
    }

    @LuaWhitelist
    public void setLight(@LuaNotNil FiguraVec2 light) {
        setLight(light.x, light.y);
    }

    @LuaWhitelist
    public void setLight(Double blockLight, double skyLight) {
        this.light = blockLight == null ? null : LightTexture.pack(blockLight.intValue(), (int) skyLight);
    }

    @LuaWhitelist
    public EntityNameplateCustomization light(@LuaNotNil FiguraVec2 light) {
        return light(light.x, light.y);
    }

    @LuaWhitelist
    public EntityNameplateCustomization light(Double blockLight, double skyLight) {
        setLight(blockLight, skyLight);
        return this;
    }

    @LuaWhitelist
    public boolean isVisible() {
        return visible;
    }

    @LuaWhitelist
    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    @LuaWhitelist
    public EntityNameplateCustomization visible(boolean visible) {
        setVisible(visible);
        return this;
    }

    @LuaWhitelist
    public boolean hasOutline() {
        return outline;
    }

    @LuaWhitelist
    public void setOutline(boolean outline) {
        this.outline = outline;
    }

    @LuaWhitelist
    public EntityNameplateCustomization outline(boolean outline) {
        setOutline(outline);
        return this;
    }

    @LuaWhitelist
    public boolean hasShadow() {
        return shadow;
    }

    @LuaWhitelist
    public void setShadow(boolean shadow) {
        this.shadow = shadow;
    }

    @LuaWhitelist
    public EntityNameplateCustomization shadow(boolean shadow) {
        setShadow(shadow);
        return this;
    }

    @Override
    public String toString() {
        return "EntityNameplateCustomization";
    }
}
