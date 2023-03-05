package dev.mv.engine.gui.components;

import dev.mv.engine.gui.components.layouts.AbstractLayout;
import dev.mv.engine.gui.event.EventListener;
import dev.mv.engine.gui.utils.VariablePosition;
import dev.mv.engine.render.shared.DrawContext2D;
import dev.mv.engine.render.shared.Window;

public class Aligner extends AbstractLayout {
    private AlignX alignX = AlignX.CENTER;
    private AlignY alignY = AlignY.CENTER;

    public Aligner(Window window, int x, int y, int width, int height, Element parent) {
        super(window, x, y, width, height, parent);
    }

    public Aligner(Window window, VariablePosition position, Element parent) {
        super(window, position, parent);
    }

    @Override
    public void draw(DrawContext2D draw) {
        if(alignX == AlignX.LEFT) {
            elements.get(0).setX(getX());
        }

        if(alignX == AlignX.CENTER) {
            elements.get(0).setX(getX() + getWidth() / 2 - elements.get(0).getWidth() / 2);
        }

        if(alignX == AlignX.RIGHT) {
            elements.get(0).setX(getX() + getWidth() - elements.get(0).getWidth());
        }

        if(alignY == AlignY.TOP) {
            elements.get(0).setY(getY() + getHeight() - elements.get(0).getHeight());
        }

        if(alignY == AlignY.CENTER) {
            elements.get(0).setY(getY() + getHeight() / 2 - elements.get(0).getHeight() / 2);
        }

        if(alignY == AlignY.BOTTOM) {
            elements.get(0).setY(getY());
        }

        elements.get(0).draw(draw);
    }

    @Override
    public void attachListener(EventListener listener) {

    }

    public AlignX getAlignX() {
        return alignX;
    }

    public void setAlignX(AlignX alignX) {
        this.alignX = alignX;
    }

    public AlignY getAlignY() {
        return alignY;
    }

    public void setAlignY(AlignY alignY) {
        this.alignY = alignY;
    }

    public enum AlignX {
        LEFT("LEFT"),
        CENTER("CENTER"),
        RIGHT("RIGHT");

        AlignX(String align) {
        }
    }

    public enum AlignY {
        TOP("TOP"),
        CENTER("CENTER"),
        BOTTOM("BOTTOM");

        AlignY(String align) {
        }
    }
}
