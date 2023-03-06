package org.moon.figura.gui.widgets.docs;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import org.moon.figura.gui.screens.DocsScreen;
import org.moon.figura.gui.widgets.AbstractContainerElement;
import org.moon.figura.gui.widgets.Label;
import org.moon.figura.lua.docs.FiguraDoc;
import org.moon.figura.lua.docs.FiguraDocsManager;
import org.moon.figura.utils.FiguraText;
import org.moon.figura.utils.TextUtils;

public class FieldPage extends AbstractContainerElement implements DocsPage {
    private int width, height;
    private final Label fieldLabel;
    private OnClassSwitch callback;
    private final String name;
    public FieldPage(int x, int y, int width, int height, FiguraDoc.FieldDoc doc) {
        super(x, y, width, height);
        name = doc.name;
        MutableComponent component = Component.empty();
        component.append(
                Component.literal(FiguraDocsManager.getNameFor(doc.type)).withStyle(
                        DocsScreen.ACCENT_COLOR.style.withClickEvent(
                                new TextUtils.FiguraClickEvent(() -> {
                                    if (callback != null) callback.onSwitch(doc.type);
                                })
                        )
                )
        );
        component.append(" "+doc.name+" ");
        MutableComponent editableComponent = Component.literal("(");
        editableComponent.append(FiguraText.of(doc.editable ? "docs.text.editable" : "docs.text.not_editable"));
        editableComponent.append(")");
        component.append(editableComponent.withStyle(doc.editable ? ChatFormatting.GREEN : ChatFormatting.DARK_RED));
        component.append("\n  ");
        component.append(FiguraText.of("docs."+doc.description));
        fieldLabel = new Label(component, x,y,width, true);
        children.add(fieldLabel);
    }

    public int getWidth() {
        return super.width;
    }

    public int getHeight() {
        return fieldLabel.getHeight();
    }

    public OnClassSwitch getCallback() {
        return callback;
    }

    public void setCallback(OnClassSwitch callback) {
        this.callback = callback;
    }

    @Override
    public void render(PoseStack stack, int mouseX, int mouseY, float delta) {
        fieldLabel.y = y;
        super.render(stack, mouseX, mouseY, delta);
    }

    public String name() {
        return name;
    }
}
