package org.moon.figura.model.rendertasks;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix3f;
import com.mojang.math.Matrix4f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import org.luaj.vm2.LuaError;
import org.moon.figura.lua.LuaNotNil;
import org.moon.figura.lua.LuaWhitelist;
import org.moon.figura.lua.docs.LuaMethodDoc;
import org.moon.figura.lua.docs.LuaTypeDoc;
import org.moon.figura.math.vector.FiguraVec2;
import org.moon.figura.math.vector.FiguraVec3;
import org.moon.figura.math.vector.FiguraVec4;
import org.moon.figura.model.PartCustomization;
import org.moon.figura.model.rendering.texture.FiguraTexture;
import org.moon.figura.model.rendering.texture.RenderTypes;
import org.moon.figura.utils.ColorUtils;

@LuaWhitelist
@LuaTypeDoc(
        name = "SpriteTask",
        value = "sprite_task"
)
public class SpriteTask extends RenderTask {

    private ResourceLocation texture;
    private int textureW = -1, textureH = -1;
    private int width, height;
    private int regionW, regionH;
    private float u = 0f, v = 0f;
    private int r = 0xFF, g = 0xFF, b = 0xFF, a = 0xFF;
    private RenderTypes renderType = RenderTypes.TRANSLUCENT;

    public SpriteTask(String name) {
        super(name);
    }

    @Override
    public boolean render(PartCustomization.Stack stack, MultiBufferSource buffer, int light, int overlay) {
        if (!enabled || texture == null || renderType == RenderTypes.NONE)
            return false;

        this.pushOntoStack(stack); //push
        PoseStack poseStack = stack.peek().copyIntoGlobalPoseStack();
        poseStack.scale(-1, -1, 1);

        //prepare variables
        Matrix4f pose = poseStack.last().pose();
        Matrix3f normal = poseStack.last().normal();

        int newLight = this.light != null ? this.light : light;
        int newOverlay = this.overlay != null ? this.overlay : overlay;
        float u2 = u + regionW / (float) textureW;
        float v2 = v + regionH / (float) textureH;

        //setup texture render
        VertexConsumer consumer = buffer.getBuffer(renderType.get(texture));

        //create vertices
        consumer.vertex(pose, 0f, height, 0f).color(r, g, b, a).uv(u, v2).overlayCoords(newOverlay).uv2(newLight).normal(normal, 0f, 0f, -1f).endVertex();
        consumer.vertex(pose, width, height, 0f).color(r, g, b, a).uv(u2, v2).overlayCoords(newOverlay).uv2(newLight).normal(normal, 0f, 0f, -1f).endVertex();
        consumer.vertex(pose, width, 0f, 0f).color(r, g, b, a).uv(u2, v).overlayCoords(newOverlay).uv2(newLight).normal(normal, 0f, 0f, -1f).endVertex();
        consumer.vertex(pose, 0f, 0f, 0f).color(r, g, b, a).uv(u, v).overlayCoords(newOverlay).uv2(newLight).normal(normal, 0f, 0f, -1f).endVertex();

        stack.pop(); //pop
        return true;
    }

    @Override
    public int getComplexity() {
        return 1; //1 face, 1 complexity
    }


    // -- lua -- //


    @LuaWhitelist
    public String getTexture() {
        return texture == null ? null : texture.toString();
    }
    
    @LuaWhitelist
    public SpriteTask setTexture(@LuaNotNil String textureLocation, Integer width, Integer height){
        if (width == null || height == null)
            throw new LuaError("Texture dimensions cannot be null");
        try {
            ResourceLocation resource = new ResourceLocation(textureLocation);
            //noinspection ConstantValue
            this.texture = Minecraft.getInstance().getTextureManager().getTexture(resource, null) != null ? resource : MissingTextureAtlasSprite.getLocation();
            return this;
        } catch (Exception e) {
            throw new LuaError(e.getMessage());
        }
    }
    
    @LuaWhitelist
    public SpriteTask setTexture(FiguraTexture texture){
        if(texture == null){
            this.texture = null;
            return this;
        } else {
            return setTexture(texture, texture.getWidth(), texture.getHeight());
        }
    }

    @LuaWhitelist
    @LuaMethodDoc("texture")
    public SpriteTask setTexture(FiguraTexture texture, Integer width, Integer height) {
        if (texture == null) {
            this.texture = null;
            return this;
        }
        
        this.texture = texture.getLocation();
        if (width == null || height == null) {
            width = texture.getWidth();
            height = texture.getHeight();
        }

        if (width <= 0 || height <= 0)
            throw new LuaError("Invalid texture size: " + width + "x" + height);

        this.textureW = this.regionW = this.width = width;
        this.textureH = this.regionH = this.height = height;
        return this;
    }

