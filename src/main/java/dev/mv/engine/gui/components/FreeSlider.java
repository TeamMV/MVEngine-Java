package dev.mv.engine.gui.components;

import dev.mv.engine.gui.components.extras.ValueChange;
import dev.mv.engine.gui.event.EventListener;
import dev.mv.engine.gui.event.ProgressListener;
import dev.mv.engine.gui.input.Draggable;
import dev.mv.engine.gui.theme.Theme;
import dev.mv.engine.gui.utils.GuiUtils;
import dev.mv.engine.gui.utils.VariablePosition;
import dev.mv.engine.input.Input;
import dev.mv.engine.render.shared.DrawContext2D;
import dev.mv.engine.render.shared.Window;
import dev.mv.utils.Utils;

public class FreeSlider extends Element implements ValueChange, Draggable {
    private boolean isDrag = false;
    private float start, end, value;

    public FreeSlider(Window window, int x, int y, int width, int height, Element parent) {
        super(window, x, y, width, height, parent);
    }

    public FreeSlider(Window window, VariablePosition position, Element parent) {
        super(window, position, parent);
    }

    @Override
    public void draw(DrawContext2D draw) {
        checkAnimations();

        if(theme.getEdgeStyle() == Theme.EdgeStyle.ROUND) {
            int thickness = theme.getOutlineThickness();
            draw.color(animationState.outlineColor);
            if(theme.hasOutline()) {
                draw.voidRoundedRectangle(animationState.posX, animationState.posY, animationState.width, animationState.height, thickness, (int) (animationState.height * (1/4f)), animationState.height * (1/4f), animationState.rotation, animationState.originX, animationState.originY);
            }
            draw.color(animationState.baseColor);
            draw.roundedRectangle(animationState.posX + thickness, animationState.posY + thickness, animationState.width - thickness * 2, animationState.height - thickness * 2, (int) (animationState.height * (1/4f)), animationState.height * (1/4f), animationState.rotation, animationState.originX, animationState.originY);


            draw.color(animationState.outlineColor);
            if(theme.hasOutline()) {
                draw.voidCircle(animationState.posX + Utils.getValue(Utils.getPercent(value - start, (int) (end - start)), animationState.width - (animationState.height + 6)), animationState.posY + animationState.height / 2 - 3, animationState.height + 6, thickness, animationState.height, animationState.rotation, animationState.originX, animationState.originY);
            }
            draw.color(animationState.baseColor);
            draw.circle(animationState.posX + Utils.getValue(Utils.getPercent(value - start, (int) (end - start)), animationState.width - (animationState.height + 6)), animationState.posY + animationState.height / 2 - 3, animationState.height + 6 - thickness, animationState.height, animationState.rotation, animationState.originX, animationState.originY);
        }
        if(theme.getEdgeStyle() == Theme.EdgeStyle.TRIANGLE) {
            int thickness = theme.getOutlineThickness();
            draw.color(animationState.outlineColor);
            if(theme.hasOutline()) {
                draw.voidTriangularRectangle(animationState.posX, animationState.posY, animationState.width, animationState.height, thickness, (int) (animationState.height * (1/4f)), animationState.rotation, animationState.originX, animationState.originY);
            }
            draw.color(animationState.baseColor);
            draw.triangularRectangle(animationState.posX + thickness, animationState.posY + thickness, animationState.width - thickness * 2, animationState.height - thickness * 2, (int) (animationState.height * (1/4f)), animationState.rotation, animationState.originX, animationState.originY);


            draw.color(animationState.outlineColor);
            if(theme.hasOutline()) {
                draw.voidRectangle(animationState.posX + Utils.getValue(Utils.getPercent(value - start, (int) (end - start)), animationState.width - (animationState.height + 6)), animationState.posY - 3, animationState.height + 6, animationState.height + 6, thickness, animationState.rotation, animationState.originX, animationState.originY);
            }
            draw.color(animationState.baseColor);
            draw.rectangle(animationState.posX + Utils.getValue(Utils.getPercent(value - start, (int) (end - start)) + thickness, animationState.width - (animationState.height + 6)), animationState.posY - 3 + thickness, animationState.height + 6 - thickness * 2, animationState.height + 6 - thickness * 2, animationState.rotation, animationState.originX, animationState.originY);
        }
        if(theme.getEdgeStyle() == Theme.EdgeStyle.SQUARE) {
            int thickness = theme.getOutlineThickness();
            draw.color(animationState.outlineColor);
            if(theme.hasOutline()) {
                draw.voidRectangle(animationState.posX, animationState.posY, animationState.width, animationState.height, thickness, animationState.rotation, animationState.originX, animationState.originY);
            }
            draw.color(animationState.baseColor);
            draw.rectangle(animationState.posX + thickness, animationState.posY + thickness, animationState.width - thickness * 2, animationState.height - thickness * 2, animationState.rotation, animationState.originX, animationState.originY);


            draw.color(animationState.outlineColor);
            if(theme.hasOutline()) {
                draw.voidRectangle(animationState.posX + Utils.getValue(Utils.getPercent(value - start, (int) (end - start)), animationState.width - (animationState.height + 6)), animationState.posY - 3, animationState.height + 6, animationState.height + 6, thickness, animationState.rotation, animationState.originX, animationState.originY);
            }
            draw.color(animationState.baseColor);
            draw.rectangle(animationState.posX + Utils.getValue(Utils.getPercent(value - start, (int) (end - start)), animationState.width - (animationState.height + 6)) + thickness, animationState.posY - 3 + thickness, animationState.height + 6 - thickness * 2, animationState.height + 6 - thickness * 2, animationState.rotation, animationState.originX, animationState.originY);
        }
    }

