package dev.mv.engine.physics.shapes2d;

import dev.mv.engine.MVEngine;
import org.joml.Matrix2f;

public class Rectangle extends Shape2D {
    private int x, y, width, height;
    private float rotation;
    private Matrix2f inverse;

    public Rectangle(int x, int y, int width, int height) {
        super(MVEngine.instance().getPhysics2D());
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        inverse = new Matrix2f();
    }

    public Rectangle(int x, int y, int width, int height, float rotation) {
        super(MVEngine.instance().getPhysics2D());
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.rotation = rotation % 360;
        if (this.rotation < 0) this.rotation += 360;
        inverse = new Matrix2f();
        inverse.rotate(rotation);
    }

    @Override
    public boolean isCollidingWith(Shape2D shape) {
        return physics.getCollider(this, shape).checkCollision(this, shape);
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
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
        inverse.rotate(rotation);
    }
}
