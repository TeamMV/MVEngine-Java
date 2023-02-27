package dev.mv.engine.render.shared;

public class NormalizedColor {

    float r, g, b, a;

    public NormalizedColor(float r, float g, float b, float a) {
        this.r = r;
        this.g = g;
        this.b = b;
        this.a = a;
    }

    public float getRed() {
        return r;
    }

    public void setRed(float r) {
        this.r = r;
    }

    public float getGreen() {
        return g;
    }

    public void setGreen(float g) {
        this.g = g;
    }

    public float getBlue() {
        return b;
    }

    public void setBlue(float b) {
        this.b = b;
    }

    public float getAlpha() {
        return a;
    }

    public void setAlpha(float a) {
        this.a = a;
    }
    
    public NormalizedColor copy() {
        return new NormalizedColor(r, g, b, a);
    }
    
    public NormalizedColor set(float r, float g, float b, float a) {
        this.r = r;
        this.g = g;
        this.b = b;
        this.a = a;
        return this;
    }

    public NormalizedColor toRGB(float hue, float saturation, float value) {
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
        float r = rgb[0] * 255;
        float g = rgb[1] * 255;
        float b = rgb[2] * 255;
        return set(r, g, b, 1f);
    }
}
