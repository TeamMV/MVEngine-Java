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
        int xStart = getX();
        int yStart = getY();

        if (currentAlign == HorizontalLayout.Align.TOP) {
            for (Element e : elements) {
                e.setX(xStart);
                e.setY(yStart + maxHeight - e.getHeight());
                e.draw(draw);
                xStart += e.getWidth();
                xStart += spacing;
            }
        } else if (currentAlign == HorizontalLayout.Align.CENTER) {
            for (Element e : elements) {
                e.setX(xStart);
                e.setY(yStart + maxHeight / 2 - e.getHeight() / 2);
                e.draw(draw);
                xStart += e.getWidth();
                xStart += spacing;
            }
        } else if (currentAlign == HorizontalLayout.Align.BOTTOM) {
            for (Element e : elements) {
                e.setX(xStart);
                e.setY(yStart);
                e.draw(draw);
                xStart += e.getWidth();
                xStart += spacing;
            }
        }
    }

    @Override
    public int getWidth() {
        int res = 0;
        for(Element e : elements) {
            res += e.getWidth() + spacing;
        } return res;
    }

    @Override
    public int getHeight() {
        return maxHeight;
    }
}
