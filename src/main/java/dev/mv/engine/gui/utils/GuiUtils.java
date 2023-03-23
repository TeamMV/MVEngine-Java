package dev.mv.engine.gui.utils;

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
}
