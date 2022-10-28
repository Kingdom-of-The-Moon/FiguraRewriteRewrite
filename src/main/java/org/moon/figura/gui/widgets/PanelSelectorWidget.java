package org.moon.figura.gui.widgets;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.moon.figura.FiguraMod;
import org.moon.figura.gui.screens.*;

import java.util.ArrayList;
import java.util.function.Supplier;

public class PanelSelectorWidget extends AbstractContainerElement {

    private final ArrayList<SwitchButton> buttons = new ArrayList<>();

    public PanelSelectorWidget(Screen parentScreen, int x, int y, int width, int selected) {
        super(x, y, width, 28);

        //buttons
        createPanelButton(() -> new ProfileScreen(parentScreen), ProfileScreen.TITLE);
        createPanelButton(() -> new BrowserScreen(parentScreen), BrowserScreen.TITLE);
        createPanelButton(() -> new WardrobeScreen(parentScreen), WardrobeScreen.TITLE);
        createPanelButton(() -> new TrustScreen(parentScreen), TrustScreen.TITLE);
        createPanelButton(() -> new ConfigScreen(parentScreen), ConfigScreen.TITLE);

        //selected button
        buttons.get(selected).setToggled(true);

        //TODO - remove this when we actually implement those panels {
        if (FiguraMod.DEBUG_MODE)
            return;

        for (int i = 0; i < 2; i++) {
            SwitchButton button = buttons.get(i);
            button.setTooltip(Component.literal("Not yet ❤"));
            button.active = false;
        }
        //TODO
    }

    private void createPanelButton(Supplier<AbstractPanelScreen> screenSupplier, Component title) {
        //create button
        SwitchButton button = new SwitchButton(width / 2 - 176 + 72 * buttons.size() + 2, y + 4, 60, 20, title, null, bx -> Minecraft.getInstance().setScreen(screenSupplier.get()));
        button.shouldHaveBackground(false);

        //add button
        buttons.add(button);
        children.add(button);
    }
}
