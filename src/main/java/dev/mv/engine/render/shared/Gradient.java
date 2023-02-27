package dev.mv.engine.render.shared;

public class Gradient {
    public NormalizedColor topLeft;
    public NormalizedColor topRight;
    public NormalizedColor bottomLeft;
    public NormalizedColor bottomRight;

    public Gradient() {
        topLeft = new NormalizedColor(0, 0, 0, 0);
        topRight = new NormalizedColor(0, 0, 0, 0);
        bottomLeft = new NormalizedColor(0, 0, 0, 0);
        bottomRight = new NormalizedColor(0, 0, 0, 0);
    }

    public void setTop(NormalizedColor color) {
        topLeft.set(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
        topRight.set(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
    }

    public void setBottom(NormalizedColor color) {
        bottomLeft.set(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
        bottomRight.set(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
    }

    public void setLeft(NormalizedColor color) {
        topLeft.set(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
        bottomLeft.set(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
    }

    public void setRight(NormalizedColor color) {
        topRight.set(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
        bottomRight.set(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
    }

    public void setTop(float r, float g, float b, float a) {
        topLeft.set(r, g, b, a);
        topRight.set(r, g, b, a);
    }

    public void setBottom(float r, float g, float b, float a) {
        bottomLeft.set(r, g, b, a);
        bottomRight.set(r, g, b, a);
    }

    public void setLeft(float r, float g, float b, float a) {
        topLeft.set(r, g, b, a);
        bottomLeft.set(r, g, b, a);
    }

    public void setRight(float r, float g, float b, float a) {
        topRight.set(r, g, b, a);
        bottomRight.set(r, g, b, a);
    }

    @Override
    public String toString() {
        return "Gradient{" +
            "topLeft=" + topLeft +
            ", topRight=" + topRight +
            ", bottomLeft=" + bottomLeft +
            ", bottomRight=" + bottomRight +
            '}';
    }

    public Gradient copy() {
        Gradient gradient = new Gradient();
        gradient.topLeft = this.topLeft.copy();
        gradient.topRight = this.topRight.copy();
        gradient.bottomLeft = this.bottomLeft.copy();
        gradient.bottomRight = this.bottomRight.copy();
        return gradient;
    }

    public void resetTo(float r, float g, float b, float a) {
        topLeft.set(r, g, b, a);
        topRight.set(r, g, b, a);
        bottomLeft.set(r, g, b, a);
        bottomRight.set(r, g, b, a);
    }
}
