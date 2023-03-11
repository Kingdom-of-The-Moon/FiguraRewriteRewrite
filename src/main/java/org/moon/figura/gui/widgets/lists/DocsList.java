package org.moon.figura.gui.widgets.lists;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import org.moon.figura.gui.screens.DocsScreen;
import org.moon.figura.gui.widgets.AbstractTreeElement;
import org.moon.figura.gui.widgets.ScrollBarWidget;
import org.moon.figura.gui.widgets.TextField;
import org.moon.figura.gui.widgets.TexturedButton;
import org.moon.figura.gui.widgets.docs.EnumPage;
import org.moon.figura.lua.docs.FiguraDoc;
import org.moon.figura.lua.docs.FiguraDocsManager;
import org.moon.figura.lua.docs.FiguraListDocs;
import org.moon.figura.utils.ColorUtils;
import org.moon.figura.utils.FiguraIdentifier;
import org.moon.figura.utils.FiguraText;
import org.moon.figura.utils.ui.UIHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class DocsList extends AbstractList{

    private static final ResourceLocation SELECTED_TEXTURE = new FiguraIdentifier("textures/gui/button.png");
    private int currentScroll = 0;
    private int maxScroll = 0;
    private int prevMaxScroll = 0;
    private int prevWidth, prevHeight;
    private final ScrollBarWidget.OnPress onScrollAction;
    private final List<DocsTreeElement> contents = new ArrayList<>();
    private final TextField searchBar;
    private static DocsTreeElement selectedElement;
    public DocsList(int x, int y, int width, int height) {
        super(x, y, width, height);
        searchBar = new TextField(x + 4, y + 4, width - 8, 20, TextField.HintType.SEARCH, (s) -> onSearchTextChanged());
        children.add(searchBar);
        scrollBar.setY(y+26);
        scrollBar.setHeight(height-28);
        onScrollAction = (s) -> currentScroll = (int)(maxScroll * s.getScrollProgress());
        scrollBar.setAction(onScrollAction);
        scrollBar.visible = true;
        updateScissors(4,26,-4,-30);

        int w = width-12-scrollBar.getWidth();
        DocsTreeElement globalsTreeElement = new DocsTreeElement(0,0,w);
        globalsTreeElement.setMessage(FiguraText.of("gui.docs.globals"));
        for (String global: FiguraDocsManager.getGlobalNames()) {
            DocsTreeElement globalSubElement = new DocsTreeElement(0,0,w);
            globalSubElement.setMessage(Component.literal(global));
            for (FiguraDoc doc :
                    FiguraDocsManager.getGlobalDocs(global)) {
                DocsTreeElement docElement = new DocsTreeElement(0,0,w,(b) -> DocsScreen.onSelect(doc));
                docElement.setCanBeSelected(true);
                docElement.setMessage(Component.literal(doc.name));
                docElement.setParentDoc(doc);
                globalSubElement.getChildren().add(docElement);
            }
            globalsTreeElement.getChildren().add(globalSubElement);
        }
        contents.add(globalsTreeElement);
        children.add(globalsTreeElement);
        DocsTreeElement enumsTreeElement = new DocsTreeElement(0,0,w);
        enumsTreeElement.setMessage(FiguraText.of("gui.docs.enums"));
        for (FiguraListDocs.ListDoc entry :
                FiguraListDocs.getEnumValues()) {
            DocsTreeElement enumElement = new DocsTreeElement(0,0,w, (b) ->
                    DocsScreen.setCurrentPage((x1, y1, width1, height1) -> new EnumPage(x1,y1,width1,height1, entry.name(), entry.get()))
                    );
            enumElement.setCanBeSelected(true);
            enumElement.setMessage(Component.literal(entry.name()));
            enumsTreeElement.getChildren().add(enumElement);
        }
        contents.add(enumsTreeElement);
        children.add(enumsTreeElement);
        prevWidth = width;
        prevHeight = height;
    }
    @Override
    public List<? extends GuiEventListener> contents() {
        return contents;
    }
    private int getMaxScroll() {
        int h = 0;
        for (var e : contents){
            h += e.getHeight() + e.getElementYOffset();
        }
        return Math.max(0, h-(height - 28));
    }
    @Override
    public void render(PoseStack stack, int mouseX, int mouseY, float delta) {
        UIHelper.renderSliced(stack, x, y, width, height, UIHelper.OUTLINE_FILL);
        if (prevWidth != width || prevHeight != height) {
            scrollBar.setX(x + width - 14);
            scrollBar.setHeight(height-28);
            searchBar.width = width - 8;
            prevWidth = width;
            prevHeight = height;
        }
        super.render(stack, mouseX, mouseY, delta);
        UIHelper.setupScissor(x+scissorsX,y+scissorsY,width+scissorsWidth,height+scissorsHeight);
        maxScroll = getMaxScroll();
        if (maxScroll != prevMaxScroll) {
            scrollBar.setScrollProgress(maxScroll > 0 ? currentScroll / (float)(maxScroll) : 0);
            prevMaxScroll = maxScroll;
        }
        currentScroll = Math.min(currentScroll, maxScroll);
        int yOffset = y+26-currentScroll;
        int xOffset = x+4;
        for (var e :
                contents) {
            e.setPosition(xOffset,yOffset);
            e.render(stack, mouseX, mouseY, delta);
            yOffset += e.getHeight() + e.getElementYOffset();
        }
        UIHelper.disableScissor();
    }
    private boolean filterFunc(TexturedButton e) {
        String searchString = searchBar.getField().getValue().toLowerCase();
        if (e instanceof DocsTreeElement ) {
            return ((DocsTreeElement) e).filter(this::filterFunc);
        }
        return e.getMessage().getString().toLowerCase().contains(searchString);
    }
    private void onSearchTextChanged() {
        for (DocsTreeElement treeElement :
                contents) {
            treeElement.filter(this::filterFunc);
        }
    }
    private static Font getFont() {
        return Minecraft.getInstance().font;
    }
    private static class DocsTreeElement extends AbstractTreeElement {
        private Component title = Component.empty();
        private FiguraDoc parentDoc;
        private static final int HEIGHT = 17;
        private boolean canBeSelected = false;
        public DocsTreeElement(int x, int y, int width, Button.OnPress callback) {
            super(x, y, width, HEIGHT, callback);
        }
        public DocsTreeElement(int x, int y, int width) {
            super(x, y, width, HEIGHT);
        }
        @Override
        public void setMessage(Component message) {
            this.title = message;
        }
        @Override
        public Component getMessage() {
            return title;
        }

        public FiguraDoc getParentDoc() {
            return parentDoc;
        }

        public void setParentDoc(FiguraDoc parentDoc) {
            this.parentDoc = parentDoc;
        }
        private boolean canBeSelected() {
            return canBeSelected;
        }

        public void setCanBeSelected(boolean canBeSelected) {
            this.canBeSelected = canBeSelected;
        }

        private boolean isSelected() {
            return this == selectedElement;
        }
        @Override
        public void renderButton(PoseStack matrices, int mouseX, int mouseY, float delta) {
            int x = getX();
            int y = getY();
            int width = getWidth();
            int height = getElementHeight();
            boolean canBeExpanded = canBeExpanded();
            int textX = x + (canBeExpanded ? 12 : 4);
            int textWidth = width - (4 + (canBeExpanded ? 12 : 4));

            if (isSelected()) {
                UIHelper.renderSliced(matrices,x,y,width,height,32,0,16,16,48,32,SELECTED_TEXTURE);
            }
            else UIHelper.renderSliced(matrices, x, y, width, height, UIHelper.OUTLINE_FILL);
            int textColor = isMouseOverElement(mouseX,mouseY) ? DocsScreen.ACCENT_COLOR.hex : 0xFFFFFF;
            if (title != null)
                UIHelper.renderScrollingText(matrices, title, textX,y+4, textWidth, height-8, textColor);
            if (canBeExpanded()) {
                Component arrow = isExpanded() ? UIHelper.DOWN_ARROW : UIHelper.UP_ARROW;
                int arrowColor = isMouseInExpander(mouseX,mouseY) ? ColorUtils.Colors.FRAN_PINK.hex : 0x404040;
                getFont().draw(matrices, arrow, x + 4, y+(height / 2f)-4f, arrowColor);
            }
        }
        private boolean isMouseInExpander(double mouseX, double mouseY) {
            int x = getX();
            int y = getY();
            int height = getElementHeight();
            return mouseX >= x && mouseX < x + 16 &&
                    mouseY >= y && mouseY < y + height;
        }
        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            if (isMouseOverElement(mouseX, mouseY)) {
                if ((!canBeSelected() || isMouseInExpander(mouseX, mouseY)) && canBeExpanded()) {
                    setExpanded(!isExpanded());
                }
                if (canBeSelected()) {
                    onPress.onPress(this);
                    selectedElement = this;
                }
                playClickSound();
                return true;
            }
            return super.mouseClicked(mouseX, mouseY, button);
        }
        public boolean filter(Function<TexturedButton, Boolean> predicate) {
            boolean match = false;
            for (TexturedButton e :
                    getChildren()) {
                match = match | predicate.apply(e);
            }
            match = match || predicate.apply(this);
            setVisible(match);
            return match;
        }

        private void playClickSound() {
            Minecraft client = Minecraft.getInstance();
            SoundManager soundManager = client.getSoundManager();
            soundManager.play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
        }
    }

}
