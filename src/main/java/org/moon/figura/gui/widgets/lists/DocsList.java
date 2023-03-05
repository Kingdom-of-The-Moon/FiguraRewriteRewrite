package org.moon.figura.gui.widgets.lists;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.AbstractContainerWidget;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
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
        var testDocsListElement = new DocsListElement(x+4,y+28,width-12-scrollBar.getWidth());
        testDocsListElement.setTitle("Test");
        var testDocsListElement2 = new DocsListElement(x+4,y+28,width-12-scrollBar.getWidth());
        testDocsListElement2.setTitle("Test2");
        testDocsListElement.children.add(testDocsListElement2);
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
    private static class DocsListElement extends AbstractContainerWidget implements FiguraWidget {
        private static final List<DocsListElement> EMPTY_LIST = List.of();
        private boolean visible = true;
        private static final int LIST_ELEMENT_HEIGHT = 17;
        private static final int LIST_ELEMENT_X_OFFSET = 3;
        private static final int LIST_ELEMENT_Y_OFFSET = 3;
        private ArrayList<DocsListElement> children = new ArrayList<>();
        private String title;
        private Component renderText = null;
        private boolean updateRenderText;
        private boolean expanded;
        private AbstractWidget pageWidget = null;
        public DocsListElement(int x, int y, int width) {
            super(x, y, width, LIST_ELEMENT_HEIGHT, Component.empty());
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

        private void updateRenderText() {
            renderText = Component.literal(title);
            updateRenderText = false;
        }

        @Override
        protected List<? extends AbstractWidget> getContainedChildren() {
            return expanded ? children : EMPTY_LIST;
        }

        public List<DocsListElement> getChildren() {
            return children;
        }

        @Override
        public void updateWidgetNarration(NarrationElementOutput builder) {
            builder.add(NarratedElementType.POSITION, title);
        }

        @Override
        public boolean isVisible() {
            return visible;
        }

        @Override
        public void setVisible(boolean visible) {
            this.visible = visible;
        }

        public boolean canBeExpanded() {
            return children.size() > 0;
        }

        public boolean isExpanded() {
            return canBeExpanded() &&  expanded;
        }

        public void setExpanded(boolean expanded) {
            this.expanded = expanded;
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
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            if (!(visible && active)) return false;
            return super.mouseClicked(mouseX, mouseY, button);
        }
        @Override
        public boolean mouseReleased(double mouseX, double mouseY, int button) {
            if (!(visible && active)) return false;
            return super.mouseReleased(mouseX, mouseY, button);
        }
        @Override
        public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
            if (!(visible && active)) return false;
            return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
        }

        private int getActualHeight() {
            return LIST_ELEMENT_HEIGHT;
        }

        @Override
        public int getHeight() {
            int height = getActualHeight();
            if (children.size() > 0 && isExpanded()) {
                height += ((LIST_ELEMENT_HEIGHT+LIST_ELEMENT_Y_OFFSET) * children.size()) - LIST_ELEMENT_Y_OFFSET;
            }
            return height;
        }

        @Override
        public void render(PoseStack matrices, int mouseX, int mouseY, float delta) {
            if (!visible) return;
            int x = getX();
            int y = getY();
            int width = getWidth();
            int height = getActualHeight();
            if (updateRenderText) updateRenderText();
            UIHelper.renderSliced(matrices, x, y, width, height, UIHelper.OUTLINE_FILL);
            UIHelper.renderScrollingText(matrices, renderText, x+4,y+4, width - 8, height-8, 0xFFFFFF);
            if (isExpanded()) {
                int yOffset = y+height+LIST_ELEMENT_Y_OFFSET;
                for (var e :
                        children) {
                    if (e.visible) {
                        e.setWidth(width-LIST_ELEMENT_X_OFFSET);
                        e.setX(x+LIST_ELEMENT_X_OFFSET);
                        e.setY(yOffset);
                        yOffset += e.getHeight() + LIST_ELEMENT_Y_OFFSET;
                    }
                }
            }
            super.render(matrices, mouseX, mouseY, delta);
        }
    }
}
