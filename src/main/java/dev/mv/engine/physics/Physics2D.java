package dev.mv.engine.physics;

import dev.mv.engine.exceptions.Exceptions;
import dev.mv.engine.physics.colliders.*;
import dev.mv.engine.physics.shapes2d.Circle;
import dev.mv.engine.physics.shapes2d.Oval;
import dev.mv.engine.physics.shapes2d.Rectangle;
import dev.mv.engine.physics.shapes2d.Shape2D;

public class Physics2D {

    private static Physics2D instance = null;

    private final Collider2D aabb;
    private final Collider2D rect;
    private final Collider2D simpleCircle;
    private final Collider2D circle;
    private final Collider2D simpleOval;
    private final Collider2D oval;
    private final Collider2D polygon;

    private Physics2D() {
        aabb = new SimpleRectangleCollider2D(this);
        rect = new RectangleCollider2D(this);
        simpleCircle = new SimpleCircleCollider2D(this);
        circle = new CircleCollider2D(this);
        simpleOval = new SimpleOvalCollider2D(this);
        oval = new OvalCollider2D(this);
        polygon = new PolygonCollider2D(this);
    }

    public static Physics2D init() {
        if (instance != null) Exceptions.send(new IllegalStateException("Physics2D already initialized"));
        instance = new Physics2D();
        return instance;
    }

    public void terminate() {
        instance = null;
    }

    public Collider2D getCollider(Shape2D a, Shape2D b) {
        if (a.isSameType(b)) {
            if (a instanceof Rectangle) {
                if (a.getRotation() % 90 == 0 && a.getRotation() % 90 == 0) {
                    return aabb;
                }
                else {
                    return rect;
                }
            }
            else if (a instanceof Circle) {
                return simpleCircle;
            }
            else if (a instanceof Oval) {
                return simpleOval;
            }
        }
        if (a instanceof Circle || b instanceof Circle) {
            return circle;
        }
        if (a instanceof Oval || b instanceof Oval) {
            return oval;
        }
        return null;
    }

}
