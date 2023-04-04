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
        boundingBox.s = radius + radius;
    }

    @Override
    public void scale(float factor) {
        radius *= factor;
        rVec.x = radius;
        updateBoundingBox();
    }

    public float getRadius() {
        return radius;
    }

    public void setRadius(float radius) {
        this.radius = radius;
        rVec.x = radius;
        updateBoundingBox();
    }

    @Override
    public float getRadiusA() {
        return radius;
    }

    @Override
    public void setRadiusA(float a) {
        this.radius = a;
        updateBoundingBox();
    }

    @Override
    public float getRadiusB() {
        return radius;
    }

    @Override
    public void setRadiusB(float b) {
        this.radius = b;
        updateBoundingBox();
    }

    @Override
    public float getRadiusVecX() {
        return radius;
    }

    @Override
    public float getRadiusVecY() {
        return 0;
    }

    @Override
    public float getRadiusRatio() {
        return 1;
    }

    @Override
    public float getRotation() {
        return 0;
    }

    @Override
    public void setRotation(float rotation) {}

}
