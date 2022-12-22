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
