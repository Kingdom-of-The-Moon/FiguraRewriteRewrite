package org.moon.figura.lua.api.nameplate;

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
    public EntityNameplateCustomization setPos(@LuaNotNil double x, double y, double z) {
        return setPos(LuaUtils.freeVec3("setPos", x, y, z));
    }

    @LuaWhitelist
    @LuaMethodDoc("set")
    public EntityNameplateCustomization setPos(FiguraVec3 pos) {
        this.position = pos.copy();
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
    public EntityNameplateCustomization setBackgroundColor(Double r, double g, double b, Double a) {
        return setBackgroundColor(r == null ? null : FiguraVec3.oneUse(r, g, b), a);
    }

    @LuaWhitelist
    public EntityNameplateCustomization setBackgroundColor(@LuaNotNil FiguraVec4 rgba) {
        return setBackgroundColor(FiguraVec3.oneUse(rgba.x, rgba.y, rgba.z), rgba.w);
    }

    @LuaWhitelist
    @LuaMethodDoc({"backgroundColor", "setBackgroundColour", "backgroundColour"})
    public EntityNameplateCustomization setBackgroundColor(@LuaNotNil FiguraVec3 rgb, Double a) {
        this.background = rgb == null ? null : ColorUtils.rgbToInt(rgb);
        this.alpha = a;
        return this;
    }

    @LuaWhitelist
    public EntityNameplateCustomization setOutlineColor(Double r, double g, double b) {
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
