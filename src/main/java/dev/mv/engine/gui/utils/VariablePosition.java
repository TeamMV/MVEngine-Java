package dev.mv.engine.gui.utils;

import dev.mv.engine.MVEngine;
import dev.mv.engine.gui.parsing.InvalidUnitException;
import dev.mv.utils.Utils;

public class VariablePosition {
    private int x, y, width, height;
    private PositionCalculator pos;

    public VariablePosition(int width, int height, PositionCalculator pos) {
        this.pos = pos;
        resize(width, height);
    }

    public static VariablePosition fromScreenPosition(int x, int y, int width, int height, int sWidth, int sHeight) {
        return new VariablePosition(sWidth, sHeight, (w, h) ->
            new int[] {
                (int) (((float) x / (float) sWidth) * w),
                (int) (((float) y / (float) sHeight) * h),
                (int) (((float) width / (float) sWidth) * w),
                (int) (((float) height / (float) sHeight) * h)
            }
        );
    }

    public void resize(int width, int height) {
        int[] size = pos.resize(width, height);
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

    private static int w = 800, h = 600;

    public static void setup(int width, int height) {
        w = width;
        h = height;
    }

    public static VariablePosition getPosition(String fx, String fy, String fwidth, String fheight) {
        return new VariablePosition(w, h, (width, height) -> {
            int x = Utils.ifNotNull(fx).thenReturn(xStr -> parse(xStr, width)).otherwiseReturn(() -> 0).getGenericReturnValue().<Integer>value();
            int y = Utils.ifNotNull(fy).thenReturn(yStr -> parse(yStr, height)).otherwiseReturn(() -> 0).getGenericReturnValue().<Integer>value();
            int cWidth = Utils.ifNotNull(fwidth).thenReturn(wStr -> parse(wStr, width)).otherwiseReturn(() -> 0).getGenericReturnValue().<Integer>value();
            int cHeight = Utils.ifNotNull(fheight).thenReturn(hStr -> parse(hStr, height)).otherwiseReturn(() -> 0).getGenericReturnValue().<Integer>value();
            return new int[]{x, y, cWidth, cHeight};
        });
    }

    private static int parse(String value, int max) {
        boolean invert = false;
        int result = 0;

        if (value.startsWith("-")) {
            value = value.split("-")[1];
            invert = true;
        }

        if (value.endsWith("px")) {
            result = Integer.parseInt(value.replaceAll("px", ""));
        } else if (value.endsWith("%")) {
            result = (int) ((Float.parseFloat(value.replaceAll("%", "")) / 100f) * max);
        } else {
            MVEngine.Exceptions.__throw__(new InvalidUnitException("Position numbers must either end with \"%\" or \"px\"!"));
        }

        if (invert) {
            result = max - result;
        }

        return result;
    }
}