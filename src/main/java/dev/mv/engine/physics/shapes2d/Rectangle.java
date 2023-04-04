package dev.mv.engine.physics.shapes2d;

import dev.mv.engine.MVEngine;
import org.joml.Vector2f;

public class Rectangle extends Shape2D {
    protected float width, height;

    public Rectangle(float x, float y, float width, float height) {
        super(MVEngine.instance().getPhysics2D(), x, y, new Vector2f(x + width / 2, y + height / 2));
        this.width = width;
        this.height = height;
        updateBoundingBox();
    }

    public Rectangle(float x, float y, float width, float height, float rotation) {
        super(MVEngine.instance().getPhysics2D(), x, y, new Vector2f(x + width / 2, y + height / 2));
        this.width = width;
        this.height = height;
        setRotation(rotation);
        updateBoundingBox();
    }

    @Override
    public boolean isCollidingWith(Shape2D shape) {
        return physics.getCollider(this, shape).checkCollision(this, shape);
    }

    @Override
    public boolean equalsType(Shape2D shape) {
        return shape instanceof Rectangle;
    }

    @Override
    public void updateBoundingBox() {
        float size = Math.max(width, height) / 2;
        boundingBox.x = center.x - size;
        boundingBox.y = center.y - size;
        boundingBox.s = size + size;
    }

    @Override
    public void scale(float factor) {
        width *= factor;
        height *= factor;
        x = center.x - width / 2;
        y = center.y - height / 2;
        updateBoundingBox();
    }

    public float getWidth() {
        return width;
    }

    public void setWidth(float width) {
        this.width = width;
        updateBoundingBox();
    }

    public float getHeight() {
        return height;
    }

    public void setHeight(float height) {
        this.height = height;
        updateBoundingBox();
    }
}
