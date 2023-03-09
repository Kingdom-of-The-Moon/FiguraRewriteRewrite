package org.moon.figura.gui.widgets.docs;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.sounds.SoundEvents;
import org.moon.figura.gui.screens.DocsScreen;
import org.moon.figura.gui.widgets.AbstractContainerElement;
import org.moon.figura.gui.widgets.Label;
import org.moon.figura.gui.widgets.lists.AbstractList;
import org.moon.figura.lua.docs.FiguraDoc;
import org.moon.figura.lua.docs.FiguraDocsManager;
import org.moon.figura.utils.FiguraText;
import org.moon.figura.utils.TextUtils;
import org.moon.figura.utils.ui.UIHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ClassPage extends AbstractContainerElement implements DocsPage {
    public ClassPage(int x, int y, int width, int height, FiguraDoc.ClassDoc doc) {
        super(x, y, width, height);
        int classPageListWidth = (int)((width / 3f) * 2) - 4;
        int classContentsWidth = (int)((width / 3f) * 1);
        ClassPageList classPageList = new ClassPageList(x, y, classPageListWidth, height, doc);
        children.add(classPageList);
        ClassContents classContents = new ClassContents(x+classPageListWidth+4, y, classContentsWidth, height, doc);
        classContents.setCallback(classPageList::goTo);
        children.add(classContents);
    }

    @Override
    public void render(PoseStack stack, int mouseX, int mouseY, float delta) {
        //fill(stack, x,y,x+width,y+height, 0xBBFF72AD);
        super.render(stack, mouseX, mouseY, delta);
    }
    private static void playClickSound() {
        Minecraft client = Minecraft.getInstance();
        SoundManager soundManager = client.getSoundManager();
        soundManager.play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
    }
    private static class ClassContents extends AbstractList {
        private final Label classNameLabel;
        private final Label fieldsLabel;
        private final Label functionsLabel;
        private GoToCallback callback;
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
            Component classLabelComponent = Component.literal(doc.name).withStyle(DocsScreen.ACCENT_COLOR.style);
            // Class label
            classNameLabel = new Label(classLabelComponent,x+4,y+4, TextUtils.Alignment.LEFT);
            classNameLabel.maxWidth = width - 22;
            classNameLabel.wrap = true;
            classNameLabel.setScale(2f);
            contents.add(classNameLabel);
            int yOffset = classNameLabel.y + classNameLabel.getHeight() + 2;

            // Fields labels
            if (doc.documentedFields.size() == 0) fieldsLabel = null;
            else {
                List<String> documentedFields = new ArrayList<>();
                MutableComponent fieldsLabelComponent = FiguraText.of("gui.docs.fields")
                        .withStyle(DocsScreen.ACCENT_COLOR.style
                                .withClickEvent(new TextUtils.FiguraClickEvent(
                                        () -> goTo("t$fl")
                                ))
                        );
                fieldsLabel = new Label(fieldsLabelComponent, x+4, yOffset, TextUtils.Alignment.LEFT);
                fieldsLabel.maxWidth = width - 22;
                fieldsLabel.wrap = true;
                fieldsLabel.setScale(1.5f);
                contents.add(fieldsLabel);
                yOffset += fieldsLabel.getHeight() + 2;
                for (FiguraDoc.FieldDoc f :
                        doc.documentedFields) {
                    if (documentedFields.contains(f.name)) continue;
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
                    documentedFields.add(f.name);
                }
                contents.addAll(fieldLabels);
            }

            // Functions labels
            if (doc.documentedMethods.size() == 0) functionsLabel = null;
            else {
                List<String> documentedMethods = new ArrayList<>();
                MutableComponent functionsLabelComponent = FiguraText.of("gui.docs.functions")
                        .withStyle(DocsScreen.ACCENT_COLOR.style
                                .withClickEvent(new TextUtils.FiguraClickEvent(
                                        () -> goTo("t$fn")
                                ))
                        );
                functionsLabel = new Label(functionsLabelComponent, x+4, yOffset, TextUtils.Alignment.LEFT);
                functionsLabel.maxWidth = width - 22;
                functionsLabel.wrap = true;
                functionsLabel.setScale(1.5f);
                contents.add(functionsLabel);
                yOffset += functionsLabel.getHeight() + 2;
                for (FiguraDoc.MethodDoc f :
                        doc.documentedMethods) {
                    if (documentedMethods.contains(f.name)) continue;
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
                    documentedMethods.add(f.name);
                }
                contents.addAll(functionLabels);
            }
            children.addAll(contents);
            int h = yOffset - y;
            maxScroll = Math.max(0, h - height);
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
            if (callback != null) {
                callback.goTo(destination);
            }
        }

        public GoToCallback getCallback() {
            return callback;
        }

        public void setCallback(GoToCallback callback) {
            this.callback = callback;
        }

        public interface GoToCallback {
            void goTo(String destination);
        }
    }
    private static class ClassPageList extends AbstractList {
        private static final int TITLE_Y_OFFSET = 16;
        private static final int Y_OFFSET = 8;
        private static final int HEADER_OFFSET = 2;
        private final List<GuiEventListener> contents = new ArrayList<>();
        private final int maxScroll;
        private final Label classNameLabel;
        private final Label extendsLabel;
        private final Label descriptionLabel;
        private final Label fieldsLabel;
        private final Label functionsLabel;
        private final HashMap<Object, Integer> yOffsets = new HashMap<>();
        private final List<FieldPage> fieldPages = new ArrayList<>();
        private final List<MethodPage> methodsPages = new ArrayList<>();

        @Override
        public List<? extends GuiEventListener> contents() {
            return contents;
        }

        private void openClassPage(Class<?> clazz) {
            FiguraDoc cd = FiguraDocsManager.getGlobalDoc(clazz);
            if (cd != null) {
                DocsScreen.onSelect(cd);
            }
        }
        public ClassPageList(int x, int y, int width, int height, FiguraDoc.ClassDoc doc) {
            super(x, y, width, height);
            int yOffset = y+4;

            int w = scrollBar.getWidth();
            int scissorsYOffset = 0;
            classNameLabel = new Label(Component.literal(doc.name).withStyle(DocsScreen.ACCENT_COLOR.style),
                            x+4,yOffset, width - 8 - w, true);
            classNameLabel.setScale(3);
            children.add(classNameLabel);
            scissorsYOffset += classNameLabel.getHeight() + HEADER_OFFSET;
            yOffset += classNameLabel.getHeight() + HEADER_OFFSET;
            if (doc.superclass != null) {
                MutableComponent extendsComponent = FiguraText.of("docs.text.extends").append(" ").append(
                        DocsScreen.getClassComponent(doc.superclass).withStyle(DocsScreen.ACCENT_COLOR.style)
                );
                extendsLabel = new Label(extendsComponent, x+4, yOffset, width - 8 - w, true);
                children.add(extendsLabel);
                yOffset += extendsLabel.getHeight() + HEADER_OFFSET;
                scissorsYOffset += extendsLabel.getHeight() + HEADER_OFFSET;
            }
            else extendsLabel = null;

            descriptionLabel = new Label(FiguraText.of("docs."+doc.description),
                    x+4, yOffset, width - 8 - w, true);
            yOffset += descriptionLabel.getHeight() + HEADER_OFFSET;
            scissorsYOffset += descriptionLabel.getHeight() + HEADER_OFFSET;
            children.add(descriptionLabel);
            int relativeYOffset = 0;
            if (doc.documentedFields.size() > 0) {
                List<String> documentedFields = new ArrayList<>();
                fieldsLabel = new Label(FiguraText.of("gui.docs.fields").withStyle(DocsScreen.ACCENT_COLOR.style),
                        x+4, yOffset+relativeYOffset, width - 8- w, true);
                fieldsLabel.setScale(2);
                yOffsets.put(fieldsLabel, relativeYOffset);
                contents.add(fieldsLabel);
                relativeYOffset += fieldsLabel.getHeight() + Y_OFFSET;
                for (FiguraDoc.FieldDoc fieldDoc :
                        doc.documentedFields) {
                    if (documentedFields.contains(fieldDoc.name)) continue;
                    FieldPage page = fieldDoc.toWidget(x+8, yOffset+relativeYOffset, width - 16 - w, 0);
                    page.setCallback(this::openClassPage);
                    yOffsets.put(page, relativeYOffset);
                    relativeYOffset += page.getHeight() + Y_OFFSET;
                    fieldPages.add(page);
                    documentedFields.add(fieldDoc.name);
                }
                contents.addAll(fieldPages);
            }
            else fieldsLabel = null;
            if (doc.documentedMethods.size() > 0) {
                if (doc.documentedFields.size() > 0) relativeYOffset += TITLE_Y_OFFSET;
                List<String> documentedMethods = new ArrayList<>();
                functionsLabel = new Label(FiguraText.of("gui.docs.functions").withStyle(DocsScreen.ACCENT_COLOR.style),
                        x+4, yOffset+relativeYOffset, width - 8 - w, true);
                functionsLabel.setScale(2);
                yOffsets.put(functionsLabel, relativeYOffset);
                contents.add(functionsLabel);
                relativeYOffset += functionsLabel.getHeight() + Y_OFFSET;
                for (FiguraDoc.MethodDoc methodDoc :
                        doc.documentedMethods) {
                    if (documentedMethods.contains(methodDoc.name)) continue;
                    MethodPage page = methodDoc.toWidget(x+8, yOffset+relativeYOffset, width - 16 - w, 0);
                    page.setCallback(this::openClassPage);
                    yOffsets.put(page, relativeYOffset);
                    relativeYOffset += page.getHeight() + Y_OFFSET;
                    methodsPages.add(page);
                    documentedMethods.add(methodDoc.name);
                }
                contents.addAll(methodsPages);
            }
            else functionsLabel = null;

            children.addAll(contents);
            scrollBar.visible = true;
            maxScroll = Math.max(0, relativeYOffset-(height - (8+scissorsYOffset)));
            updateScissors(4,4 + (scissorsYOffset),-4,-(4+scissorsYOffset));
        }

        @Override
        public void render(PoseStack stack, int mouseX, int mouseY, float delta) {
            UIHelper.renderSliced(stack, x,y,width,height, UIHelper.OUTLINE_FILL);
            super.render(stack, mouseX, mouseY, delta);
            int scroll = (int)(scrollBar.getScrollProgress() * maxScroll);
            int yOffset = y+4-scroll + classNameLabel.getHeight() + descriptionLabel.getHeight() + (HEADER_OFFSET * 2);
            if (extendsLabel != null) {
                yOffset += extendsLabel.getHeight() + HEADER_OFFSET;
            }
            UIHelper.setupScissor(x+scissorsX, y+scissorsY, width+scissorsWidth, height+scissorsHeight);
            if(fieldsLabel != null) {
                fieldsLabel.y = yOffset + yOffsets.get(fieldsLabel);
                fieldsLabel.render(stack, mouseX, mouseY, delta);
                for (FieldPage page :
                        fieldPages) {
                    page.y = yOffset + yOffsets.get(page);
                    page.render(stack, mouseX, mouseY, delta);
                }
            }
            if(functionsLabel != null) {
                functionsLabel.y = yOffset + yOffsets.get(functionsLabel);
                functionsLabel.render(stack, mouseX, mouseY, delta);
                for (MethodPage page :
                        methodsPages) {
                    page.y = yOffset + yOffsets.get(page);
                    page.render(stack, mouseX, mouseY, delta);
                }
            }
            UIHelper.disableScissor();
        }
        private void scrollTo(int i) {
            scrollBar.setScrollProgress(Math.min(i, maxScroll) / (float) maxScroll);
        }

        private FieldPage getFieldPageByName(String name) {
            for (var f :
                    fieldPages) {
                if (f.name().equals(name)) return f;
            }
            return null;
        }

        private MethodPage getMethodPageByName(String name) {
            for (var m :
                 methodsPages) {
                if (m.name().equals(name)) return m;
            }
            return null;
        }

        public void goTo(String destination) {
            if (maxScroll == 0) return;
            int i = destination.indexOf("$");
            if (i == -1) return;
            String prefix = destination.substring(0, i);
            switch (prefix) {
                case "c" -> scrollTo(yOffsets.get(classNameLabel));
                case "t", "fn", "fl" -> {
                    String suffix = destination.substring(i+1);
                    switch (prefix) {
                        case "fl" -> {
                            FieldPage page = getFieldPageByName(suffix);
                            if (page != null) scrollTo(yOffsets.get(page));
                        }
                        case "fn" -> {
                            MethodPage page = getMethodPageByName(suffix);
                            if (page != null) scrollTo(yOffsets.get(page));
                        }
                        default -> {
                            if (suffix.equals("fl")) scrollTo(yOffsets.get(fieldsLabel));
                            else scrollTo(yOffsets.get(functionsLabel));
                        }
                    }
                }
            }
        }

    }
}
