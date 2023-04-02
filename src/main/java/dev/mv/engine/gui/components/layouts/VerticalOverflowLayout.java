package dev.mv.engine.gui.components.layouts;

import dev.mv.engine.gui.components.Element;
import dev.mv.engine.gui.components.extras.IgnoreDraw;
import dev.mv.engine.gui.input.Scrollable;
import dev.mv.engine.gui.utils.GuiUtils;
import dev.mv.engine.gui.utils.VariablePosition;
import dev.mv.engine.render.shared.DrawContext2D;
import dev.mv.engine.render.shared.Window;
import dev.mv.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class VerticalOverflowLayout extends VerticalLayout implements Scrollable {
    private ScrollStyle scrollStyle = ScrollStyle.BOTH;
    private int scrollX, scrollY;
    private List<Element> rendered = new ArrayList<>();
    private boolean[] unreleased = new boolean[10];

    public VerticalOverflowLayout(Window window, Element parent) {
        super(window, parent);
    }

    public VerticalOverflowLayout(Window window, int x, int y, Element parent) {
        super(window, x, y, parent);
    }

    public VerticalOverflowLayout(Window window, int x, int y) {
        super(window, x, y);
    }

    public VerticalOverflowLayout(Window window, VariablePosition variablePosition, Element parent) {
        super(window, variablePosition, parent);
    }

    public ScrollStyle getScrollStyle() {
        return scrollStyle;
    }

    public void setScrollStyle(ScrollStyle scrollStyle) {
        this.scrollStyle = scrollStyle;
    }

    public int getScrollX() {
        return scrollX;
    }

    public void setScrollX(int scrollX) {
        this.scrollX = scrollX;
    }

    public int getScrollY() {
        return scrollY;
    }

    public void setScrollY(int scrollY) {
        this.scrollY = scrollY;
    }

    @Override
    public int getWidth() {
        return initialState.width;
    }

    @Override
    public int getHeight() {
        return initialState.height;
    }

    @Override
    public int getElementX() {
        return super.getElementX() + scrollX;
    }

    @Override
    public int getElementY() {
        return super.getElementY() + scrollY;
    }

    @Override
    public int getElementWidth() {
        return maxWidth;
    }

    @Override
    public int getElementHeight() {
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

    @Override
    public void draw(DrawContext2D draw) {
        resetCanvas(draw);
        drawBackground(draw);
        setupCanvas(draw);

        int yStart = getElementY() + getHeight() - getPaddingBottom() - getPaddingTop();
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
                        element.setX(xStart + (getWidth() - element.getWidth()));
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

        resetCanvas(draw);
        drawFrame(draw);
    }

    private void setupCanvas(DrawContext2D draw) {
        draw.canvas(getDrawAreaX1(), getDrawAreaY1(), getDrawAreaX2() - getDrawAreaX1(), getDrawAreaY2() - getDrawAreaY1(), theme.getEdgeRadius(), theme.getEdgeStyle());
    }

    @Override
    public void click(int x, int y, int btn) {
        if (GuiUtils.mouseNotInside(getX() + getPaddingLeft(), getY() + getPaddingBottom(), getWidth() - getPaddingLeft() - getPaddingRight(), getHeight() - getPaddingBottom() - getPaddingTop(), theme))
            return;
        super.click(x, y, btn);
    }

    @Override
    public void clickRelease(int x, int y, int btn) {
        if (GuiUtils.mouseNotInside(getX() + getPaddingLeft(), getY() + getPaddingBottom(), getWidth() - getPaddingLeft() - getPaddingRight(), getHeight() - getPaddingBottom() - getPaddingTop(), theme))
            return;
        super.clickRelease(x, y, btn);
    }

    @Override
    public void dragBegin(int x, int y, int btn) {
        if (GuiUtils.mouseNotInside(getX() + getPaddingLeft(), getY() + getPaddingBottom(), getWidth() - getPaddingLeft() - getPaddingRight(), getHeight() - getPaddingBottom() - getPaddingTop(), theme))
            return;
        super.dragBegin(x, y, btn);
    }

    @Override
    public void drag(int x, int y, int btn) {
        int newX = Utils.clamp(x, getX() + getPaddingLeft(), getX() + getWidth() - getPaddingRight());
        int newY = Utils.clamp(y, getY() + getPaddingBottom(), getY() + getHeight() - getPaddingTop());
        super.drag(newX, newY, btn);
    }

    @Override
    public void dragLeave(int x, int y, int btn) {
        int newX = Utils.clamp(x, getX() + getPaddingLeft(), getX() + getWidth() - getPaddingRight());
        int newY = Utils.clamp(y, getY() + getPaddingBottom(), getY() + getHeight() - getPaddingTop());
        super.dragLeave(newX, newY, btn);
    }

    @Override
    public boolean distributeScrollX(int amount) {
        if (super.distributeScrollX(amount)) return true;
        if (scrollStyle.canScrollX()) {
            scrollX -= amount * 40;
            elements.forEach(e -> e.setX(e.getX() + scrollX));
        }
        return true;
    }

    @Override
    public boolean distributeScrollY(int amount) {
        if (super.distributeScrollY(amount)) return true;
        if (scrollStyle.canScrollY()) {
            if (amount < 0 && !canScrollUp()) return true;
            if (amount > 0 && !canScrollDown()) return true;
            scrollY -= amount * 40;
            if (scrollY < 0) scrollY = 0;
            if (scrollY + (getHeight() - getPaddingTop() - getPaddingBottom()) > getElementHeight())
                scrollY = getElementHeight() - (getHeight() - getPaddingTop() - getPaddingBottom());
            elements.forEach(e -> e.setY(e.getY() + scrollY));
        }
        return true;
    }

    public boolean canScrollUp() {
        return getElementHeight() - (getHeight() - getPaddingTop() - getPaddingBottom()) - scrollY > 0;
    }

    public boolean canScrollDown() {
        return scrollY > 0;
    }

    public boolean canScrollLeft() {
        return getElementWidth() - (getWidth() - getPaddingLeft() - getPaddingRight()) - scrollX > 0;
    }

    public boolean canScrollRight() {
        return scrollX > 0;
    }

    @Override
    public void scrollX(int amount) {
    }

    @Override
    public void scrollY(int amount) {
    }

    @Override
    public int getDrawAreaX1() {
        if (parent == null) {
            System.out.println(getX());
            return getX() + getPaddingLeft();
        }
        return Math.max(parent.getDrawAreaX1(), getX() + getPaddingLeft());
    }

    @Override
    public int getDrawAreaY1() {
        if (parent == null) {
            return getY() + getPaddingBottom();
        }
        return Math.max(parent.getDrawAreaY1(), getY() + getPaddingBottom());
    }

    @Override
    public int getDrawAreaX2() {
        if (parent == null) {
            return getX() + getWidth() - getPaddingRight();
        }
        return Math.min(parent.getDrawAreaX2(), getX() + getWidth() - getPaddingRight());
    }

    @Override
    public int getDrawAreaY2() {
        if (parent == null) {
            return getY() + getHeight() - getPaddingTop();
        }
        return Math.min(parent.getDrawAreaY2(), getY() + getHeight() - getPaddingTop());
    }

    public enum ScrollStyle {
        X,
        Y,
        BOTH,
        NONE;

        public boolean canScrollX() {
            return this == X || this == BOTH;
        }

        public boolean canScrollY() {
            return this == Y || this == BOTH;
        }
    }
}
