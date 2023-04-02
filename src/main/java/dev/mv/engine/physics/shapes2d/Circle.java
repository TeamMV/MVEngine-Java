package dev.mv.engine.physics.shapes2d;

import org.joml.Vector2f;

public class Circle extends Oval {

    private float radius;

    public Circle(float x, float y, float radius) {
        super(new Vector2f(x, y), radius);
        this.radius = radius;
    }

    @Override
    public boolean isCollidingWith(Shape2D shape) {
        return physics.getCollider(this, shape).checkCollision(this, shape);
    }

    @Override
    public boolean equalsType(Shape2D shape) {
        return shape instanceof Circle;
    }

    @Override
    public void updateBoundingBox() {
        boundingBox.x = center.x - radius;
        boundingBox.y = center.y - radius;
        boundingBox.w = center.x + radius;
        boundingBox.h = center.y + radius;
    }

    @Override
    public void scale(float factor) {
        radius *= factor;
        a = radius;
        b = radius;
        c = radius * 2;
        rVec.x = radius;
        updateBoundingBox();
    }

    public float getRadius() {
        return radius;
    }

    public void setRadius(float radius) {
        this.radius = radius;
        a = radius;
        b = radius;
        rVec.x = radius;
        c = radius * 2;
        updateBoundingBox();
    }

    @Override
    public void setFocusA(Vector2f focA) {
        this.focA = focA;
        focB = focA;
        setX(focA.x);
        setY(focA.y);
    }

    @Override
    public void setFocusB(Vector2f focB) {
        setFocusA(focB);
    }

    @Override
    public void setConstant(float c) {
        setRadius(c / 2);
    }

    @Override
    public float getRotation() {
        return 0;
    }

    @Override
    public void setRotation(float rotation) {
    }
}
