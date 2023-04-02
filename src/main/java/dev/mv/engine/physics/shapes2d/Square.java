package dev.mv.engine.physics.shapes2d;

public class Square extends Rectangle {

    public Square(float x, float y, float size) {
        super(x, y, size, size);
    }

    public Square(float x, float y, float size, float rotation) {
        super(x, y, size, size, rotation);
    }

    public float getSize() {
        return width;
    }

    public void setSize(float size) {
        width = size;
        height = size;
        updateBoundingBox();
    }

    @Override
    public boolean equalsType(Shape2D shape) {
        return shape instanceof Square;
    }

    @Override
    public void updateBoundingBox() {
        boundingBox.x = center.x - width;
        boundingBox.y = center.y - width;
        boundingBox.w = center.x + width;
        boundingBox.h = center.y + width;
    }

    @Override
    public void scale(float factor) {
        width *= factor;
        height = width;
        float offset = width / 2;
        x = center.x - offset;
        y = center.y - offset;
        updateBoundingBox();
    }
}
