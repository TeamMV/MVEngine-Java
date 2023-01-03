package dev.mv.engine.gui.components.layouts;

import dev.mv.engine.gui.components.Element;
import dev.mv.engine.render.shared.DrawContext2D;
import dev.mv.engine.render.shared.Window;

public class HorizontalLayout extends AbstractLayout{
    public enum Align{
        TOP,
        CENTER,
        BOTTOM
    }

    private HorizontalLayout.Align currentAlign = Align.TOP;

    public HorizontalLayout(Window window, Element parent) {
        super(window, parent);
    }

    public HorizontalLayout(Window window, int x, int y, Element parent) {
        super(window, x, y, -1, -1, parent);
    }

    public HorizontalLayout(Window window, int x, int y) {
        super(window, x, y, -1, -1, null);
    }

    public void alignContent(HorizontalLayout.Align align) {
        currentAlign = align;
    }

    @Override
    public void draw(DrawContext2D draw) {
        int yStart = getY();
        int xStart = getX();

        if (currentAlign == HorizontalLayout.Align.TOP) {
            for (Element e : elements) {
                e.setX(xStart);
                e.setY(yStart - e.getHeight());
                e.draw(draw);
                xStart += e.getWidth();
                xStart += spacing;
            }
        } else if (currentAlign == HorizontalLayout.Align.CENTER) {
            for (Element e : elements) {
                e.setX(xStart + ((maxWidth / 2) - (e.getWidth() / 2)));
                e.setY(yStart - e.getHeight());
                e.draw(draw);
                xStart -= e.getWidth();
                xStart -= spacing;
            }
        } else if (currentAlign == HorizontalLayout.Align.BOTTOM) {
            for (Element e : elements) {
                e.setX(xStart + (maxWidth - e.getWidth()));
                e.setY(yStart - e.getHeight());
                e.draw(draw);
                xStart -= e.getWidth();
                xStart -= spacing;
            }
        }
    }
}
