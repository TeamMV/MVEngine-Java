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
        if (r <= 1f)
            this.r = r;
        else
            this.r = r / 255f;
        if (g <= 1f)
            this.g = g;
        else
            this.g = g / 255f;
        if (b <= 1f)
            this.b = b;
        else
            this.b = b / 255f;
        if (a <= 1f)
            this.a = a;
        else
            this.a = a / 255f;
        return this;
    }

    public NormalizedColor fromHue(float hue) {
        return fromHsv(hue, 1, 1);
    }

    public NormalizedColor fromHsv(float hue, float saturation, float value) {
        final float c = saturation * value;
        final float h = hue / 60;
        final float x = c * (1 - Math.abs(h % 2 - 1));
        final float m = value - saturation;

        float[] rgb = switch ((int) Math.floor(h)) {
            case 0 -> new float[]{c, x, 0};
            case 1 -> new float[]{x, c, 0};
            case 2 -> new float[]{0, c, x};
            case 3 -> new float[]{0, x, c};
            case 4 -> new float[]{x, 0, c};
            case 5, 6 -> new float[]{c, 0, x};
            default -> throw new IllegalStateException();
        };
        return set(rgb[0] + m, rgb[1] + m, rgb[2] + m, 1f);
    }

    @Override
    public String toString() {
        return "NormalizedColor{" +
            "r=" + r +
            ", g=" + g +
            ", b=" + b +
            ", a=" + a +
            '}';
    }
}
