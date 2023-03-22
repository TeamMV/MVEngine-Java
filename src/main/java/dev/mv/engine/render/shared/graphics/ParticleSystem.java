package dev.mv.engine.render.shared.graphics;

import dev.mv.engine.render.shared.DrawContext2D;
import dev.mv.utils.collection.Vec;

public abstract class ParticleSystem {
    public enum Shape{
        CIRCLE,
        SQUARE,
        TRIANGLE
    }

    private int maxNum = 1;
    private Shape shape;
    private Vec<Particle> particles;

    public ParticleSystem(int maxNum, Shape shape) {
        this.maxNum = maxNum;
        this.shape = shape;
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
}
