package org.moon.figura.gui.widgets.docs;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.network.chat.Component;
import org.moon.figura.gui.screens.DocsScreen;
import org.moon.figura.gui.widgets.AbstractContainerElement;
import org.moon.figura.gui.widgets.Label;
import org.moon.figura.gui.widgets.lists.AbstractList;
import org.moon.figura.lua.docs.FiguraListDocs;
import org.moon.figura.utils.FiguraText;
import org.moon.figura.utils.TextUtils;
import org.moon.figura.utils.ui.UIHelper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class EnumPage extends AbstractList implements DocsPage {
    private final List<GuiEventListener> contents = new ArrayList<>();
    private final Label titleLabel;
    private final Label descriptionLabel;
    private final LabelTable table;
    private final int maxScroll;
    @Override
    public List<? extends GuiEventListener> contents() {
        return contents;
    }

    public EnumPage(int x, int y, int width, int height, FiguraListDocs.ListDoc doc) {
        super(x, y, width, height);
        int yOffset = 0;
        titleLabel = new Label(Component.literal(doc.name).withStyle(DocsScreen.ACCENT_COLOR.style),
                x+4,y+4, width-8-scrollBar.getWidth(), true);
        titleLabel.setScale(3);
        descriptionLabel = new Label(FiguraText.of("docs.enum."+doc.id),
                x+4,y+titleLabel.getHeight()+4, width-8-scrollBar.getWidth(), true);
        children.add(titleLabel);
        children.add(descriptionLabel);
        table = new LabelTable(
                x+4,y+titleLabel.getHeight()+descriptionLabel.getHeight()+8,width-12-scrollBar.getWidth());
        contents.add(table);
        int cY = 0;
        for (Object o :
                doc.get()) {
            if (o instanceof Map.Entry<?,?> e) {
                Label k = new Label(Component.literal(e.getKey().toString()).withStyle(DocsScreen.ACCENT_COLOR.style),
                        x,y+yOffset, TextUtils.Alignment.LEFT);
                table.setLabelAt(cY,0,k);
                if (e.getValue() instanceof Collection<?> coll) {
                    int cX = 1;
                    for (Object val :
                            coll) {
                        Label v = new Label(val.toString(), x,y+yOffset, TextUtils.Alignment.LEFT);
                        table.setLabelAt(cY,cX,v);
                        cX++;
                    }
                }
            }
            else {
                Label l = new Label(o.toString(), x,y+yOffset, TextUtils.Alignment.LEFT);
                table.setLabelAt(cY,0,l);
            }
            cY++;
        }
        scrollBar.visible = true;
        scrollBar.setY(y+titleLabel.getHeight()+descriptionLabel.getHeight()+8);
        scrollBar.setHeight(height-(titleLabel.getHeight()+descriptionLabel.getHeight()+12));
        table.update();
        maxScroll = Math.max(0, table.getHeight() - (height - 12 - titleLabel.getHeight() - descriptionLabel.getHeight()));
        updateScissors(4,8+titleLabel.getHeight()+descriptionLabel.getHeight(),-8,-(12+titleLabel.getHeight()+ descriptionLabel.getHeight()));
    }

    @Override
    public void render(PoseStack stack, int mouseX, int mouseY, float delta) {
        UIHelper.renderSliced(stack,x,y,width,height, UIHelper.OUTLINE_FILL);
        super.render(stack, mouseX, mouseY, delta);
        UIHelper.setupScissor(x+scissorsX,y+scissorsY,width+scissorsWidth, height+scissorsHeight);
        int scroll = (int)(scrollBar.getScrollProgress() * maxScroll);
        table.y = y+titleLabel.getHeight()+ descriptionLabel.getHeight()+8-scroll;
        table.render(stack, mouseX, mouseY, delta);
        UIHelper.disableScissor();
    }

    private static class LabelTable extends AbstractContainerElement {
        private static final int X_OFFSET = 4;
        private static final int Y_OFFSET = 4;
        private final List<List<Label>> labelTable = new ArrayList<>();
        private int rows, columns, height;
        private boolean shouldUpdate;
        public LabelTable(int x, int y, int width) {
            super(x, y, width,0);
        }

        public int getHeight() {
            return height;
        }

        public int getTableColumns() {
            int x = 0;
            for (List<Label> l :
                    labelTable) {
                x = Math.max(x, l.size());
            }
            return x;
        }
        public int getTableRows() {
            return labelTable.size();
        }
        public Label getLabelAt(int row, int column) {
            List<Label> l = labelTable.get(row);
            if (l == null || column >= l.size()) return null;
            shouldUpdate = true;
            return l.get(column);
        }
        public void setLabelAt(int row, int column, Label l) {
            expandToSize(row, column);
            labelTable.get(row).set(column, l);
            rows = getTableRows();
            columns = getTableColumns();
            shouldUpdate = true;
        }
        private void expandToSize(int row, int column) {
            for (int x = 0; x <= row; x++) {
                if (labelTable.size() <= x) labelTable.add(new ArrayList<>());
                List<Label> l = labelTable.get(x);
                for (int y = 0; y <= column; y++) {
                    if (l.size() <= y) l.add(null);
                }
            }
        }

        private void update() {
            int w = (int)(width / (float)(columns));
            height = Y_OFFSET;
            for (List<Label> labels :
                    labelTable) {
                int maxHeight = 0;
                for (Label l :
                        labels) {
                    if (l != null) {
                        l.maxWidth = w - (X_OFFSET*2);
                        l.wrap = true;
                        l.setScale(l.getScale());
                        maxHeight = Math.max(l.getHeight(), maxHeight);
                    }
                }
                height += maxHeight + Y_OFFSET;
            }
            shouldUpdate = false;
        }

        @Override
        public void render(PoseStack stack, int mouseX, int mouseY, float delta) {
            if (shouldUpdate) update();
            UIHelper.renderSliced(stack, x,y,width,height, UIHelper.OUTLINE_FILL);
            int columnWidth = (int)(width / (float)(columns));
            for (int cX = 1; cX < columns; cX++) {
                UIHelper.setupTexture(UIHelper.OUTLINE);
                blit(stack,x+cX*columnWidth,y+1,1,height-1,0,1,1,1,16,16);
            }
            int yOffset = Y_OFFSET;
            for (int lY = 0; lY < rows; lY++) {
                int cHeight = 0;
                for (int lX = 0; lX < columns; lX++) {
                    Label l = getLabelAt(lY, lX);
                    if (l != null) {
                        l.x = x+lX * columnWidth + X_OFFSET;
                        l.y = y + yOffset;
                        l.render(stack, mouseX, mouseY, delta);
                        cHeight = Math.max(l.getHeight(), cHeight);
                    }
                }
                yOffset += cHeight + Y_OFFSET;
                if (lY < rows-1) {
                    UIHelper.setupTexture(UIHelper.OUTLINE);
                    blit(stack,x+1,y+yOffset-3,width-1,1,0,1,1,1,16,16);
                }
            }
        }
    }
}
