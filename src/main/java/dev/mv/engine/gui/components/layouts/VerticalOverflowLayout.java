package dev.mv.engine.gui.components.layouts;

import dev.mv.engine.gui.components.Element;
import dev.mv.engine.gui.components.extras.IgnoreDraw;
import dev.mv.engine.gui.input.ScrollInput;
import dev.mv.engine.gui.input.Scrollable;
import dev.mv.engine.gui.utils.GuiUtils;
import dev.mv.engine.gui.utils.VariablePosition;
import dev.mv.engine.input.Input;
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
    public void draw(DrawContext2D draw) {
        if (parent != null) {
            draw.canvas(parent.getDrawAreaX(), parent.getDrawAreaY(), parent.getDrawAreaWidth(), parent.getDrawAreaHeight());
        }
        else {
            draw.canvas();
        }
        drawFrame(draw);
        draw.canvas(getDrawAreaX(), getDrawAreaY(), getDrawAreaWidth(), getDrawAreaHeight());

        int yStart = getElementY() + getHeight();
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
                        element.setX(xStart + ((maxWidth / 2) - (element.getWidth() / 2)));
                        element.setY(yStart - element.getHeight());
                        element.draw(draw);
                        yStart -= element.getHeight();
                        yStart -= spacing;
                    }
                    continue;
                }
                e.setX(xStart + ((maxWidth / 2) - (e.getWidth() / 2)));
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
                e.setX(xStart + (maxWidth - e.getWidth()));
                e.setY(yStart - e.getHeight());
                e.draw(draw);
                yStart -= e.getHeight();
                yStart -= spacing;
            }
        }
    }

    private boolean inside(Element e) {
        return e.getX() + e.getWidth() > getX() && e.getX() < getX() + getWidth() && e.getY() + e.getHeight() > getY() && e.getY() < getY() + getHeight();
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

    @Override
    public void click(int x, int y, int btn) {
        if (GuiUtils.mouseNotInside(getX() + getPaddingLeft(), getY() + getPaddingBottom(), getWidth() - getPaddingLeft() - getPaddingRight(), getHeight() - getPaddingBottom() - getPaddingTop())) return;
        unreleased[btn] = true;
        super.click(x, y, btn);
    }

    @Override
    public void clickRelease(int x, int y, int btn) {
        if (!unreleased[btn]) return;
        unreleased[btn] = false;
        super.clickRelease(x, y, btn);
    }

    @Override
    public void dragBegin(int x, int y, int btn) {
        if (GuiUtils.mouseNotInside(getX() + getPaddingLeft(), getY() + getPaddingBottom(), getWidth() - getPaddingLeft() - getPaddingRight(), getHeight() - getPaddingBottom() - getPaddingTop())) return;
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
        if(super.distributeScrollX(amount)) return true;
        if(scrollStyle.canScrollX()) {
            scrollX -= amount * 20;
            elements.forEach(e -> e.setX(e.getX() + scrollX));
        }
        return true;
    }

    @Override
    public boolean distributeScrollY(int amount) {
        if (super.distributeScrollY(amount)) return true;
        if(scrollStyle.canScrollY() && Input.mouse[Input.MOUSE_SCROLL_Y] != 0f) {
            scrollY -= Input.mouse[Input.MOUSE_SCROLL_Y] * 20;
            elements.forEach(e -> e.setY(e.getY() + scrollY));
        }
        return true;
    }

    @Override
    public void scrollX(int amount) {}

    @Override
    public void scrollY(int amount) {}

    @Override
    public int getDrawAreaX() {
        if (parent == null) {
            return getX() + getPaddingLeft();
        }
        return Math.max(parent.getDrawAreaX(), getX() + getPaddingLeft());
    }

    @Override
    public int getDrawAreaY() {
        if (parent == null) {
            return getY() + getPaddingBottom();
        }
        return Math.max(parent.getDrawAreaY(), getY() + getPaddingBottom());
    }

    @Override
    public int getDrawAreaWidth() {
        if (parent == null) {
            return getWidth() - getPaddingLeft() - getPaddingRight();
        }
        int parentX2 = parent.getDrawAreaWidth() + parent.getDrawAreaX();
        int x2 = getX() + getWidth() - getPaddingRight();
        return parentX2 < x2 ? parent.getDrawAreaWidth() : getWidth() - getPaddingRight() - getPaddingLeft();
        //return Math.min(parent.getDrawAreaWidth() + parent.getDrawAreaX(), getX() + getWidth() - getPaddingRight());
    }

    @Override
    public int getDrawAreaHeight() {
        if (parent == null) {
            return getHeight() - getPaddingBottom() - getPaddingTop();
        }
        return Math.min(parent.getDrawAreaHeight() + parent.getDrawAreaY(), getY() + getHeight() - getPaddingTop());
    }
}
