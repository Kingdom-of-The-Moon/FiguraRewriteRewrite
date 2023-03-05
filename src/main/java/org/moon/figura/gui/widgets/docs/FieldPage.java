package org.moon.figura.gui.widgets.docs;

import org.moon.figura.gui.widgets.AbstractContainerElement;
import org.moon.figura.lua.docs.FiguraDoc;

public class FieldPage extends AbstractContainerElement implements DocsPage {
    public FieldPage(int x, int y, int width, int height, FiguraDoc.FieldDoc doc) {
        super(x, y, width, height);
    }

    @Override
    public void goTo(String destination) {

    }
}
