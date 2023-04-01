package dev.mv.engine.gui.utils;

import dev.mv.engine.gui.components.Element;
import dev.mv.engine.gui.components.extras.IgnoreDraw;
import dev.mv.engine.input.Input;

public class GuiUtils {
    public static boolean mouseNotInside(int x, int y, int width, int height) {
        int mx = Input.mouse[Input.MOUSE_X];
        int my = Input.mouse[Input.MOUSE_Y];
        return !(
            mx >= x && mx <= x + width &&
                my >= y && my <= y + height);
    }

    public static boolean mouseInside(int x, int y, int width, int height) {
        int mx = Input.mouse[Input.MOUSE_X];
        int my = Input.mouse[Input.MOUSE_Y];
        return (
            mx >= x && mx <= x + width &&
                my >= y && my <= y + height);
    }

    public static boolean mouseInside(Element e) {
        if(e instanceof IgnoreDraw) return true;
        return mouseInside(e.getX(), e.getY(), e.getWidth(), e.getHeight());
    }
}
