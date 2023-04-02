package dev.mv.engine.physics.colliders;

import dev.mv.engine.exceptions.Exceptions;
import dev.mv.engine.physics.Collider2D;
import dev.mv.engine.physics.Physics2D;
import dev.mv.engine.physics.shapes2d.Circle;
import dev.mv.engine.physics.shapes2d.Shape2D;
import dev.mv.utils.Utils;

public class SimpleCircleCollider2D implements Collider2D {

    private static final String name = SimpleCircleCollider2D.class.getSimpleName();
    private Physics2D physics;

    public SimpleCircleCollider2D(Physics2D physics) {
        this.physics = physics;
    }

    @Override
    public boolean checkCollision(Shape2D a, Shape2D b) {
        checkType(a, b);
        float xDist = a.getX() - b.getX();
        float yDist = a.getY() - b.getY();
        return Utils.square(xDist) + Utils.square(yDist) <= Utils.square(((Circle) a).getRadius() + ((Circle) b).getRadius());
    }

    private void checkType(Shape2D a, Shape2D b) {
        if (!(a instanceof Circle && b instanceof Circle)) {
            Exceptions.send("BAD_COLLIDER", name, "non circle shapes");
        }
    }
}
