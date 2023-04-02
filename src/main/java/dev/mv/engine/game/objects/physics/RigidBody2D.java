package dev.mv.engine.game.objects.physics;

import dev.mv.engine.physics.shapes2d.Shape2D;

public interface RigidBody2D extends PhysicsActor {

    Shape2D getHitbox();

    default Shape2D.BoundingBox2D getBoundingBox() {
        return getHitbox().getBoundingBox();
    }

}
