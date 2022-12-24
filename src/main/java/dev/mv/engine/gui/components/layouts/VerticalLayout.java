package dev.mv.engine.gui.components.layouts;

import dev.mv.engine.gui.components.Element;
import dev.mv.engine.render.shared.DrawContext2D;
import dev.mv.engine.render.shared.Window;

public class VerticalLayout extends AbstractLayout{
    public enum Align{
        LEFT,
        CENTER,
        RIGHT
    }

    private Align currentAlign = Align.LEFT;

    public VerticalLayout(Window window, Element parent) {
        super(window, parent);
    }

    public VerticalLayout(Window window, int x, int y, Element parent) {
        super(window, x, y, -1, -1, parent);
    }

    public VerticalLayout(Window window, int x, int y) {
        super(window, x, y, -1, -1, null);
    }

    public void alignContent(Align align) {
        currentAlign = align;
    }

    @Override
    public void draw(DrawContext2D draw) {
        int yStart = getY();
        int xStart = getX();

        if (currentAlign == Align.LEFT) {
            for (Element e : elements) {
                e.setX(xStart);
                e.setY(yStart - e.getHeight());
                e.draw(draw);
                yStart -= e.getHeight();
                yStart -= spacing;
            }
        } else if (currentAlign == Align.CENTER) {
            for (Element e : elements) {
                e.setX(xStart + ((maxWidth / 2) - (e.getWidth() / 2)));
                e.setY(yStart - e.getHeight());
                e.draw(draw);
                yStart -= e.getHeight();
                yStart -= spacing;
            }
        } else if (currentAlign == Align.RIGHT) {
            for (Element e : elements) {
                e.setX(xStart + (maxWidth - e.getWidth()));
                e.setY(yStart - e.getHeight());
                e.draw(draw);
                yStart -= e.getHeight();
                yStart -= spacing;
            }
        }
    }
}
