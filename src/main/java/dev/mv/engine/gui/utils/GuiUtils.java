package dev.mv.engine.gui.utils;

public class GuiUtils {
    public static boolean mouseNotInside(int my, int mx, int x, int y, int width, int height) {
        return !(
            mx >= x && mx <= x + width &&
            my >= y && my <= y + height);
    }
}
