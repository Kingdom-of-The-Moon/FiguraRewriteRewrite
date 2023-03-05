package org.moon.figura.gui.screens;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.moon.figura.gui.widgets.Label;
import org.moon.figura.gui.widgets.TextField;
import org.moon.figura.gui.widgets.lists.DocsList;
import org.moon.figura.lua.docs.FiguraDoc;
import org.moon.figura.utils.FiguraText;
import org.moon.figura.utils.TextUtils;
import org.moon.figura.utils.ui.UIHelper;

public class DocsScreen extends AbstractPanelScreen {

    public static final Component TITLE = FiguraText.of("gui.panels.title.docs");

    public DocsScreen(Screen parentScreen) {
        super(parentScreen, TITLE, DocsScreen.class);
    }

    @Override
    public Component getTitle() {
        return TITLE;
    }
    private DocsList docsList;
    @Override
    protected void init() {
        super.init();
        int yOffset = panels.height;
        docsList = new DocsList(4, yOffset,(width / 4)-8,height-yOffset-4, this);
        addRenderableWidget(docsList);
    }

    @Override
    public void render(PoseStack stack, int mouseX, int mouseY, float delta) {
        super.render(stack, mouseX, mouseY, delta);
        int yOffset = panels.height;
        UIHelper.renderSliced(stack, (int)(width / 4f), yOffset, (int)((width / 4f) * 2f), height - yOffset - 4, UIHelper.OUTLINE_FILL);
        UIHelper.renderSliced(stack, (int)((width / 4f) * 3f) + 4, yOffset, (int)(width / 4f) - 8, height - yOffset - 4, UIHelper.OUTLINE_FILL);
    }

    public void onSelect(FiguraDoc doc) {
        System.out.println(doc.name);
    }

}
