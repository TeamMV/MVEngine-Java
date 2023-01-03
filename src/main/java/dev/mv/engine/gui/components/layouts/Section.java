package dev.mv.engine.gui.components.layouts;

import dev.mv.engine.gui.components.Element;
import dev.mv.engine.render.shared.DrawContext2D;
import dev.mv.engine.render.shared.Window;

public class Section extends AbstractLayout{
    protected Section(Window window, Element parent) {
        super(window, parent);
    }

    protected Section(Window window, int width, int height, Element parent) {
        super(window, width, height, parent);
    }

    protected Section(Window window, int x, int y, int width, int height, Element parent) {
        super(window, x, y, width, height, parent);
    }

    @Override
    public void draw(DrawContext2D draw) {
        elements.forEach(e -> e.draw(draw));
    }
}
