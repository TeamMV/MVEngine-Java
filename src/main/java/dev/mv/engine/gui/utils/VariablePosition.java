package dev.mv.engine.gui.utils;

import dev.mv.engine.exceptions.Exceptions;
import dev.mv.engine.exceptions.InvalidUnitException;
import dev.mv.utils.Utils;

import java.awt.*;

public class VariablePosition {
    private static int w = 800, h = 600;
    private int x, y, width, height;
    private boolean sizeRelative;
    private PositionCalculator pos;

    public VariablePosition(int width, int height, PositionCalculator pos, boolean sizeRelative) {
        this.pos = pos;
        resize(width, height, width, height);
        this.sizeRelative = sizeRelative;
    }

    public static VariablePosition fromScreenPosition(int x, int y, int width, int height, int sWidth, int sHeight, boolean sizeRelative) {
        return new VariablePosition(sWidth, sHeight, (w, h, sw, sh) ->
            new int[]{
                (int) (((float) x / (float) sWidth) * w),
                (int) (((float) y / (float) sHeight) * h),
                (int) (((float) width / (float) sWidth) * w),
                (int) (((float) height / (float) sHeight) * h)
            }, sizeRelative
        );
    }

    public static void setup(int width, int height) {
        w = width;
        h = height;
    }

    public static VariablePosition getPosition(String fx, String fy, String fwidth, String fheight) {
        return new VariablePosition(w, h, (width, height, wwidth, wheight) -> {
            int x = Utils.ifNotNull(fx).thenReturn(xStr -> parse(xStr, width, wwidth, wheight)).otherwiseReturn(() -> 0).getGenericReturnValue().<Integer>value();
            int y = Utils.ifNotNull(fy).thenReturn(yStr -> parse(yStr, height, wwidth, wheight)).otherwiseReturn(() -> 0).getGenericReturnValue().<Integer>value();
            int cWidth = Utils.ifNotNull(fwidth).thenReturn(wStr -> parse(wStr, width, wwidth, wheight)).otherwiseReturn(() -> 0).getGenericReturnValue().<Integer>value();
            int cHeight = Utils.ifNotNull(fheight).thenReturn(hStr -> parse(hStr, height, wwidth, wheight)).otherwiseReturn(() -> 0).getGenericReturnValue().<Integer>value();
            return new int[]{x, y, cWidth, cHeight};
        }, ((fwidth != null && fwidth.endsWith("%")) || (fheight != null && fheight.endsWith("%"))));
    }

    private static int parse(String value, int max, int wwidth, int wheight) {
        boolean invert = false;
        int result = 0;

        if (value == null || value.isEmpty()) return 0;

        if (value.startsWith("-")) {
            value = value.split("-")[1];
            invert = true;
        }

        if (value.endsWith("px") || value.matches("[0-9]+")) {
            result = Integer.parseInt(value.replaceAll("px", ""));
        } else if (value.endsWith("%")) {
            result = (int) ((Float.parseFloat(value.replaceAll("%", "")) / 100f) * max);
        } else if (value.endsWith("vw")) {
            result = (int) ((Float.parseFloat(value.replaceAll("vw", "")) / 100f) * wwidth);
        } else if (value.endsWith("vh")) {
            result = (int) ((Float.parseFloat(value.replaceAll("vh", "")) / 100f) * wheight);
        } else if (value.endsWith("in")) {
            result = (int) (Toolkit.getDefaultToolkit().getScreenResolution() * (float) Integer.parseInt(value.replaceAll("in", "")));
        } else if (value.endsWith("mm")) {
            result = (int) (Toolkit.getDefaultToolkit().getScreenResolution() * 25.4f * (float) Integer.parseInt(value.replaceAll("mm", "")));
        } else if (value.endsWith("cm")) {
            result = (int) (Toolkit.getDefaultToolkit().getScreenResolution() * 2.54f * (float) Integer.parseInt(value.replaceAll("cm", "")));
        } else {
            Exceptions.send(new InvalidUnitException("Position numbers must either end with \"%\" or \"px\"!"));
        }

        if (invert) {
            result = max - result;
        }

        Toolkit.getDefaultToolkit().getScreenResolution();

        return result;
    }

    public void resize(int width, int height, int windowWidth, int windowHeight) {
        int[] size = pos.resize(width, height, windowWidth, windowHeight);
        x = size[0];
        y = size[1];
        this.width = size[2];
        this.height = size[3];
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public boolean isSizeRelative() {
        return sizeRelative;
    }
}