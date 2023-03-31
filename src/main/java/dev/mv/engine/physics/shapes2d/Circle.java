package dev.mv.engine.physics.shapes2d;

import dev.mv.engine.MVEngine;
import org.joml.Vector2i;

public class Circle extends Oval {

    private int radius;

    public Circle(int x, int y, int radius) {
        super(new Vector2i(x, y), radius / 2);
        this.radius = radius;
    }

    @Override
    public boolean isCollidingWith(Shape2D shape) {
        return physics.getCollider(this, shape).checkCollision(this, shape);
    }

    @Override
    public boolean isSameType(Shape2D shape) {
        return shape instanceof Circle;
    }

    public int getRadius() {
        return radius;
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }
}
