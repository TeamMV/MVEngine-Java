package dev.mv.engine.physics.shapes2d;

import dev.mv.engine.MVEngine;
import org.joml.Vector2i;

public class Oval extends Shape2D {

    private Vector2i focA, focB;
    private int c;

    Oval(Vector2i centre, int c) {
        super(MVEngine.instance().getPhysics2D(), centre.x, centre.y);
        this.focA = centre;
        this.focB = centre;
        this.c = c;
    }

    public Oval(Vector2i focA, Vector2i focB, int c) {
        super(MVEngine.instance().getPhysics2D(), (focA.x + focB.x) / 2, (focA.y + focB.y) / 2);
        this.focA = focA;
        this.focB = focB;
        this.c = c;
    }

    @Override
    public boolean isCollidingWith(Shape2D shape) {
        return physics.getCollider(this, shape).checkCollision(this, shape);
    }

    @Override
    public boolean isSameType(Shape2D shape) {
        return shape instanceof Oval || shape instanceof Circle;
    }
}
