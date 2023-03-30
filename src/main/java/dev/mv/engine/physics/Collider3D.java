package dev.mv.engine.physics;

import dev.mv.engine.physics.shapes3d.Shape3D;

public interface Collider3D {

    boolean checkCollision(Shape3D a, Shape3D b);

}
