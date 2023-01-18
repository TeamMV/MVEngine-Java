package dev.mv.engine.render.shared;

import java.util.Arrays;

public class Color {
    public static Color WHITE = new Color(255, 255, 255, 255);
    public static Color BLACK = new Color(0, 0, 0, 255);
    public static Color RED = new Color(255, 0, 0, 255);
    public static Color GREEN = new Color(0, 255, 0, 255);
    public static Color BLUE = new Color(0, 0, 255, 255);
    public static Color YELLOW = new Color(255, 255, 0, 255);
    public static Color MAGENTA = new Color(255, 0, 255, 255);
    public static Color CYAN = new Color(0, 255, 255, 255);

    float r, g, b, a;

    public Color(float r, float g, float b, float a) {
        this.r = r;
        this.g = g;
        this.b = b;
        this.a = a;
    }

    public Color set(int r, int g, int b, int a) {
        this.r = r;
        this.g = g;
        this.b = b;
        this.a = a;
        return this;
    }

    public Color set(float r, float g, float b, float a) {
        this.r = r;
        this.g = g;
        this.b = b;
        this.a = a;
        return this;
    }

    public float getRed() {
        return r;
    }

    public float getGreen() {
        return g;
    }

    public float getBlue() {
        return b;
    }

    public float getAlpha() {
        return a;
    }

    public void setRed(float r) {
        this.r = r;
    }

    public void setGreen(float g) {
        this.g = g;
    }

    public void setBlue(float b) {
        this.b = b;
    }

    public void setAlpha(float a) {
        this.a = a;
    }

    public Color normalize(float normalizeTreshold) {
        r = r / (255.0f / normalizeTreshold);
        g = g / (255.0f / normalizeTreshold);
        b = b / (255.0f / normalizeTreshold);
        a = a / (255.0f / normalizeTreshold);
        return this;
    }

    public Color copy() {
        return new Color(r, g, b, a);
    }

    public void copyValuesTo(Color dest) {
        dest.setRed(r);
        dest.setGreen(g);
        dest.setBlue(b);
        dest.setAlpha(a);
    }

    @Override
    public String toString() {
        return "Color{" +
            "r=" + r +
            ", g=" + g +
            ", b=" + b +
            ", a=" + a +
            '}';
    }

    public Color toRGB(float hue, float saturation, float value) {
        final int h = (int) hue / 60;
        final float f = hue / 60 - h;
        final float p = value * (1 - saturation);
        final float q = value * (1 - f * saturation);
        final float t = value * (1 - (1 - f) * saturation);

        float[] rgb = switch (h) {
            case 0 -> new float[]{value, t, p};
            case 1 -> new float[]{q, value, p};
            case 2 -> new float[]{p, value, t};
            case 3 -> new float[]{p, q, value};
            case 4 -> new float[]{t, p, value};
            case 5, 6 -> new float[]{value, p, q};
            default -> throw new IllegalStateException();
        };
        rgb[0] = rgb[0] * 255;
        rgb[1] = rgb[1] * 255;
        rgb[2] = rgb[2] * 255;
        return set(rgb[0], rgb[1], rgb[2], 255);

    }
}
