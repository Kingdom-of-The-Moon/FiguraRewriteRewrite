package org.moon.figura.gui.widgets.lists;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.AbstractContainerWidget;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import org.moon.figura.gui.widgets.AbstractTreeElement;
import org.moon.figura.gui.widgets.FiguraWidget;
import org.moon.figura.gui.widgets.TextField;
import org.moon.figura.utils.FiguraText;
import org.moon.figura.utils.TextUtils;
import org.moon.figura.utils.ui.UIHelper;

import java.util.ArrayList;
import java.util.List;

public class DocsList extends AbstractList{
    public DocsList(int x, int y, int width, int height) {
        super(x, y, width, height);
        children.add(new TextField(x + 4, y + 4, width - 8, 20, TextField.HintType.SEARCH, this::onSearchTextChanged));
        scrollBar.setY(y+26);
        scrollBar.setHeight(height-28);
        scrollBar.visible = true;
        var testDocsListElement = new DocsTreeElement(x+4,y+28,width-12-scrollBar.getWidth());
        testDocsListElement.setTitle("Test");
        for (int i = 0; i < 3; i++) {
            var testChildDocElement = new DocsTreeElement(0,0,0);
            testChildDocElement.setTitle("Test%s".formatted(i));
            testDocsListElement.getChildren().add(testChildDocElement);
        }
        testDocsListElement.setExpanded(true);
        children.add(testDocsListElement);
        updateScissors(4,4,-4,-4);
    }

    @Override
    public void render(PoseStack stack, int mouseX, int mouseY, float delta) {
        UIHelper.renderSliced(stack, x, y, width, height, UIHelper.OUTLINE_FILL);
        super.render(stack, mouseX, mouseY, delta);
    }

    private void onSearchTextChanged(String searchString) {

    }
    private static Font getFont() {
        return Minecraft.getInstance().font;
    }
    private static class DocsTreeElement extends AbstractTreeElement<DocsTreeElement> {
        private String title;
        private Component renderText = null;
        private boolean updateRenderText;
        private AbstractWidget pageWidget = null;

        private void updateRenderText() {
            renderText = Component.literal(title);
            updateRenderText = false;
        }

        public DocsTreeElement(int x, int y, int width) {
            super(x, y, width);
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            if (this.title == null || !this.title.equals(title)) {
                this.title = title;
                updateRenderText = true;
            }
        }

        public boolean canBeSelected() {
            return pageWidget != null;
        }

        public AbstractWidget getPageWidget() {
            return pageWidget;
        }

        public void setPageWidget(AbstractWidget pageWidget) {
            this.pageWidget = pageWidget;
        }

        @Override
        public void renderElement(PoseStack matrices, int mouseX, int mouseY, float delta) {
            if (updateRenderText) updateRenderText();
            int x = getX();
            int y = getY();
            int width = getWidth();
            int height = getActualHeight();
            boolean canBeExpanded = canBeExpanded();
            int textX = x + (canBeExpanded ? 12 : 4);
            int textWidth = width - (4 + (canBeExpanded ? 12 : 4));
            UIHelper.renderSliced(matrices, x, y, width, height, UIHelper.OUTLINE_FILL);
            UIHelper.renderScrollingText(matrices, renderText, textX,y+4, textWidth, height-8, 0xFFFFFF);
            if (canBeExpanded()) {
                Component arrow = isExpanded() ? UIHelper.DOWN_ARROW : UIHelper.UP_ARROW;
                getFont().draw(matrices, arrow, x + 4, y+(height / 2f)-4f, 0xFF404040);
            }
        }
    }
}
