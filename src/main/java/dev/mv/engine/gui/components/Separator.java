package dev.mv.engine.gui.components;

import dev.mv.engine.gui.event.EventListener;
import dev.mv.engine.render.shared.DrawContext2D;
import dev.mv.engine.render.shared.Window;

public class Separator extends Element{

    public Separator(Window window, int x, int y, int width, int height, Element parent) {
        super(window, x, y, width, height, parent);
    }

    public Separator(Window window, int width, int height, Element parent) {
        super(window, -1, -1, width, height, parent);
    }

    public Separator(Window window, int x, int y, int width, int height) {
        super(window, x, y, width, height, null);
    }

    @Override
    public void draw(DrawContext2D draw) {
        checkAnimations();

        draw.color(animationState.outlineColor);
        draw.rectangle(animationState.posX, animationState.posY, animationState.width, animationState.height);
    }

    @Override
    public void attachListener(EventListener listener) {

    }
}
