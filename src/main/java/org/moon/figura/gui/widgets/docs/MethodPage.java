package org.moon.figura.gui.widgets.docs;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import org.moon.figura.gui.screens.DocsScreen;
import org.moon.figura.gui.widgets.AbstractContainerElement;
import org.moon.figura.gui.widgets.Label;
import org.moon.figura.lua.docs.FiguraDoc;
import org.moon.figura.lua.docs.FiguraDocsManager;
import org.moon.figura.utils.FiguraText;
import org.moon.figura.utils.TextUtils;

import java.util.ArrayList;
import java.util.List;

public class MethodPage extends AbstractContainerElement implements DocsPage {
    private static final int Y_OFFSET = 2;
    private final String name;
    private int width, height;
    private final Label methodNameLabel;
    private final Label descriptionLabel;
    private OnClassSwitch callback;
    private final List<Label> overloadLabels = new ArrayList<>();
    public MethodPage(int x, int y, int width, int height, FiguraDoc.MethodDoc doc) {
        super(x, y, width, height);
        name = doc.name;
        methodNameLabel = new Label(
                Component.literal(doc.name).withStyle(DocsScreen.ACCENT_COLOR.style),
                x, y, TextUtils.Alignment.LEFT);
        int yOffset = methodNameLabel.y + methodNameLabel.getHeight() + Y_OFFSET;
        children.add(methodNameLabel);
        for (int i = 0; i < doc.parameterNames.length; i++) {
            String[] paramNames = doc.parameterNames[i];
            Class<?>[] paramTypes = doc.parameterTypes[i];
            Class<?> returnType = doc.returnTypes[i];
            MutableComponent overloadComponent = Component.literal(doc.name);
            overloadComponent.append("(");
            for (int j = 0; j < paramNames.length; j++) {
                Class<?> type = paramTypes[j];
                String className = FiguraDocsManager.getNameFor(type);
                overloadComponent.append(Component.literal(className).withStyle(
                        DocsScreen.ACCENT_COLOR.style.withClickEvent(
                                new TextUtils.FiguraClickEvent(() -> {
                                    if (callback != null) callback.onSwitch(type);
                                })
                        )
                ));
                overloadComponent.append(" "+paramNames[j]);
                if (j < paramNames.length - 1) overloadComponent.append(", ");
            }
            overloadComponent.append(") ");
            overloadComponent.append(FiguraText.of("docs.text.returns").append(" "));
            String className = FiguraDocsManager.getNameFor(returnType);
            overloadComponent.append(Component.literal(className).withStyle(
                    DocsScreen.ACCENT_COLOR.style.withClickEvent(
                            new TextUtils.FiguraClickEvent(() -> {
                                if (callback != null) callback.onSwitch(returnType);
                            })
                    )
            ));
            Label overloadLabel = new Label(overloadComponent, x + 4, yOffset, width - 4, true);
            overloadLabels.add(overloadLabel);
            yOffset += overloadLabel.getHeight() + Y_OFFSET;
        }
        children.addAll(overloadLabels);
        descriptionLabel = new Label(FiguraText.of("docs."+doc.description), x+4, yOffset, width - 4, true);
        children.add(descriptionLabel);
        yOffset += descriptionLabel.getHeight();
        this.height = yOffset - y;
    }

    public int getWidth() {
        return super.width;
    }

    public int getHeight() {
        return height;
    }

    public OnClassSwitch getCallback() {
        return callback;
    }
    public void setCallback(OnClassSwitch callback) {
        this.callback = callback;
    }

    @Override
    public void render(PoseStack stack, int mouseX, int mouseY, float delta) {
        int yOffset = y;
        methodNameLabel.y = yOffset;
        yOffset += methodNameLabel.getHeight() + Y_OFFSET;
        for (Label l :
                overloadLabels) {
            l.y = yOffset;
            yOffset += l.getHeight() + Y_OFFSET;
        }
        descriptionLabel.y = yOffset;
        super.render(stack, mouseX, mouseY, delta);
    }

    public String name() {
        return name;
    }
}
