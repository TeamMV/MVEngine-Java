package dev.mv.engine.gui.components.layouts;

import dev.mv.engine.gui.components.Element;
import dev.mv.engine.gui.theme.Theme;
import dev.mv.engine.gui.utils.VariablePosition;
import dev.mv.engine.render.shared.DrawContext2D;
import dev.mv.engine.render.shared.Window;

public abstract class FramedLayout extends AbstractLayout {
    protected boolean showFrame = false;
    protected int paddingLeft = 0, paddingRight = 0, paddingTop = 0, paddingBottom = 0;

    protected FramedLayout(Window window, Element parent) {
        super(window, parent);
    }

    protected FramedLayout(Window window, int width, int height, Element parent) {
        super(window, width, height, parent);
    }

    protected FramedLayout(Window window, int x, int y, int width, int height, Element parent) {
        super(window, x, y, width, height, parent);
    }

    protected FramedLayout(Window window, VariablePosition position, Element parent) {
        super(window, position, parent);
    }

    public abstract int getElementWidth();

    public abstract int getElementHeight();

    public void setPadding(int left, int right, int top, int bottom) {
        paddingLeft = left;
        paddingRight = right;
        paddingTop = top;
        paddingBottom = bottom;
    }

    public int getPaddingLeft() {
        return paddingLeft;
    }

    public void setPaddingLeft(int paddingLeft) {
        this.paddingLeft = paddingLeft;
    }

    public int getPaddingRight() {
        return paddingRight;
    }

    public void setPaddingRight(int paddingRight) {
        this.paddingRight = paddingRight;
    }

    public int getPaddingTop() {
        return paddingTop;
    }

    public void setPaddingTop(int paddingTop) {
        this.paddingTop = paddingTop;
    }

    public int getPaddingBottom() {
        return paddingBottom;
    }

    public void setPaddingBottom(int paddingBottom) {
        this.paddingBottom = paddingBottom;
    }

    public int getElementX() {
        return getX() + paddingLeft;
    }

    public int getElementY() {
        return getY() + paddingBottom;
    }

    @Override
    public int getWidth() {
        return getElementWidth() + paddingLeft + paddingRight;
    }

    @Override
    public int getHeight() {
        return getElementHeight() + paddingTop + paddingBottom;
    }

    public void showFrame() {
        showFrame = true;
    }

    public void hideFrame() {
        showFrame = false;
    }

    protected void drawFrame(DrawContext2D draw) {
        if (!showFrame) return;
        if (theme.getEdgeStyle() == Theme.EdgeStyle.ROUND) {
            if (theme.hasOutline()) {
                int thickness = theme.getOutlineThickness();
                draw.color(getOutlineColor());
                draw.voidRoundedRectangle(getX(), getY(), getWidth(), getHeight(), thickness, theme.getEdgeRadius(), theme.getEdgeRadius());
                draw.color(getBaseColor());
                draw.roundedRectangle(getX() + thickness, getY() + thickness, getWidth() - thickness * 2, getHeight() - thickness * 2, theme.getEdgeRadius(), theme.getEdgeRadius());
            } else {
                draw.color(getBaseColor());
                draw.roundedRectangle(getX(), getY(), getWidth(), getHeight(), theme.getEdgeRadius(), theme.getEdgeRadius());
            }
        } else if (theme.getEdgeStyle() == Theme.EdgeStyle.TRIANGLE) {
            if (theme.hasOutline()) {
                int thickness = theme.getOutlineThickness();
                draw.color(getOutlineColor());
                draw.voidTriangularRectangle(getX(), getY(), getWidth(), getHeight(), thickness, theme.getEdgeRadius());
                draw.color(getBaseColor());
                draw.triangularRectangle(getX() + thickness, getY() + thickness, getWidth() - thickness * 2, getHeight() - thickness * 2, theme.getEdgeRadius());
            } else {
                draw.color(getBaseColor());
                draw.triangularRectangle(getX(), getY(), getWidth(), getHeight(), theme.getEdgeRadius());
            }
        } else if (theme.getEdgeStyle() == Theme.EdgeStyle.SQUARE) {
            if (theme.hasOutline()) {
                int thickness = theme.getOutlineThickness();
                draw.color(getOutlineColor());
                draw.voidRectangle(getX(), getY(), getWidth(), getHeight(), thickness);
                draw.color(getBaseColor());
                draw.rectangle(getX() + thickness, getY() + thickness, getWidth() - thickness * 2, getHeight() - thickness * 2);
            } else {
                draw.color(getBaseColor());
                draw.rectangle(getX(), getY(), getWidth(), getHeight());
            }
        }
    }
}