    @Override
    public void attachListener(EventListener listener) {
        if(listener instanceof ProgressListener progressListener) {
            progressListeners.add(progressListener);
        }
    }

    @Override
    public void increment(float amount) {
        value = Utils.clamp(value + amount, start, end);
        progressListeners.forEach(l -> {
            l.onIncrement(this, (int) value, (int) end, (int) Utils.getPercent(value - start, (int) (end - start)));
        });
    }

    @Override
    public void decrement(float amount) {
        value = Utils.clamp(value - amount, start, end);
        progressListeners.forEach(l -> {
            l.onDecrement(this, (int) value, (int) end, (int) Utils.getPercent(value - start, (int) (end - start)));
        });
    }

    @Override
    public void incrementByPercentage(int amount) {
        increment(Utils.getPercent((float) amount, (int) (end - start)));
    }

    @Override
    public void decrementByPercentage(int amount) {
        decrement(Utils.getPercent((float) amount, (int) (end - start)));
    }

    public float getValue() {
        return value;
    }

    public float getStart() {
        return start;
    }

    public void setStart(float start) {
        this.start = start;
        value = Utils.clamp(value, start, end);
    }

    public float getEnd() {
        return end;
    }

    public void setEnd(float end) {
        this.end = end;
        value = Utils.clamp(value, start, end);
    }

    public void setValue(float value) {
        if(this.value < value) increment(value - this.value);
        if(this.value > value) decrement(this.value - value);
        //Do it like this for the event listeners...
    }

    @Override
    public void dragBegin(int x, int y, int btn) {
        if(btn == Input.BUTTON_LEFT) {
            if(GuiUtils.mouseInside(getX(), getY() - 3, getWidth(), getHeight() + 6)) {
                isDrag = true;
            }
        }
    }

    @Override
    public void drag(int x, int y, int btn) {
        if(btn == Input.BUTTON_LEFT) {
            if (isDrag) {
                setValue(Utils.getValue(Utils.getPercent(x - (getX() + (getHeight() + 6) / 2f) + (getWidth() / (end - start)) / 2f, getWidth() - (getHeight() + 6)), (int) (end - start)) + start);
            }
        }
    }

    @Override
    public void dragLeave(int x, int y, int btn) {
        isDrag = false;
    }

    @Override
    public int getHeight() {
        return super.getHeight() + 6;
    }
}