    @LuaWhitelist
    public FiguraVec2 getDimensions() {
        return FiguraVec2.of(textureW, textureH);
    }

    @LuaWhitelist
    public SpriteTask setDimensions(@LuaNotNil FiguraVec2 dimensions){
        return setDimensions(dimensions.x, dimensions.y);
    }
    
    @LuaWhitelist
    @LuaMethodDoc("dimensions")
    public SpriteTask setDimensions(double w, double h) {
        this.textureW = (int) Math.round(w);
        this.textureH = (int) Math.round(h);
        return this;
    }

    @LuaWhitelist
    public FiguraVec2 getSize() {
        return FiguraVec2.of(width, height);
    }
    
    @LuaWhitelist
    public SpriteTask setSize(@LuaNotNil FiguraVec2 size){
        return setSize(size.x, size.y);
    }

    @LuaWhitelist
    @LuaMethodDoc("size")
    public SpriteTask setSize(double width, double height) {
        this.width = (int) Math.round(width);
        this.height = (int) Math.round(height);
        return this;
    }

    @LuaWhitelist
    public FiguraVec2 getRegion() {
        return FiguraVec2.of(regionW, regionH);
    }
    
    @LuaWhitelist
    public SpriteTask setRegion(@LuaNotNil FiguraVec2 region){
        return setRegion(region.x, region.y);
    }

    @LuaWhitelist
    @LuaMethodDoc("region")
    public SpriteTask setRegion(double width, double height) {
        this.regionW = (int) Math.round(width);
        this.regionH = (int) Math.round(height);
        return this;
    }

    @LuaWhitelist
    public FiguraVec2 getUV() {
        return FiguraVec2.of(u, v);
    }

    @LuaWhitelist
    public SpriteTask setUV(@LuaNotNil FiguraVec2 uv){
        return setUV(uv.x, uv.y);
    }
    
    @LuaWhitelist
    @LuaMethodDoc("uv")
    public SpriteTask setUV(double u, double v) {
        this.u = (float) u;
        this.v = (float) v;
        return this;
    }

    @LuaWhitelist
    public FiguraVec2 getUVPixels() {
        if (this.textureW == -1 || this.textureH == -1)
            throw new LuaError("Cannot call getUVPixels before defining the texture dimensions!");
        return getUV().mul(this.textureW, this.textureH);
    }

    @LuaWhitelist
    public SpriteTask setUVPixels(@LuaNotNil FiguraVec2 uvPixels){
        return setUVPixels(uvPixels.x, uvPixels.y);
    }
    
    @LuaWhitelist
    @LuaMethodDoc("uvPixels")
    public SpriteTask setUVPixels(double u, double v) {
        if (this.textureW == -1 || this.textureH == -1)
            throw new LuaError("Cannot call setUVPixels before defining the texture dimensions!");

        setUV(u / this.textureW, v / this.textureH);
        return this;
    }

    @LuaWhitelist
    public FiguraVec4 getColor() {
        return FiguraVec4.of(r, g, b, a).scale(1f / 255f);
    }
    
    @LuaWhitelist
    public SpriteTask setColor(@LuaNotNil FiguraVec3 rgb){
        return setColor(FiguraVec4.oneUse(rgb.x, rgb.y, rgb.z, 1));
    }
    
    @LuaWhitelist
    public SpriteTask setColor(double r, double g, double b, Double a){
        return setColor(FiguraVec4.oneUse(r, g, b, a == null? 1 : a));
    }

    @LuaWhitelist
    @LuaMethodDoc("color")
    public SpriteTask setColor(@LuaNotNil FiguraVec4 rgba) {
        int i = ColorUtils.rgbaToInt(rgba);
        this.r = i >> 24 & 0xFF;
        this.g = i >> 16 & 0xFF;
        this.b = i >> 8 & 0xFF;
        this.a = i & 0xFF;
        return this;
    }

    @LuaWhitelist
    public String getRenderType() {
        return renderType.name();
    }

    @LuaWhitelist
    @LuaMethodDoc("renderType")
    public SpriteTask setRenderType(@LuaNotNil String renderType) {
        try {
            this.renderType = RenderTypes.valueOf(renderType.toUpperCase());
            return this;
        } catch (Exception ignored) {
            throw new LuaError("Illegal RenderType: \"" + renderType + "\".");
        }
    }

    @Override
    public String toString() {
        return name + " (Sprite Render Task)";
    }
}
