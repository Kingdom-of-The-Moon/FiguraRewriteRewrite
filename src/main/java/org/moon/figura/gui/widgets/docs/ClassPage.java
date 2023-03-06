package org.moon.figura.gui.widgets.docs;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import org.moon.figura.gui.screens.DocsScreen;
import org.moon.figura.gui.widgets.AbstractContainerElement;
import org.moon.figura.gui.widgets.Label;
import org.moon.figura.gui.widgets.ScrollBarWidget;
import org.moon.figura.gui.widgets.lists.AbstractList;
import org.moon.figura.lua.docs.FiguraDoc;
import org.moon.figura.utils.FiguraText;
import org.moon.figura.utils.TextUtils;
import org.moon.figura.utils.ui.UIHelper;

import java.util.ArrayList;
import java.util.List;

public class ClassPage extends AbstractContainerElement implements DocsPage {
    public ClassPage(int x, int y, int width, int height, FiguraDoc.ClassDoc doc) {
        super(x, y, width, height);
        int classPageListWidth = (int)((width / 3f) * 2) - 4;
        int classContentsWidth = (int)((width / 3f) * 1);
        ClassPageList classPageList = new ClassPageList(x, y, classPageListWidth, height, doc);
        children.add(classPageList);
        ClassContents classContents = new ClassContents(x+classPageListWidth+4, y, classContentsWidth, height, doc);
        children.add(classContents);
    }

    @Override
    public void goTo(String destination) {

    }

    @Override
    public void render(PoseStack stack, int mouseX, int mouseY, float delta) {
        //fill(stack, x,y,x+width,y+height, 0xBBFF72AD);
        super.render(stack, mouseX, mouseY, delta);
    }

    private static class ClassContents extends AbstractList {
        private final Label classNameLabel;
        private final Label fieldsLabel;
        private final Label functionsLabel;
        private final List<Label> fieldLabels = new ArrayList<>();
        private final List<Label> functionLabels = new ArrayList<>();
        private final List<Label> contents = new ArrayList<>();
        private final int maxScroll;

        @Override
        public List<? extends GuiEventListener> contents() {
            return contents;
        }

        public ClassContents(int x, int y, int width, int height, FiguraDoc.ClassDoc doc) {
            super(x, y, width, height);
            Component classLabelComponent = Component.literal(doc.name).withStyle(
                    DocsScreen.ACCENT_COLOR.style.withClickEvent(new TextUtils.FiguraClickEvent(
                            () -> goTo("c$")
                    ))
            );
            // Class label
            classNameLabel = new Label(classLabelComponent,x+4,y+4, TextUtils.Alignment.LEFT);
            classNameLabel.maxWidth = width - 22;
            classNameLabel.wrap = true;
            classNameLabel.setScale(2f);
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
                fieldsLabel.wrap = true;
                fieldsLabel.setScale(1.5f);
                children.add(fieldsLabel);
                contents.add(fieldsLabel);
                yOffset += fieldsLabel.getHeight() + 2;
                for (FiguraDoc.FieldDoc f :
                        doc.documentedFields) {
                    MutableComponent fieldLabelComponent = Component.literal(f.name)
                            .withStyle(Style.EMPTY
                                    .withClickEvent(new TextUtils.FiguraClickEvent(
                                            () -> goTo("fl$"+f.name)
                                    ))
                            );
                    Label fieldLabel = new Label(fieldLabelComponent, x+4,yOffset, TextUtils.Alignment.LEFT);
                    fieldLabel.maxWidth = width - 22;
                    fieldLabel.wrap = true;
                    fieldLabel.setScale(1);
                    yOffset += fieldLabel.getHeight() + 2;
                    fieldLabels.add(fieldLabel);
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
                functionsLabel.wrap = true;
                functionsLabel.setScale(1.5f);
                children.add(functionsLabel);
                contents.add(functionsLabel);
                yOffset += functionsLabel.getHeight() + 2;
                for (FiguraDoc.MethodDoc f :
                        doc.documentedMethods) {
                    MutableComponent fieldLabelComponent = Component.literal(f.name)
                            .withStyle(Style.EMPTY
                                    .withClickEvent(new TextUtils.FiguraClickEvent(
                                            () -> goTo("fn$"+f.name)
                                    ))
                            );
                    Label functionLabel = new Label(fieldLabelComponent, x+4,yOffset, TextUtils.Alignment.LEFT);
                    functionLabel.maxWidth = width - 22;
                    functionLabel.wrap = true;
                    functionLabel.setScale(1);
                    yOffset += functionLabel.getHeight() + 2;
                    functionLabels.add(functionLabel);
                    children.add(functionLabel);
                    contents.add(functionLabel);
                }
            }
            int h = yOffset - y;
            maxScroll = Math.max(0, h - height);
            children.remove(scrollBar);
            children.add(scrollBar);
            scrollBar.visible = true;
            updateScissors(4,4,-4,-4);
        }

        @Override
        public void render(PoseStack stack, int mouseX, int mouseY, float delta) {
            int scroll = (int)(scrollBar.getScrollProgress() * maxScroll);
            int yOffset = y + 4 - scroll;
            classNameLabel.y = yOffset;
            yOffset += classNameLabel.getHeight() + 2;
            if (fieldsLabel != null) {
                fieldsLabel.y = yOffset;
                yOffset += fieldsLabel.getHeight() + 2;
                for (var f :
                        fieldLabels) {
                    f.y = yOffset;
                    yOffset += f.getHeight() + 2;
                }
            }
            if (functionsLabel != null) {
                functionsLabel.y = yOffset;
                yOffset += functionsLabel.getHeight() + 2;
                for (var f :
                        functionLabels) {
                    f.y = yOffset;
                    yOffset += f.getHeight() + 2;
                }
            }
            UIHelper.renderSliced(stack, x,y,width,height,UIHelper.OUTLINE_FILL);
            super.render(stack, mouseX, mouseY, delta);
            UIHelper.setupScissor(x+scissorsX, y+scissorsY, width+scissorsWidth, height+scissorsHeight);
            for (var c :
                    contents) {
                c.render(stack, mouseX, mouseY, delta);
            }
            UIHelper.disableScissor();
        }

        public void goTo(String destination) {

        }
    }
    private static class ClassPageList extends AbstractList {

        public ClassPageList(int x, int y, int width, int height, FiguraDoc.ClassDoc doc) {
            super(x, y, width, height);
            scrollBar.visible = true;
        }

        @Override
        public void render(PoseStack stack, int mouseX, int mouseY, float delta) {
            UIHelper.renderSliced(stack, x,y,width,height, UIHelper.OUTLINE_FILL);
            super.render(stack, mouseX, mouseY, delta);
        }
    }
}
