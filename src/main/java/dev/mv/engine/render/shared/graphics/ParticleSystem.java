package dev.mv.engine.render.shared.graphics;

import dev.mv.engine.render.shared.DrawContext2D;
import dev.mv.utils.collection.Vec;

public abstract class ParticleSystem {
    protected int maxNum = 1;
    protected Shape shape;
    protected Vec<Particle> particles;
    protected int x, y;
    protected float speed = 1;
    public ParticleSystem(int x, int y, int maxNum, Shape shape) {
        this.maxNum = maxNum;
        this.shape = shape;
        this.x = x;
        this.y = y;
        particles = new Vec<Particle>(maxNum);
    }

    public int getMaxNum() {
        return maxNum;
    }

    public void setMaxNum(int maxNum) {
        this.maxNum = maxNum;
    }

    public Shape getShape() {
        return shape;
    }

    public void setShape(Shape shape) {
        this.shape = shape;
    }

    public abstract void draw(DrawContext2D ctx2D);

    public float getSpeed() {
        return speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public enum Shape {
        CIRCLE,
        SQUARE,
        TRIANGLE
    }
}
