package dev.mv.engine.physics.colliders;

import dev.mv.engine.exceptions.Exceptions;
import dev.mv.engine.physics.Collider2D;
import dev.mv.engine.physics.Physics2D;
import dev.mv.engine.physics.shapes2d.Rectangle;
import dev.mv.engine.physics.shapes2d.Shape2D;

public class RectangleCollider2D implements Collider2D {

    private static final String name = RectangleCollider2D.class.getSimpleName();
    private Physics2D physics;

    public RectangleCollider2D(Physics2D physics) {
        this.physics = physics;
    }

    @Override
    public boolean checkCollision(Shape2D a, Shape2D b) {
        checkType(a, b);
        return false;
    }

    private void checkType(Shape2D a, Shape2D b) {
        if (!(a instanceof Rectangle && b instanceof Rectangle)) {
            Exceptions.send("BAD_COLLIDER", name, "non rectangular shapes");
        }
    }
}
