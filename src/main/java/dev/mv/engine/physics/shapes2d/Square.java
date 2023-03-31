package dev.mv.engine.physics.shapes2d;

public class Square extends Rectangle {

    public Square(int x, int y, int size) {
        super(x, y, size, size);
    }

    public Square(int x, int y, int size, float rotation) {
        super(x, y, size, size, rotation);
    }

    public int getSize() {
        return width;
    }

    @Override
    public boolean isSameType(Shape2D shape) {
        return shape instanceof Square;
    }
}
