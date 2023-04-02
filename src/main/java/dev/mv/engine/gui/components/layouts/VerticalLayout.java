package dev.mv.engine.gui.components.layouts;

import dev.mv.engine.gui.components.Element;
import dev.mv.engine.gui.components.extras.IgnoreDraw;
import dev.mv.engine.gui.utils.VariablePosition;
import dev.mv.engine.render.shared.DrawContext2D;
import dev.mv.engine.render.shared.Window;

public class VerticalLayout extends FramedLayout {
    protected Align currentAlign = Align.LEFT;

    public VerticalLayout(Window window, Element parent) {
        super(window, parent);
    }

    public VerticalLayout(Window window, int x, int y, Element parent) {
        super(window, x, y, -1, -1, parent);
    }

    public VerticalLayout(Window window, int x, int y) {
        super(window, x, y, -1, -1, null);
    }

    public VerticalLayout(Window window, VariablePosition variablePosition, Element parent) {
        super(window, variablePosition, parent);
    }

    @Override
    public int getElementWidth() {
        return initialState.width <= 0 ? maxWidth : initialState.width;
    }

    @Override
    public int getElementHeight() {
        if (initialState.height > 0) return initialState.height;
        int res = 0;
        for (Element e : elements) {
            if (e instanceof IgnoreDraw ignoreDraw) {
                for (Element element : ignoreDraw.toRender()) {
                    res += element.getHeight() + spacing;
                }
                continue;
            }
            res += e.getHeight() + spacing;
        }
        return res;
    }

    public void alignContent(Align align) {
        currentAlign = align;
    }

    @Override
    public void draw(DrawContext2D draw) {
        drawBackground(draw);
        drawFrame(draw);

        int yStart = getElementY() + getElementHeight();
        int xStart = getElementX();

        if (currentAlign == Align.LEFT) {
            for (Element e : elements) {
                maxWidth = Math.max(maxWidth, e.getWidth());
                if (e instanceof IgnoreDraw ignoreDraw) {
                    for (Element element : ignoreDraw.toRender()) {
                        element.setX(xStart);
                        element.setY(yStart - element.getHeight());
                        element.draw(draw);
                        yStart -= element.getHeight();
                        yStart -= spacing;
                    }
                    continue;
                }
                e.setX(xStart);
                e.setY(yStart - e.getHeight());
                e.draw(draw);
                yStart -= e.getHeight();
                yStart -= spacing;
            }
        } else if (currentAlign == Align.CENTER) {
            for (Element e : elements) {
                if (e instanceof IgnoreDraw ignoreDraw) {
                    for (Element element : ignoreDraw.toRender()) {
                        element.setX(xStart + ((getWidth() / 2) - (element.getWidth() / 2)));
                        element.setY(yStart - element.getHeight());
                        element.draw(draw);
                        yStart -= element.getHeight();
                        yStart -= spacing;
                    }
                    continue;
                }
                e.setX(xStart + ((getWidth() / 2) - (e.getWidth() / 2)));
                e.setY(yStart - e.getHeight());
                e.draw(draw);
                yStart -= e.getHeight();
                yStart -= spacing;
            }
        } else if (currentAlign == Align.RIGHT) {
            for (Element e : elements) {
                if (e instanceof IgnoreDraw ignoreDraw) {
                    for (Element element : ignoreDraw.toRender()) {
                        element.setX(xStart + (maxWidth - element.getWidth()));
                        element.setY(yStart - element.getHeight());
                        element.draw(draw);
                        yStart -= element.getHeight();
                        yStart -= spacing;
                    }
                    continue;
                }
                e.setX(xStart + (getWidth() - e.getWidth()));
                e.setY(yStart - e.getHeight());
                e.draw(draw);
                yStart -= e.getHeight();
                yStart -= spacing;
            }
        }
    }

    public enum Align {
        LEFT,
        CENTER,
        RIGHT
    }
}
