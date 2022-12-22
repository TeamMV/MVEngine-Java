package dev.mv.engine.render.shared;

public class Gradient {
    public Color topLeft;
    public Color topRight;
    public Color bottomLeft;
    public Color bottomRight;

    public void setTop(Color color) {
        topLeft = color;
        topRight = color;
    }

    public void setBottom(Color color) {
        bottomLeft = color;
        bottomRight = color;
    }

    public void setLeft(Color color) {
        topLeft = color;
        bottomLeft = color;
    }

    public void setRight(Color color) {
        topRight = color;
        bottomRight = color;
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

    public Gradient normalize(float normalizedMaximum) {
        topLeft.normalize(normalizedMaximum);
        topRight.normalize(normalizedMaximum);
        bottomLeft.normalize(normalizedMaximum);
        bottomRight.normalize(normalizedMaximum);
        return this;
    }
}
