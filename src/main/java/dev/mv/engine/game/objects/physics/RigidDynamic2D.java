package dev.mv.engine.game.objects.physics;

public interface RigidDynamic2D extends RigidBody2D {

    boolean hasGravity();

    void setGravity(boolean gravity);

    float getGravityScale();

    void setGravityScale(float gravity);

}
