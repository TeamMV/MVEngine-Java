package dev.mv.engine.render.shared;

public class Color {
    public static Color WHITE = new Color(255, 255, 255, 255);
    public static Color BLACK = new Color(0, 0, 0, 255);
    public static Color RED = new Color(255, 0, 0, 255);
    public static Color GREEN = new Color(0, 255, 0, 255);
    public static Color BLUE = new Color(0, 0, 255, 255);
    public static Color YELLOW = new Color(255, 255, 0, 255);
    public static Color MAGENTA = new Color(255, 0, 255, 255);
    public static Color CYAN = new Color(0, 255, 255, 255);

    byte r, g, b, a;

    public Color(byte r, byte g, byte b, byte a) {
        this.r = r;
        this.g = g;
        this.b = b;
        this.a = a;
    }

    public Color(int r, int g, int b, int a) {
        this.r = (byte) r;
        this.g = (byte) g;
        this.b = (byte) b;
        this.a = (byte) a;
    }

    public Color(int color) {
        this(color >> 24 & 0xff, color >> 16 & 0xff, color >> 8 & 0xff, color & 0xff);
    }

    public Color set(byte r, byte g, byte b, byte a) {
        this.r = r;
        this.g = g;
        this.b = b;
        this.a = a;
        return this;
    }

    public Color set(int r, int g, int b, int a) {
        this.r = (byte) r;
        this.g = (byte) g;
        this.b = (byte) b;
        this.a = (byte) a;
        return this;
    }

    public Color set(int color) {
        set(color >> 24 & 0xff, color >> 16 & 0xff, color >> 8 & 0xff, color & 0xff);
        return this;
    }

    public int getRed() {
        return r < 0 ? 256 - r : r;
    }

    public void setRed(byte r) {
        this.r = r;
    }

    public void setRed(int r) {
        this.r = (byte) r;
    }

    public int getGreen() {
        return g < 0 ? 256 - g : g;
    }

    public void setGreen(byte g) {
        this.g = g;
    }

    public void setGreen(int g) {
        this.g = (byte) g;
    }

    public int getBlue() {
        return b < 0 ? 256 - b : b;
    }

    public void setBlue(byte b) {
        this.b = b;
    }

    public void setBlue(int b) {
        this.b = (byte) b;
    }

    public int getAlpha() {
        return a < 0 ? 256 - a : a;
    }

    public void setAlpha(byte a) {
        this.a = a;
    }

    public void setAlpha(int a) {
        this.a = (byte) a;
    }

    public NormalizedColor normalize(float normalizeTreshold) {
        return new NormalizedColor(r / (255.0f / normalizeTreshold), g / (255.0f / normalizeTreshold), b / (255.0f / normalizeTreshold), a / (255.0f / normalizeTreshold));
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
        byte r = (byte) (rgb[0] * 255);
        byte g = (byte) (rgb[1] * 255);
        byte b = (byte) (rgb[2] * 255);
        return set(r, g, b, 255);
    }

    public void copyFrom(Color other) {
        set(other.getRed(), other.getGreen(), other.getBlue(), other.getAlpha());
    }

    public int toInt() {
        return (r << 24) | (g << 16) | (b << 8) | a;
    }
}
