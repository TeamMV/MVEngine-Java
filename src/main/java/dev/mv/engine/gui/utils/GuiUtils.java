package dev.mv.engine.gui.utils;

import dev.mv.engine.gui.components.Element;
import dev.mv.engine.gui.components.extras.IgnoreDraw;
import dev.mv.engine.gui.theme.Theme;
import dev.mv.engine.input.Input;
import dev.mv.utils.Utils;

public class GuiUtils {
    public static boolean mouseNotInside(int x, int y, int width, int height) {
        return !mouseInside(x, y, width, height);
    }

    public static boolean mouseInside(int x, int y, int width, int height) {
        int mx = Input.mouse[Input.MOUSE_X];
        int my = Input.mouse[Input.MOUSE_Y];
        return (
            mx >= x && mx <= x + width &&
                my >= y && my <= y + height);
    }

    public static boolean mouseNotInside(int x, int y, int width, int height, Theme.EdgeStyle style, int radius) {
        return !mouseInside(x, y, width, height, style, radius);
    }
    
    public static boolean mouseInside(int x, int y, int width, int height, Theme.EdgeStyle style, int radius) {
        if (mouseNotInside(x, y, width, height)) return false;
        if (style == Theme.EdgeStyle.SQUARE) return true;
        int mx = Input.mouse[Input.MOUSE_X];
        int my = Input.mouse[Input.MOUSE_Y];
        if (style == Theme.EdgeStyle.TRIANGLE) {
            if (mx - x < my - (y + height - radius)) {
                return false;
            }
            if ((x + width) - mx < my - (y + height - radius)) {
                return false;
            }
            if (mx - x < (y + radius) - my) {
                return false;
            }
            if (mx - (x + width - radius) > my - y) {
                return false;
            }
        }
        else if (style == Theme.EdgeStyle.ROUND) {
            if (mx < x + radius && my > y + height - radius && Utils.square((x + radius) - mx) + Utils.square(my - (y + height - radius)) > Utils.square(radius)) {
                return false;
            }
            if (mx > x + width - radius && my > y + height - radius && Utils.square(mx - (x + width - radius)) + Utils.square(my - (y + height - radius)) > Utils.square(radius)) {
                return false;
            }
            if (mx < x + radius && my < y + radius && Utils.square((x + radius) - mx) + Utils.square((y + radius) - my) > Utils.square(radius)) {
                return false;
            }
            if (mx > x + width - radius && my < y + radius && Utils.square(mx - (x + width - radius)) + Utils.square((y + radius) - my) > Utils.square(radius)) {
                return false;
            }
        }
        return true;
    }

    public static boolean mouseNotInside(int x, int y, int width, int height, Theme theme) {
        return !mouseInside(x, y, width, height, theme);
    }

    public static boolean mouseInside(int x, int y, int width, int height, Theme theme) {
        return mouseInside(x, y, width, height, theme.getEdgeStyle(), theme.getEdgeRadius());
    }

    public static boolean mouseNotInside(Element e) {
        if(e instanceof IgnoreDraw) return false;
        return !mouseInside(e.getX(), e.getY(), e.getWidth(), e.getHeight());
    }

    public static boolean mouseInside(Element e) {
        if(e instanceof IgnoreDraw) return true;
        return mouseInside(e.getX(), e.getY(), e.getWidth(), e.getHeight());
    }

    public static boolean mouseNotInsideThemed(Element e) {
        if(e instanceof IgnoreDraw) return false;
        return !mouseInside(e.getX(), e.getY(), e.getWidth(), e.getHeight(), e.getTheme().getEdgeStyle(), e.getTheme().getEdgeRadius());
    }

    public static boolean mouseInsideThemed(Element e) {
        if(e instanceof IgnoreDraw) return true;
        return mouseInside(e.getX(), e.getY(), e.getWidth(), e.getHeight(), e.getTheme().getEdgeStyle(), e.getTheme().getEdgeRadius());
    }
}
