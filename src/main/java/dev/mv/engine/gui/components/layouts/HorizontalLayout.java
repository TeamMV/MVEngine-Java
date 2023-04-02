package dev.mv.engine.gui.components.layouts;

import dev.mv.engine.gui.components.Element;
import dev.mv.engine.gui.components.extras.IgnoreDraw;
import dev.mv.engine.gui.utils.VariablePosition;
import dev.mv.engine.render.shared.DrawContext2D;
import dev.mv.engine.render.shared.Window;

public class HorizontalLayout extends FramedLayout {
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

    public HorizontalLayout(Window window, VariablePosition variablePosition, Element parent) {
        super(window, variablePosition, parent);
    }

    @Override
    public int getElementWidth() {
        if(initialState.width > 0) return initialState.width;
        int res = 0;
        for (Element e : elements) {
            if (e instanceof IgnoreDraw ignoreDraw) {
                for (Element element : ignoreDraw.toRender()) {
                    res += element.getWidth() + spacing;
                }
                continue;
            }
            res += e.getWidth() + spacing;
        }
        return res;
    }

    @Override
    public int getElementHeight() {
        return initialState.height <= 0 ? maxHeight : initialState.height;
    }

    public void alignContent(HorizontalLayout.Align align) {
        currentAlign = align;
    }

    @Override
    public void draw(DrawContext2D draw) {
        drawBackground(draw);
        drawFrame(draw);

        int xStart = getElementX();
        int yStart = getElementY();

        if (currentAlign == HorizontalLayout.Align.TOP) {
            for (Element e : elements) {
                maxHeight = Math.max(maxHeight, e.getHeight());
                if (e instanceof IgnoreDraw ignoreDraw) {
                    for (Element element : ignoreDraw.toRender()) {
                        element.setX(xStart);
                        element.setY(yStart + maxHeight - element.getHeight());
                        element.draw(draw);
                        xStart += element.getWidth();
                        xStart += spacing;
                    }
                    continue;
                }
                e.setX(xStart);
                e.setY(yStart + maxHeight - e.getHeight());
                e.draw(draw);
                xStart += e.getWidth();
                xStart += spacing;
            }
        } else if (currentAlign == HorizontalLayout.Align.CENTER) {
            for (Element e : elements) {
                if (e instanceof IgnoreDraw ignoreDraw) {
                    for (Element element : ignoreDraw.toRender()) {
                        element.setX(xStart);
                        element.setY(yStart + maxHeight / 2 - element.getHeight() / 2);
                        element.draw(draw);
                        xStart += element.getWidth();
                        xStart += spacing;
                    }
                    continue;
                }
                e.setX(xStart);
                e.setY(yStart + maxHeight / 2 - e.getHeight() / 2);
                e.draw(draw);
                xStart += e.getWidth();
                xStart += spacing;
            }
        } else if (currentAlign == HorizontalLayout.Align.BOTTOM) {
            for (Element e : elements) {
                if (e instanceof IgnoreDraw ignoreDraw) {
                    for (Element element : ignoreDraw.toRender()) {
                        element.setX(xStart);
                        element.setY(yStart);
                        element.draw(draw);
                        xStart += element.getWidth();
                        xStart += spacing;
                    }
                    continue;
                }
                e.setX(xStart);
                e.setY(yStart);
                e.draw(draw);
                xStart += e.getWidth();
                xStart += spacing;
            }
        }
    }

    public enum Align {
        TOP,
        CENTER,
        BOTTOM
    }
}
