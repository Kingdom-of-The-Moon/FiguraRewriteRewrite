package org.moon.figura.gui.widgets.docs;

import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.network.chat.Component;

public interface DocsPage extends GuiEventListener, NarratableEntry, Renderable {

    interface OnClassSwitch {
        void onSwitch(Class<?> clazz);
    }
}