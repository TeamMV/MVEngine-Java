package dev.mv.engine.render.shared;

public class Color {
    float r, g, b, a;

    public Color(float r, float g, float b, float a) {
        this.r = r;
        this.g = g;
        this.b = b;
        this.a = a;
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
}
