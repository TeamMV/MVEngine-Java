package dev.mv.engine.render.shared.graphics;

import dev.mv.engine.render.shared.Color;
import dev.mv.engine.render.shared.DrawContext2D;

public class Particle {
    private ParticleSystem.Shape shape;
    private float direction;
    private float rotation;
    private float scale;
    private float xVel, yVel;
    private float speed;
    private int x, y;
    private Color color;

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

    int getX() {
        return x;
    }

    void setX(int x) {
        this.x = x;
    }

    int getY() {
        return y;
    }

    void setY(int y) {
        this.y = y;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    void update() {
        x += xVel;
        y += yVel;

        xVel -= xVel / speed;
        yVel -= yVel / speed;

        rotation += speed * 3;

        scale -= speed / 3;

        color.setAlpha((int) (color.getAlpha() - speed * 2));
        if(isDone()) System.out.println(x);
    }

    boolean isDone() {
        return color.getAlpha() <= 0;
    }

    void draw(DrawContext2D ctx2D) {
        ctx2D.color(color);
        if(shape == ParticleSystem.Shape.CIRCLE) {
            ctx2D.circle(x, y, (int) scale, 5f);
        }
        if(shape == ParticleSystem.Shape.SQUARE) {
            ctx2D.rectangle((int) (x - scale / 2f), (int) (y - scale / 2f), (int) scale, (int) scale, rotation);
        }
        if(shape == ParticleSystem.Shape.TRIANGLE) {
            ctx2D.triangle(x, y, (int) (x + scale), y, (int) (x + scale / 2), (int) (y + scale), rotation);
        }
    }
}
