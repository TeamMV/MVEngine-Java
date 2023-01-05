package dev.mv.engine.gui.components;

import dev.mv.engine.gui.event.EventListener;
import dev.mv.engine.render.shared.DrawContext2D;
import dev.mv.engine.render.shared.Window;

public class Space extends Element{
    public Space(Window window, int x, int y, int width, int height, Element parent) {
        super(window, x, y, width, height, parent);
    }

    public Space(Window window, int width, int height, Element parent) {
        super(window, -1, -1, width, height, parent);
    }

    public Space(Window window, int x, int y, int width, int height) {
        super(window, x, y, width, height, null);
    }

    @Override
    public void draw(DrawContext2D draw) {

    }

    @Override
    public void attachListener(EventListener listener) {

    }
}
