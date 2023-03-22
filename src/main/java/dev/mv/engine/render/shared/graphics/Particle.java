package dev.mv.engine.render.shared.graphics;

import dev.mv.engine.render.shared.DrawContext2D;

public class Particle {
    private ParticleSystem.Shape shape;
    private float direction;
    private float rotation;
    private float scale;
    private float xVel, yVel;
    private float speed;

    public ParticleSystem.Shape getShape() {
        return shape;
    }

    void setShape(ParticleSystem.Shape shape) {
        this.shape = shape;
    }

    float getDirection() {
        return direction;
    }

    void setDirection(float direction) {
        this.direction = direction;
    }

    float getRotation() {
        return rotation;
    }

    void setRotation(float rotation) {
        this.rotation = rotation;
    }

    float getScale() {
        return scale;
    }

    void setScale(float scale) {
        this.scale = scale;
    }

    float getxVel() {
        return xVel;
    }

    void setxVel(float xVel) {
        this.xVel = xVel;
    }

    float getyVel() {
        return yVel;
    }

    void setyVel(float yVel) {
        this.yVel = yVel;
    }

    float getSpeed() {
        return speed;
    }

    void setSpeed(float speed) {
        this.speed = speed;
    }

    void draw(DrawContext2D ctx2D) {

    }
}
