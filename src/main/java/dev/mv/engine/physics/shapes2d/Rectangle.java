package dev.mv.engine.physics.shapes2d;

import dev.mv.engine.MVEngine;

public class Rectangle extends Shape2D {
    private int width, height;
    private float rotation;

    public Rectangle(int x, int y, int width, int height) {
        super(MVEngine.instance().getPhysics2D(), x, y);
        this.width = width;
        this.height = height;
    }

    public Rectangle(int x, int y, int width, int height, float rotation) {
        super(MVEngine.instance().getPhysics2D(),x, y);
        this.width = width;
        this.height = height;
        this.rotation = rotation % 360;
        if (this.rotation < 0) this.rotation += 360;
    }

    @Override
    public boolean isCollidingWith(Shape2D shape) {
        return physics.getCollider(this, shape).checkCollision(this, shape);
    }

    @Override
    public boolean isSameType(Shape2D shape) {
        return shape instanceof Rectangle;
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

    public float getRotation() {
        return rotation;
    }

    public void setRotation(float rotation) {
        this.rotation = rotation % 360;
        if (this.rotation < 0) this.rotation += 360;
    }
}
