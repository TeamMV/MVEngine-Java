package dev.mv.engine.physics;

import dev.mv.engine.exceptions.Exceptions;
import dev.mv.engine.physics.colliders.AABBCollider2D;
import dev.mv.engine.physics.colliders.RectangleCollider2D;
import dev.mv.engine.physics.shapes2d.Rectangle;
import dev.mv.engine.physics.shapes2d.Shape2D;

public class Physics2D {

    private static Physics2D instance = null;

    private final Collider2D aabb;
    private final Collider2D rect;

    private Physics2D() {
        aabb = new AABBCollider2D(this);
        rect = new RectangleCollider2D(this);
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
                if (((Rectangle) a).getRotation() % 90 == 0 && ((Rectangle) a).getRotation() % 90 == 0) {
                    return aabb;
                }
                else {
                    return rect;
                }
            }
        }
        return null;
    }

}
