package dev.mv.engine.physics.shapes2d;

import dev.mv.engine.physics.Physics2D;

public abstract class Shape2D {

    protected Physics2D physics;

    protected Shape2D(Physics2D physics) {
        this.physics = physics;
    }

    public abstract boolean isCollidingWith(Shape2D shape);

    public boolean isSameType(Shape2D shape) {
        return this.getClass().isAssignableFrom(shape.getClass()) || shape.getClass().isAssignableFrom(this.getClass());
    }
}
