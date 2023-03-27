package dev.mv.engine.gui.components;

import dev.mv.engine.gui.components.extras.Toggle;
import dev.mv.engine.gui.components.extras.ValueChange;
import dev.mv.engine.gui.event.EventListener;
import dev.mv.engine.gui.event.ProgressListener;
import dev.mv.engine.gui.theme.Theme;
import dev.mv.engine.gui.utils.VariablePosition;
import dev.mv.engine.render.shared.DrawContext2D;
import dev.mv.engine.render.shared.Window;
import dev.mv.utils.Utils;

public class ProgressBar extends Element implements Toggle, ValueChange {
    private float currentValue = 0;
    private int totalValue = 0;
    private boolean enabled = true;

    public ProgressBar(Window window, int x, int y, int width, int height, Element parent) {
        super(window, x, y, width, height, parent);
    }

    public ProgressBar(Window window, int width, int height, Element parent) {
        super(window, -1, -1, width, height, parent);
    }

    public ProgressBar(Window window, int x, int y, int width, int height) {
        super(window, x, y, width, height, null);
    }

    public ProgressBar(Window window, VariablePosition position, Element parent) {
        super(window, position, parent);
    }

    public float getCurrentValue() {
        return currentValue;
    }

    public void setCurrentValue(int currentValue) {
        this.currentValue = currentValue;
    }

    public int getTotalValue() {
        return totalValue;
    }

    public void setTotalValue(int totalValue) {
        this.totalValue = totalValue;
    }

    public float getPercentage() {
        return Utils.getPercent((int) currentValue, totalValue);
    }

    public void setPercentage(float percentage) {
        currentValue = Utils.getValue(percentage, totalValue);
    }

