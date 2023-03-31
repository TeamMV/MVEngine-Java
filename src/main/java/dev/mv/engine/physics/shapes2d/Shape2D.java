package dev.mv.engine.physics.shapes2d;

import dev.mv.engine.physics.Physics2D;

public abstract class Shape2D {

    protected int x, y;

    protected Physics2D physics;

    protected Shape2D(Physics2D physics, int x, int y) {
        this.physics = physics;
        this.x = x;
        this.y = y;
    }

    public abstract boolean isCollidingWith(Shape2D shape);

    public abstract boolean isSameType(Shape2D shape);

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
}
