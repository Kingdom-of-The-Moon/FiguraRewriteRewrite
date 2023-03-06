package org.moon.figura.gui.screens;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.moon.figura.gui.widgets.docs.DocsPage;
import org.moon.figura.gui.widgets.lists.DocsList;
import org.moon.figura.lua.docs.FiguraDoc;
import org.moon.figura.utils.ColorUtils;
import org.moon.figura.utils.FiguraText;
import org.moon.figura.utils.ui.UIHelper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DocsScreen extends AbstractPanelScreen {
    private static Map<String, List<FiguraDoc>> docs;
    private static final Map<Class<?>, FiguraDoc.ClassDoc> classesToDocs = new HashMap<>();
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
    private static DocsList docsList = null;
    @Override
    protected void init() {
        super.init();
        int yOffset = panels.height;
        if (docsList == null) docsList = new DocsList(4, yOffset,(width / 4)-8,height-yOffset-4);
        else {
            docsList.width = (width / 4) - 8;
            docsList.height = height-yOffset-4;
        }
        addRenderableWidget(docsList);
        currentInstance = this;
        update();
    }
    public static void init(Map<String, List<FiguraDoc>> docs) {
        DocsScreen.docs = docs;
        for (Map.Entry<String, List<FiguraDoc>> entry :
                docs.entrySet()) {
            for (FiguraDoc doc :
                    entry.getValue()) {
                if (doc instanceof FiguraDoc.ClassDoc cd) classesToDocs.put(cd.thisClass, cd);
            }
        }
        DocsList.init(docs);
    }
    public static FiguraDoc.ClassDoc getDocForClass(Class<?> clazz) {
        return classesToDocs.get(clazz);
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
            currentPage = selectedDoc.getDocsWidget((width / 4), yOffset, (int)((width / 4f)*3) - 4, height - yOffset - 4);
            if (currentPage != null) addRenderableWidget(currentPage);
        }
    }
}
