package dev.mv.engine.physics.shapes2d;

import dev.mv.engine.physics.Physics2D;
import org.joml.Vector2i;

public abstract class Shape2D {
    protected int x, y;
    protected float rotation;
    protected Vector2i center;
    protected Physics2D physics;
    protected Vertices vertices;

    protected Shape2D(Physics2D physics, Vector2i center) {
        this.physics = physics;
        this.center = center;
        x = center.x;
        y = center.y;
    }

    protected Shape2D(Physics2D physics, int x, int y) {
        this.physics = physics;
        this.x = x;
        this.y = y;
        center = new Vector2i(x, y);
    }

    protected Shape2D(Physics2D physics, int x, int y, Vector2i center) {
        this.physics = physics;
        this.x = x;
        this.y = y;
        this.center = center;
    }

    public abstract boolean isCollidingWith(Shape2D shape);

    public abstract boolean isSameType(Shape2D shape);

    protected abstract void recalculateVertices();

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
        recalculateVertices();
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
        recalculateVertices();
    }

    public Vector2i getCenter() {
        return center;
    }

    public float getRotation() {
        return rotation;
    }

    public void setRotation(float rotation) {
        this.rotation = rotation % 360;
        if (this.rotation < 0) this.rotation += 360;
        recalculateVertices();
    }

    public Vertices getVertices() {
        return vertices;
    }
}
