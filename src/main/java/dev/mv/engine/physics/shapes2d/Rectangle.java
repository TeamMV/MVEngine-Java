package dev.mv.engine.physics.shapes2d;

import dev.mv.engine.MVEngine;
import org.joml.Vector2i;

public class Rectangle extends Shape2D {
    protected int width, height;

    public Rectangle(int x, int y, int width, int height) {
        super(MVEngine.instance().getPhysics2D(), x, y, new Vector2i(x + width / 2, y + height / 2));
        this.width = width;
        this.height = height;
    }

    public Rectangle(int x, int y, int width, int height, float rotation) {
        super(MVEngine.instance().getPhysics2D(), x, y, new Vector2i(x + width / 2, y + height / 2));
        this.width = width;
        this.height = height;
        setRotation(rotation);
    }

    @Override
    public boolean isCollidingWith(Shape2D shape) {
        return physics.getCollider(this, shape).checkCollision(this, shape);
    }

    @Override
    public boolean isSameType(Shape2D shape) {
        return shape instanceof Rectangle;
    }

    @Override
    protected void recalculateVertices() {

    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }
}
