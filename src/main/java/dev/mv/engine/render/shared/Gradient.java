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
        gradient.topLeft = this.topLeft;
        gradient.topRight = this.topRight;
        gradient.bottomLeft = this.bottomLeft;
        gradient.bottomRight = this.bottomRight;
        return gradient;
    }
}
