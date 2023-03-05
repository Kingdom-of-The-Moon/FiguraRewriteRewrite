package org.moon.figura.gui.widgets.docs;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import org.moon.figura.gui.screens.DocsScreen;
import org.moon.figura.gui.widgets.Label;
import org.moon.figura.gui.widgets.ScrollBarWidget;
import org.moon.figura.gui.widgets.lists.AbstractList;
import org.moon.figura.lua.docs.FiguraDoc;
import org.moon.figura.utils.FiguraText;
import org.moon.figura.utils.TextUtils;

import java.util.ArrayList;
import java.util.List;

public class ClassPage extends AbstractList implements DocsPage {
    public ClassPage(int x, int y, int width, int height, FiguraDoc.ClassDoc doc) {
        super(x, y, width, height);
    }

    @Override
    public void goTo(String destination) {

    }

    public static class ClassContents extends AbstractList {
        private final Label classNameLabel;
        private final Label fieldsLabel;
        private final Label functionsLabel;
        private final List<Label> fieldLabels = new ArrayList<>();
        private final List<Label> functionLabels = new ArrayList<>();
        private final List<Label> contents = new ArrayList<>();
        private final ScrollBarWidget scrollBar;
        private final int maxScroll;

        @Override
        public List<? extends GuiEventListener> contents() {
            return contents;
        }

        public ClassContents(int x, int y, int width, int height, FiguraDoc.ClassDoc doc) {
            super(x, y, width, height);
            scrollBar = new ScrollBarWidget(x + width - 14,y+4,10,height-8, 0);
            Component classLabelComponent = Component.literal(doc.name).withStyle(
                    DocsScreen.ACCENT_COLOR.style.withClickEvent(new TextUtils.FiguraClickEvent(
                            () -> goTo("c$")
                    ))
            );
            // Class label
            classNameLabel = new Label(classLabelComponent,x+4,y+4, TextUtils.Alignment.LEFT);
            classNameLabel.maxWidth = width - 22;
            classNameLabel.setScale(1.25f);
            children.add(classNameLabel);
            contents.add(classNameLabel);
            int yOffset = classNameLabel.y + classNameLabel.getHeight() + 2;

            // Fields labels
            if (doc.documentedFields.size() == 0) fieldsLabel = null;
            else {
                MutableComponent fieldsLabelComponent = FiguraText.of("gui.docs.contents.fields")
                        .withStyle(DocsScreen.ACCENT_COLOR.style
                                .withClickEvent(new TextUtils.FiguraClickEvent(
                                        () -> goTo("t$fl")
                                ))
                        );
                fieldsLabel = new Label(fieldsLabelComponent, x+4, yOffset, TextUtils.Alignment.LEFT);
                fieldsLabel.maxWidth = width - 22;
                fieldsLabel.setScale(1.1f);
                children.add(fieldsLabel);
                contents.add(fieldsLabel);
                yOffset += fieldsLabel.getHeight() + 2;
                for (FiguraDoc.FieldDoc f :
                        doc.documentedFields) {
                    MutableComponent fieldLabelComponent = Component.literal(f.name)
                            .withStyle(DocsScreen.ACCENT_COLOR.style
                                    .withClickEvent(new TextUtils.FiguraClickEvent(
                                            () -> goTo("fl$"+f.name)
                                    ))
                            );
                    Label fieldLabel = new Label(fieldLabelComponent, x+4,yOffset, TextUtils.Alignment.LEFT);
                    fieldLabel.maxWidth = width - 22;
                    fieldLabel.setScale(1);
                    yOffset += fieldLabel.getHeight() + 2;
                    children.add(fieldLabel);
                    contents.add(fieldLabel);
                }
            }

            // Functions labels
            if (doc.documentedMethods.size() == 0) functionsLabel = null;
            else {
                MutableComponent functionsLabelComponent = FiguraText.of("gui.docs.contents.functions")
                        .withStyle(DocsScreen.ACCENT_COLOR.style
                                .withClickEvent(new TextUtils.FiguraClickEvent(
                                        () -> goTo("t$fn")
                                ))
                        );
                functionsLabel = new Label(functionsLabelComponent, x+4, yOffset, TextUtils.Alignment.LEFT);
                functionsLabel.maxWidth = width - 22;
                functionsLabel.setScale(1.1f);
                children.add(functionsLabel);
                contents.add(functionsLabel);
                yOffset += functionsLabel.getHeight() + 2;
                for (FiguraDoc.MethodDoc f :
                        doc.documentedMethods) {
                    MutableComponent fieldLabelComponent = Component.literal(f.name)
                            .withStyle(DocsScreen.ACCENT_COLOR.style
                                    .withClickEvent(new TextUtils.FiguraClickEvent(
                                            () -> goTo("fn$"+f.name)
                                    ))
                            );
                    Label functionLabel = new Label(fieldLabelComponent, x+4,yOffset, TextUtils.Alignment.LEFT);
                    functionLabel.maxWidth = width - 22;
                    functionLabel.setScale(1);
                    yOffset += functionLabel.getHeight() + 2;
                    children.add(functionLabel);
                    contents.add(functionLabel);
                }
            }
            int h = yOffset - y;
            maxScroll = Math.max(0, h - height);
        }

        @Override
        public void render(PoseStack stack, int mouseX, int mouseY, float delta) {
            int scroll = (int)(scrollBar.getScrollProgress() * maxScroll);
            super.render(stack, mouseX, mouseY, delta);
        }

        public void goTo(String destination) {

        }
    }

}
