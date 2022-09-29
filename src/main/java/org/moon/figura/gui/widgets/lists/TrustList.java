package org.moon.figura.gui.widgets.lists;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.Mth;
import org.moon.figura.FiguraMod;
import org.moon.figura.gui.widgets.SliderWidget;
import org.moon.figura.gui.widgets.SwitchButton;
import org.moon.figura.trust.TrustContainer;
import org.moon.figura.trust.TrustOption;
import org.moon.figura.utils.FiguraText;
import org.moon.figura.utils.ui.UIHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TrustList extends AbstractList {

    private final List<TrustSlider> sliders = new ArrayList<>();
    private final List<TrustSwitch> switches = new ArrayList<>();

    private final Map<String, List<CustomTrustElement>> customTrustElements = new HashMap<>();

    public TrustList(int x, int y, int width, int height) {
        super(x, y, width, height);
    }

    @Override
    public void render(PoseStack stack, int mouseX, int mouseY, float delta) {
        //background and scissors
        UIHelper.renderSliced(stack, x, y, width, height, UIHelper.OUTLINE);
        UIHelper.setupScissor(x + scissorsX, y + scissorsY, width + scissorsWidth, height + scissorsHeight);

        //scrollbar
        Font font = Minecraft.getInstance().font;
        int lineHeight = font.lineHeight;
        int entryHeight = 27 + lineHeight; //11 (slider) + font height + 16 (padding)
        int customElementsCount = 0;
        for (Map.Entry<String, List<CustomTrustElement>> kl:
                customTrustElements.entrySet()) {
            List<CustomTrustElement> l = kl.getValue();
            for (CustomTrustElement v:
                 l) {
                customElementsCount++;
            }
        }
        int totalHeight = ((sliders.size() + switches.size() + customElementsCount) * entryHeight)
                +
                (customTrustElements.size() * (16+lineHeight));
        scrollBar.y = y + 4;
        scrollBar.visible = totalHeight > height;
        scrollBar.setScrollRatio(entryHeight, totalHeight - height);

        //render sliders
        int xOffset = scrollBar.visible ? 8 : 15;
        int yOffset = scrollBar.visible ? (int) -(Mth.lerp(scrollBar.getScrollProgress(), -16, totalHeight - height)) : 16;
        for (TrustSlider slider : sliders) {
            slider.x = x + xOffset;
            slider.y = y + yOffset;
            yOffset += 27 + lineHeight;
        }

        //render switches
        for (TrustSwitch trustSwitch : switches) {
            trustSwitch.x = x + xOffset;
            trustSwitch.y = y + yOffset;
            yOffset += 27 + lineHeight;
        }


        //Render custom elements
        for (Map.Entry<String, List<CustomTrustElement>> kl:
             customTrustElements.entrySet()) {
            String k  = kl.getKey();
            var title = FiguraText.of("trust.custom."+k).setStyle(FiguraMod.getAccentColor());
            int textWidth = font.width(title);
            int textXPos = ((((width - textWidth - xOffset - 30) / 2)));
            font.draw(stack, title,
                    x + xOffset + textXPos, y + yOffset + 8, 0xFFFFFFFF);
            yOffset += 16 + lineHeight;
            List<CustomTrustElement> l = kl.getValue();
            for (CustomTrustElement e:
                 l) {
                if (e instanceof TrustRange trustRange) {
                    trustRange.x = x + xOffset;
                    trustRange.y = y + yOffset;
                    yOffset += 27 + lineHeight;
                } else if (e instanceof TrustToggle trustToggle) {
                    trustToggle.x = x + xOffset;
                    trustToggle.y = y + yOffset;
                    yOffset += 27 + lineHeight;
                }
            }
        }

        //render children
        super.render(stack, mouseX, mouseY, delta);

        //reset scissor
        RenderSystem.disableScissor();
    }

    public void updateList(TrustContainer container) {
        //clear old widgets
        sliders.forEach(children::remove);
        sliders.clear();

        //clear old switches
        switches.forEach(children::remove);
        switches.clear();

        customTrustElements.forEach((key, l) -> {
            l.forEach(children::remove);
            l.clear();
        });
        customTrustElements.clear();

        int lineHeight = Minecraft.getInstance().font.lineHeight;

        //add new sliders
        for (TrustContainer.Trust trust : TrustContainer.Trust.values()) {
            if (!trust.isToggle) {
                TrustSlider slider = new TrustSlider(x + 8, y, width - 30, 11 + lineHeight, container, trust, this);
                sliders.add(slider);
                children.add(slider);
            } else {
                TrustSwitch trustSwitch = new TrustSwitch(x + 8, y, width - 30, 20 + lineHeight, container, trust, this);
                switches.add(trustSwitch);
                children.add(trustSwitch);
            }
        }

        container.getCustomSettings().forEach((k, opts) -> {
            List<CustomTrustElement> elements = new ArrayList<>();
            opts.forEach(opt -> {
                if (opt instanceof TrustOption.Range r) {
                    TrustRange rng = new TrustRange(x + 8, y, width - 30, 11 + lineHeight, k, r, container, this);
                    elements.add(rng);
                    children.add(rng);
                } else if (opt instanceof TrustOption.Toggle t) {
                    TrustToggle tgl = new TrustToggle(x + 8, y, width - 30, 11 + lineHeight, k, t, container, this);
                    elements.add(tgl);
                    children.add(tgl);
                }
            });
            customTrustElements.put(k, elements);
        });
    }

    private static class TrustSlider extends SliderWidget {

        private static final Component INFINITY = FiguraText.of("trust.infinity");

        private final TrustContainer container;
        private final TrustContainer.Trust trust;
        private final TrustList parent;
        private Component value;
        private boolean changed;

        public TrustSlider(int x, int y, int width, int height, TrustContainer container, TrustContainer.Trust trust, TrustList parent) {
            super(x, y, width, height, Mth.clamp(container.get(trust) / (trust.max + 1d), 0d, 1d), trust.max, false);
            this.container = container;
            this.trust = trust;
            this.parent = parent;
            this.value = trust.checkInfinity(container.get(trust)) ? INFINITY : Component.literal(String.valueOf(container.get(trust)));
            this.changed = container.getSettings().containsKey(trust);

            setAction(slider -> {
                //update trust
                int value = (int) ((trust.max + 1d) * slider.getScrollProgress());
                boolean infinity = trust.checkInfinity(value);

                container.getSettings().put(trust, infinity ? Integer.MAX_VALUE : value);
                changed = true;

                //update text
                this.value = infinity ? INFINITY : Component.literal(String.valueOf(value));
            });
        }

        @Override
        public void renderButton(PoseStack stack, int mouseX, int mouseY, float delta) {
            Font font = Minecraft.getInstance().font;

            //button
            stack.pushPose();
            stack.translate(0f, font.lineHeight, 0f);
            super.renderButton(stack, mouseX, mouseY, delta);
            stack.popPose();

            //texts
            MutableComponent name = FiguraText.of("trust." + trust.name().toLowerCase());
            if (changed) name = Component.literal("*").setStyle(FiguraMod.getAccentColor()).append(name).append("*");

            font.draw(stack, name, x + 1, y + 1, 0xFFFFFF);
            font.draw(stack, value.copy().setStyle(FiguraMod.getAccentColor()), x + width - font.width(value) - 1, y + 1, 0xFFFFFF);
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            if (!this.active || !this.isHoveredOrFocused() || !this.isMouseOver(mouseX, mouseY))
                return false;

            if (button == 1) {
                container.getSettings().remove(trust);
                this.parent.updateList(container);
                playDownSound(Minecraft.getInstance().getSoundManager());
                return true;
            }

            return super.mouseClicked(mouseX, mouseY, button);
        }

        @Override
        public boolean isMouseOver(double mouseX, double mouseY) {
            return this.parent.isInsideScissors(mouseX, mouseY) && super.isMouseOver(mouseX, mouseY);
        }
    }

    private static class TrustSwitch extends SwitchButton {

        private final TrustContainer container;
        private final TrustContainer.Trust trust;
        private final TrustList parent;
        private Component value;
        private boolean changed;

        public TrustSwitch(int x, int y, int width, int height, TrustContainer container, TrustContainer.Trust trust, TrustList parent) {
            super(x, y, width, height, trust.asBoolean(container.get(trust)));
            this.container = container;
            this.trust = trust;
            this.parent = parent;
            this.changed = container.getSettings().containsKey(trust);
            this.value = FiguraText.of("trust." + (toggled ? "enabled" : "disabled"));
        }

        @Override
        public void onPress() {
            //update trust
            boolean value = !this.isToggled();

            this.container.getSettings().put(trust, value ? 1 : 0);
            this.changed = true;

            //update text
            this.value = FiguraText.of("trust." + (value ? "enabled" : "disabled"));

            super.onPress();
        }

        @Override
        protected void renderTexture(PoseStack stack, float delta) {
            Font font = Minecraft.getInstance().font;

            //button
            stack.pushPose();
            stack.translate(0f, font.lineHeight, 0f);
            super.renderTexture(stack, delta);
            stack.popPose();

            //texts
            MutableComponent name = FiguraText.of("trust." + trust.name().toLowerCase());
            if (changed) name = Component.literal("*").setStyle(FiguraMod.getAccentColor()).append(name).append("*");

            font.draw(stack, name, x + 1, y + 1, 0xFFFFFF);
            font.draw(stack, value.copy().setStyle(FiguraMod.getAccentColor()), x + width - font.width(value) - 1, y + 1, 0xFFFFFF);
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            if (!this.active || !this.isHoveredOrFocused() || !this.isMouseOver(mouseX, mouseY))
                return false;

            if (button == 1) {
                container.getSettings().remove(trust);
                this.parent.updateList(container);
                playDownSound(Minecraft.getInstance().getSoundManager());
                return true;
            }

            return super.mouseClicked(mouseX, mouseY, button);
        }

        @Override
        public boolean isMouseOver(double mouseX, double mouseY) {
            return this.parent.isInsideScissors(mouseX, mouseY) && super.isMouseOver(mouseX, mouseY);
        }
    }

    private interface CustomTrustElement {}
    public static class TrustRange extends SliderWidget implements CustomTrustElement {

        private Component valuePreview;

        private final TrustContainer container;
        private final TrustList parent;
        private final String optionId;
        private final String settingsId;

        private final TrustOption.Range option;
        public TrustRange(int x, int y, int width, int height, String settingsId, TrustOption.Range option, TrustContainer container, TrustList parent) {
            super(x, y, width, height, (double)((option.value - option.MinValue)) / (option.MaxValue-option.MinValue), (option.MaxValue-option.MinValue)+1, option.ShowSteps);
            valuePreview = Component.literal(option.getValueString());
            this.container = container;
            this.parent = parent;
            this.optionId = option.getName();
            this.option = option;
            this.settingsId = settingsId;
            setAction(slider -> {
                int v = (int) Math.round(Mth.lerp(slider.getScrollProgress(), option.MinValue, option.MaxValue));
                option.value = v;
                valuePreview = Component.literal(option.getValueString());
            });
        }

        @Override
        public void renderButton(PoseStack stack, int mouseX, int mouseY, float delta) {
            Font font = Minecraft.getInstance().font;

            //button
            stack.pushPose();
            stack.translate(0f, font.lineHeight, 0f);
            super.renderButton(stack, mouseX, mouseY, delta);
            stack.popPose();

            //texts
            MutableComponent name = FiguraText.of("trust.custom.%s.%s".formatted(settingsId, optionId));
            if (option.value != option.defaultValue()) name =
                    Component.literal("*").append(name).append("*").setStyle(FiguraMod.getAccentColor());

            font.draw(stack, name, x + 1, y + 1, 0xFFFFFF);
            font.draw(stack, valuePreview.copy().setStyle(FiguraMod.getAccentColor()), x + width - font.width(valuePreview) - 1, y + 1, 0xFFFFFF);
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            if (!this.active || !this.isHoveredOrFocused() || !this.isMouseOver(mouseX, mouseY))
                return false;

            if (button == 1) {
                this.parent.updateList(container);
                playDownSound(Minecraft.getInstance().getSoundManager());
                return true;
            }

            return super.mouseClicked(mouseX, mouseY, button);
        }

        @Override
        public boolean isMouseOver(double mouseX, double mouseY) {
            return this.parent.isInsideScissors(mouseX, mouseY) && super.isMouseOver(mouseX, mouseY);
        }
    }
    private static class TrustToggle extends SwitchButton implements CustomTrustElement {

        private final TrustContainer container;
        private final TrustOption.Toggle option;
        private final TrustList parent;
        private final String settingsId;
        private Component value;
        private boolean changed;

        public TrustToggle(int x, int y, int width, int height, String settingsId, TrustOption.Toggle option, TrustContainer container, TrustList parent) {
            super(x, y, width, height, option.value);
            this.container = container;
            this.option = option;
            this.parent = parent;
            this.settingsId = settingsId;
            this.changed = option.value != option.defaultValue();
            this.value = FiguraText.of("trust." + (toggled ? "enabled" : "disabled"));
        }

        @Override
        public void onPress() {
            //update trust
            boolean value = !this.isToggled();

            option.value = value;
            this.changed = option.value != option.defaultValue();

            //update text
            this.value = FiguraText.of("trust." + (value ? "enabled" : "disabled"));

            super.onPress();
        }

        @Override
        protected void renderTexture(PoseStack stack, float delta) {
            Font font = Minecraft.getInstance().font;

            //button
            stack.pushPose();
            stack.translate(0f, font.lineHeight, 0f);
            super.renderTexture(stack, delta);
            stack.popPose();

            //texts
            MutableComponent name = FiguraText.of("trust.custom.%s.%s".formatted(settingsId, option.getName()));
            if (changed) name = Component.literal("*").setStyle(FiguraMod.getAccentColor()).append(name).append("*");

            font.draw(stack, name, x + 1, y + 1, 0xFFFFFF);
            font.draw(stack, value.copy().setStyle(FiguraMod.getAccentColor()), x + width - font.width(value) - 1, y + 1, 0xFFFFFF);
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            if (!this.active || !this.isHoveredOrFocused() || !this.isMouseOver(mouseX, mouseY))
                return false;

            if (button == 1) {
                container.getSettings().remove(option);
                this.parent.updateList(container);
                playDownSound(Minecraft.getInstance().getSoundManager());
                return true;
            }

            return super.mouseClicked(mouseX, mouseY, button);
        }

        @Override
        public boolean isMouseOver(double mouseX, double mouseY) {
            return this.parent.isInsideScissors(mouseX, mouseY) && super.isMouseOver(mouseX, mouseY);
        }
    }

}
