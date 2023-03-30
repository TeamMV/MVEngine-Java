package dev.mv.engine.physics;

import dev.mv.engine.physics.shapes2d.Shape2D;

public interface Collider2D {

    boolean checkCollision(Shape2D a, Shape2D b);

}
