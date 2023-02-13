package org.moon.figura.model.rendertasks;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Matrix4f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import org.luaj.vm2.LuaError;
import org.moon.figura.avatar.Badges;
import org.moon.figura.lua.LuaNotNil;
import org.moon.figura.lua.LuaWhitelist;
import org.moon.figura.lua.docs.LuaMethodDoc;
import org.moon.figura.lua.docs.LuaTypeDoc;
import org.moon.figura.math.vector.FiguraVec3;
import org.moon.figura.math.vector.FiguraVec4;
import org.moon.figura.model.PartCustomization;
import org.moon.figura.utils.ColorUtils;
import org.moon.figura.utils.TextUtils;
import org.moon.figura.utils.ui.UIHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

@LuaWhitelist
@LuaTypeDoc(
        name = "TextTask",
        value = "text_task"
)
public class TextTask extends RenderTask {

    private String textCached;
    private List<Component> text;
    private Alignment alignment = Alignment.LEFT;
    private boolean shadow = false, outline = false;
    private boolean background = false, seeThrough = false;
    private Integer outlineColor, backgroundColor;
    private int width = 0;

    private int cachedComplexity;

    public TextTask(String name) {
        super(name);
    }

    @Override
    public boolean render(PartCustomization.Stack stack, MultiBufferSource buffer, int light, int overlay) {
        if (!enabled || text == null || text.size() == 0)
            return false;

        this.pushOntoStack(stack);
        PoseStack poseStack = stack.peek().copyIntoGlobalPoseStack();
        poseStack.scale(-1, -1, -1);

        Font font = Minecraft.getInstance().font;
        Matrix4f matrix = poseStack.last().pose();

        int l = this.light != null ? this.light : light;
        int bgColor = backgroundColor != null ? backgroundColor : background ? (int) (Minecraft.getInstance().options.getBackgroundOpacity(0.25f) * 0xFF) << 24 : 0;
        int outlineColor = this.outlineColor != null ? this.outlineColor : 0x202020;

        for (int i = 0; i < text.size(); i++) {
            Component text = this.text.get(i);
            int x = -alignment.apply(font, text);
            int y = (font.lineHeight + 1) * i;

            if (background || seeThrough) {
                font.drawInBatch(text, x, y, 0x20FFFFFF, false, matrix, buffer, seeThrough, bgColor, l);
            }

            if (outline) {
                UIHelper.renderOutlineText(poseStack, font, text, x, y, 0xFFFFFF, outlineColor);
            } else {
                font.drawInBatch(text, x, y, 0xFFFFFF, shadow, matrix, buffer, false, 0, l);
            }
        }

        stack.pop();
        return true;
    }

    @Override
    public int getComplexity() {
        return cachedComplexity;
    }

    private void updateText() {
        if (this.textCached == null) {
            this.text = null;
            return;
        }

        Component component = Badges.noBadges4U(TextUtils.tryParseJson(this.textCached));
        if (this.width > 0) {
            List<FormattedCharSequence> warped = TextUtils.wrapText(component, this.width, Minecraft.getInstance().font);
            List<Component> newList = new ArrayList<>();
            for (FormattedCharSequence charSequence : warped)
                newList.add(TextUtils.charSequenceToText(charSequence));
            this.text = newList;
        } else {
            this.text = TextUtils.splitText(component, "\n");
        }
    }


    // -- lua -- //


    @LuaWhitelist
    public String getText() {
        return textCached;
    }

    @LuaWhitelist
    @LuaMethodDoc("text")
    public TextTask setText(String text) {
        this.textCached = text;
        updateText();
        if (text != null) this.cachedComplexity = text.length() + 1;
        return this;
    }
    
    @LuaWhitelist
    public String getAlignment(){
        return this.alignment.name();
    }
    
    @LuaWhitelist
    @LuaMethodDoc("alignment")
    public TextTask setAlignment(@LuaNotNil String alignment){
        try {
            this.alignment = Alignment.valueOf(alignment.toUpperCase());
            return this;
        } catch (Exception exception){
            throw new LuaError("Invalid alignment type\"" + alignment + "\"");
        }
    }

    @LuaWhitelist
    public boolean hasShadow() {
        return this.shadow;
    }

    @LuaWhitelist
    @LuaMethodDoc("shadow")
    public TextTask setShadow(boolean shadow) {
        this.shadow = shadow;
        return this;
    }

    @LuaWhitelist
    public boolean hasOutline() {
        return this.outline;
    }

    @LuaWhitelist
    @LuaMethodDoc("outline")
    public TextTask setOutline(boolean outline) {
        this.outline = outline;
        return this;
    }

    @LuaWhitelist
    public FiguraVec3 getOutlineColor() {
        return ColorUtils.intToRGB(this.outlineColor);
    }

    @LuaWhitelist
    public TextTask setOutlineColor(double x, double y, double z) {
        return setOutlineColor(FiguraVec3.oneUse(x, y, z));
    }
    
    @LuaWhitelist
    @LuaMethodDoc("outlineColor")
    public TextTask setOutlineColor(@LuaNotNil FiguraVec3 color){
        this.outlineColor = ColorUtils.rgbToInt(color);
        return this;
    }
    
    @LuaWhitelist
    public int getWidth(){
        return width;
    }
    
    @LuaWhitelist
    @LuaMethodDoc("width")
    public TextTask setWidth(int width){
        this.width = width;
        updateText();
        return this;
    }

    @LuaWhitelist
    public boolean isSeeThrough() {
        return this.seeThrough;
    }

    @LuaWhitelist
    @LuaMethodDoc("seeThrough")
    public TextTask setSeeThrough(boolean seeThrough) {
        this.seeThrough = seeThrough;
        return this;
    }

    @LuaWhitelist
    public boolean hasBackground() {
        return this.background;
    }

    @LuaWhitelist
    @LuaMethodDoc("background")
    public TextTask setBackground(boolean background) {
        this.background = background;
        return this;
    }

    @LuaWhitelist
    public TextTask background(boolean background) {
        return setBackground(background);
    }

    @LuaWhitelist
    public FiguraVec4 getBackgroundColor() {
        return this.backgroundColor == null ? null : ColorUtils.intToARGB(this.backgroundColor);
    }
    
    @LuaWhitelist
    public TextTask setBackgroundColor(double r, double g, double b, Double a){
        return setBackgroundColor(FiguraVec4.oneUse(r, g, b, a == null? Minecraft.getInstance().options.getBackgroundOpacity(0.25f) : a));
    }

    @LuaWhitelist
    @LuaMethodDoc("backgroundColor")
    public TextTask setBackgroundColor(@LuaNotNil FiguraVec4 color) {
        this.backgroundColor = ColorUtils.rgbaToIntARGB(color);
        return this;
    }

    @Override
    public String toString() {
        return name + " (Text Render Task)";
    }

    private enum Alignment {
        LEFT((font, component) -> 0),
        RIGHT(Font::width),
        CENTER((font, component) -> font.width(component) / 2);

        private final BiFunction<Font, Component, Integer> function;

        Alignment(BiFunction<Font, Component, Integer> function) {
            this.function = function;
        }

        public int apply(Font font, Component component) {
            return function.apply(font, component);
        }
    }
}