    @Override
    public void draw(DrawContext2D draw) {
        checkAnimations();

        if (theme.getEdgeStyle() == Theme.EdgeStyle.ROUND) {
            if (theme.hasOutline()) {
                int thickness = theme.getOutlineThickness();
                draw.color(animationState.outlineColor);
                if (!enabled) {
                    draw.color(theme.getDisabledOutlineColor());
                }
                draw.voidRoundedRectangle(animationState.posX, animationState.posY, animationState.width, animationState.height, thickness, theme.getEdgeRadius() + thickness, theme.getEdgeRadius(), animationState.rotation, animationState.originX, animationState.originY);
                draw.color(animationState.baseColor);
                if (!enabled) {
                    draw.color(theme.getDisabledBaseColor());
                }
                draw.roundedRectangle(animationState.posX + thickness, animationState.posY + thickness, animationState.width - 2 * thickness, animationState.height - 2 * thickness, theme.getEdgeRadius(), theme.getEdgeRadius(), animationState.rotation, animationState.originX, animationState.originY);

                draw.color(animationState.extraColor);
                draw.roundedRectangle(animationState.posX + thickness, animationState.posY + thickness, Math.max(Utils.getValue(15, totalValue), Utils.getValue(Utils.getPercent(currentValue, totalValue), animationState.width)) - 2 * thickness, animationState.height - 2 * thickness, theme.getEdgeRadius(), theme.getEdgeRadius(), animationState.rotation, animationState.originX, animationState.originY);
            } else {
                draw.color(animationState.baseColor);
                draw.roundedRectangle(animationState.posX, animationState.posY, animationState.width, animationState.height, theme.getEdgeRadius(), theme.getEdgeRadius(), animationState.rotation, animationState.originX, animationState.originY);

                draw.color(animationState.extraColor);
                draw.roundedRectangle(animationState.posX, animationState.posY, Math.max(Utils.getValue(15, totalValue), Utils.getValue(Utils.getPercent(currentValue, totalValue), animationState.width)), animationState.height, theme.getEdgeRadius(), theme.getEdgeRadius(), animationState.rotation, animationState.originX, animationState.originY);
            }
        } else if (theme.getEdgeStyle() == Theme.EdgeStyle.TRIANGLE) {
            if (theme.hasOutline()) {
                int thickness = theme.getOutlineThickness();
                draw.color(animationState.outlineColor);
                if (!enabled) {
                    draw.color(theme.getDisabledOutlineColor());
                }
                draw.voidTriangularRectangle(animationState.posX, animationState.posY, animationState.width, animationState.height, thickness, theme.getEdgeRadius() + thickness, animationState.rotation, animationState.originX, animationState.originY);
                draw.color(animationState.baseColor);
                if (!enabled) {
                    draw.color(theme.getDisabledBaseColor());
                }
                draw.triangularRectangle(animationState.posX + thickness, animationState.posY + thickness, animationState.width - 2 * thickness, animationState.height - 2 * thickness, theme.getEdgeRadius(), animationState.rotation, animationState.originX, animationState.originY);
                draw.color(animationState.extraColor);
                draw.triangularRectangle(animationState.posX + thickness, animationState.posY + thickness, Math.max(Utils.getValue(15, totalValue), Utils.getValue(Utils.getPercent(currentValue, totalValue), animationState.width)) - 2 * thickness, animationState.height - 2 * thickness, theme.getEdgeRadius(), animationState.rotation, animationState.originX, animationState.originY);
            } else {
                draw.color(animationState.baseColor);
                draw.triangularRectangle(animationState.posX, animationState.posY, animationState.width, animationState.height, theme.getEdgeRadius(), animationState.rotation, animationState.originX, animationState.originY);

                draw.color(animationState.extraColor);
                draw.triangularRectangle(animationState.posX, animationState.posY, Math.max(Utils.getValue(15, totalValue), Utils.getValue(Utils.getPercent(currentValue, totalValue), animationState.width)), animationState.height, theme.getEdgeRadius(), animationState.rotation, animationState.originX, animationState.originY);
            }
        } else if (theme.getEdgeStyle() == Theme.EdgeStyle.SQUARE) {
            if (theme.hasOutline()) {
                int thickness = theme.getOutlineThickness();
                draw.color(animationState.outlineColor);
                if (!enabled) {
                    draw.color(theme.getDisabledOutlineColor());
                }
                draw.voidRectangle(animationState.posX, animationState.posY, animationState.width, animationState.height, thickness, animationState.rotation, animationState.originX, animationState.originY);
                draw.color(animationState.baseColor);
                if (!enabled) {
                    draw.color(theme.getDisabledBaseColor());
                }
                draw.rectangle(animationState.posX + thickness, animationState.posY + thickness, animationState.width - 2 * thickness, animationState.height - 2 * thickness, animationState.rotation, animationState.originX, animationState.originY);

                draw.color(animationState.extraColor);
                draw.rectangle(animationState.posX + thickness, animationState.posY + thickness, Math.max(Utils.getValue(0, totalValue), Utils.getValue(Utils.getPercent(currentValue, totalValue), animationState.width)) - 2 * thickness, animationState.height - 2 * thickness, animationState.rotation, animationState.originX, animationState.originY);
            } else {
                draw.color(animationState.baseColor);
                draw.rectangle(animationState.posX, animationState.posY, animationState.width, animationState.height, animationState.rotation, animationState.originX, animationState.originY);

                draw.color(animationState.extraColor);
                draw.rectangle(animationState.posX, animationState.posY, Math.max(Utils.getValue(0, totalValue), Utils.getValue(Utils.getPercent(currentValue, totalValue), animationState.width)), animationState.height, animationState.rotation, animationState.originX, animationState.originY);
            }
        }
    }

    @Override
    public void attachListener(EventListener listener) {
        if (listener instanceof ProgressListener progressListener) {
            progressListeners.add(progressListener);
        }
    }

    @Override
    public void disable() {
        enabled = false;
    }

    @Override
    public void enable() {
        enabled = true;
    }

    @Override
    public void toggle() {
        enabled = !enabled;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public void increment(float amount) {
        currentValue = Utils.clamp((int) (currentValue + amount), 0, totalValue);
        if (!progressListeners.isEmpty()) {
            progressListeners.forEach(l -> l.onIncrement((Element) this, (int) currentValue, totalValue, (int) getPercentage()));
        }
    }

    @Override
    public void decrement(float amount) {
        currentValue = Utils.clamp((int) (currentValue - amount), 0, totalValue);
        if (!progressListeners.isEmpty()) {
            progressListeners.forEach(l -> l.onDecrement(this, (int) currentValue, totalValue, (int) getPercentage()));
        }
    }

    @Override
    public void incrementByPercentage(int amount) {
        increment(Utils.getValue(amount, totalValue));
    }

    @Override
    public void decrementByPercentage(int amount) {
        decrement(Utils.getValue(amount, totalValue));
    }
}
