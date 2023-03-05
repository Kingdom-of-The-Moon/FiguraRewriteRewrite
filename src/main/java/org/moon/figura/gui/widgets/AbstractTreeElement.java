package org.moon.figura.gui.widgets;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.AbstractContainerWidget;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import org.moon.figura.gui.widgets.lists.DocsList;
import org.moon.figura.utils.ui.UIHelper;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractTreeElement <T extends AbstractTreeElement<?>> extends AbstractContainerWidget implements FiguraWidget {
    private final List<T> EMPTY_LIST = List.of();
    private boolean visible = true;
    private static final int LIST_ELEMENT_HEIGHT = 17;
    private static final int LIST_ELEMENT_X_OFFSET = 4;
    private static final int LIST_ELEMENT_Y_OFFSET = 3;
    private static final int TREE_LINE_START_OFFSET = 1;
    private static final int TREE_LINE_COLOR = 0xFF404040;
    private final ArrayList<T> children = new ArrayList<>();
    private boolean expanded;
    public AbstractTreeElement(int x, int y, int width) {
        super(x, y, width, LIST_ELEMENT_HEIGHT, Component.empty());
    }
    public int getActualHeight() {
        return LIST_ELEMENT_HEIGHT;
    }
    public int getLineStartOffset() {
        return TREE_LINE_START_OFFSET;
    }
    public int getLineColor() {
        return TREE_LINE_COLOR;
    }
    public int getElementHeight() {
        return LIST_ELEMENT_HEIGHT;
    }
    public int getElementXOffset() {
        return LIST_ELEMENT_X_OFFSET;
    }
    public int getElementYOffset() {
        return LIST_ELEMENT_Y_OFFSET;
    }
    @Override
    protected List<? extends AbstractWidget> getContainedChildren() {
        return expanded ? children : EMPTY_LIST;
    }

    public List<T> getChildren() {
        return children;
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
        return getDrawnCount() > 0;
    }

    public boolean isExpanded() {
        return canBeExpanded() &&  expanded;
    }

    public void setExpanded(boolean expanded) {
        this.expanded = expanded;
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

    private int getDrawnCount() {
        int drawnCount = 0;
        for (var e : children) {
            if (e.isVisible()) drawnCount++;
        }
        return drawnCount;
    }
    @Override
    public int getHeight() {
        int height = getActualHeight();
        if (children.size() > 0 && isExpanded()) {
            height += ((LIST_ELEMENT_HEIGHT+LIST_ELEMENT_Y_OFFSET) * children.size()) - LIST_ELEMENT_Y_OFFSET;
        }
        return height;
    }

    public abstract void renderElement(PoseStack matrices, int mouseX, int mouseY, float delta);

    @Override
    public void render(PoseStack matrices, int mouseX, int mouseY, float delta) {
        if (!isVisible()) return;
        renderElement(matrices, mouseX, mouseY, delta);
        int x = getX();
        int y = getY();
        int width = getWidth();
        int height = getActualHeight();

        int elementXOffset = getElementXOffset();
        int elementYOffset = getElementYOffset();
        int lineStartOffset = getLineStartOffset();
        int lineColor = getLineColor();

        if (isExpanded()) {
            int startYOffset = y+height;
            int yOffset = y+height+elementYOffset;
            int drawnCount = getDrawnCount();
            int[] linePositions = new int[drawnCount];
            int i = 0;
            for (var e :
                    children) {
                if (e.isVisible()) {
                    e.setWidth(width-elementXOffset);
                    e.setX(x+elementXOffset);
                    e.setY(yOffset);
                    linePositions[i] = yOffset + (e.getActualHeight()/2);
                    yOffset += e.getHeight() + elementYOffset;
                    i++;
                }
            }
            fill(matrices, x+lineStartOffset, startYOffset, x+lineStartOffset+1, linePositions[linePositions.length-1], lineColor);
            for (int j = 0; j < drawnCount; j++) {
                int lineYPos = linePositions[j];
                fill(matrices,x+lineStartOffset+1, lineYPos, x+elementXOffset, lineYPos+1, lineColor);
            }
        }
        super.render(matrices, mouseX, mouseY, delta);
    }
}