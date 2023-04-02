package dev.mv.engine.physics.shapes2d;

import dev.mv.engine.physics.Physics2D;
import org.joml.Vector2f;

public abstract class Shape2D {
    protected float x, y;
    protected float rotation;
    protected Vector2f center;
    protected final BoundingBox2D boundingBox = new BoundingBox2D();
    protected Physics2D physics;

    protected Shape2D(Physics2D physics, Vector2f center) {
        this.physics = physics;
        this.center = center;
        x = center.x;
        y = center.y;
    }

    protected Shape2D(Physics2D physics, float x, float y) {
        this.physics = physics;
        this.x = x;
        this.y = y;
        center = new Vector2f(x, y);
    }

    protected Shape2D(Physics2D physics, float x, float y, Vector2f center) {
        this.physics = physics;
        this.x = x;
        this.y = y;
        this.center = center;
    }

    public abstract boolean isCollidingWith(Shape2D shape);
    public abstract boolean equalsType(Shape2D shape);
    public abstract void scale(float factor);
    public abstract void updateBoundingBox();

    public void moveX(float amount) {
        this.x += amount;
        this.center.x += amount;
        updateBoundingBox();
    }

    public void moveY(float amount) {
        this.y += amount;
        this.center.y += amount;
        updateBoundingBox();
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        moveX(x - this.x);
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        moveY(y - this.y);
    }

    public float getCenterX() {
        return center.x;
    }

    public void setCenterX(float x) {
        moveX(x - center.x);
    }

    public float getCenterY() {
        return center.y;
    }

    public void setCenterY(float y) {
        moveY(y - center.y);
    }

    public float getRotation() {
        return rotation;
    }

    public void setRotation(float rotation) {
        this.rotation = rotation % 360;
        if (this.rotation < 0) this.rotation += 360;
    }

    public BoundingBox2D getBoundingBox() {
        return boundingBox;
    }

    public static class BoundingBox2D {

        float x, y, w, h;

        private BoundingBox2D() {}

        public boolean isColliding(BoundingBox2D b) {
            return x < b.x + b.w &&
                x + w > b.x &&
                y < b.y + b.h &&
                y + h > b.y;
        }
    }
}
