package org.moon.figura.gui.screens;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import org.moon.figura.gui.widgets.docs.DocsPage;
import org.moon.figura.gui.widgets.lists.DocsList;
import org.moon.figura.lua.docs.FiguraDoc;
import org.moon.figura.lua.docs.FiguraDocsManager;
import org.moon.figura.utils.ColorUtils;
import org.moon.figura.utils.FiguraText;
import org.moon.figura.utils.TextUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DocsScreen extends AbstractPanelScreen {
    public static final ColorUtils.Colors ACCENT_COLOR = ColorUtils.Colors.FRAN_PINK;
    public static final Component TITLE = FiguraText.of("gui.panels.title.docs");
    private static FiguraDoc selectedDoc;
    private DocsPage currentPage;
    private static DocsScreen currentInstance;
    public DocsScreen(Screen parentScreen) {
        super(parentScreen, TITLE, DocsScreen.class);
    }


    @Override
    public Component getTitle() {
        return TITLE;
    }
    @Override
    protected void init() {
        super.init();
        int yOffset = panels.height;
        addRenderableWidget(new DocsList(4, yOffset,(width / 4)-8,height-yOffset-4));
        currentInstance = this;
        update();
    }
    @Override
    public void render(PoseStack stack, int mouseX, int mouseY, float delta) {
        super.render(stack, mouseX, mouseY, delta);
    }

    public static void onSelect(FiguraDoc doc) {
        selectedDoc = doc;
        currentInstance.update();
    }
    public static FiguraDoc getSelectedDoc() {
        return selectedDoc;
    }
    private void update() {
        if (currentPage != null) {
            removeWidget(currentPage);
        }
        if (selectedDoc != null) {
            int yOffset = panels.height;
            currentPage = selectedDoc.toWidget((width / 4), yOffset, (int)((width / 4f)*3) - 4, height - yOffset - 4);
            if (currentPage != null) addRenderableWidget(currentPage);
        }
    }

    public static MutableComponent getClassComponent(Class<?> clazz) {
        String name = FiguraDocsManager.getNameFor(clazz);
        FiguraDoc doc = FiguraDocsManager.getGlobalDoc(clazz);
        Style s = Style.EMPTY.withUnderlined(doc != null);
        if (doc != null) {
            s = s.withClickEvent(new TextUtils.FiguraClickEvent(() -> onSelect(doc)));
        }
        return Component.literal(name).withStyle(s);
    }
}
