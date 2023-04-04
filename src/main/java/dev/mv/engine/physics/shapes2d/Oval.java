package dev.mv.engine.physics.shapes2d;

import dev.mv.engine.MVEngine;
import dev.mv.utils.Utils;
import org.joml.Vector2f;

public class Oval extends Shape2D {

    protected float a, b;
    protected Vector2f rVec;
    protected float rRatio;

    Oval(Vector2f centre, float rad) {
        super(MVEngine.instance().getPhysics2D(), centre);
        x = centre.x;
        y = centre.y;
        a = rad;
        b = rad;
        rVec = new Vector2f(rad, 0);
        rRatio = 1;
    }

    public Oval(Vector2f focA, Vector2f focB, float c) {
        super(MVEngine.instance().getPhysics2D(), (focA.x + focB.x) / 2, (focA.y + focB.y) / 2);
        x = center.x;
        y = center.y;
        calculate(focA, focB, c);
    }

    public Oval(float x, float y, float radA, float radB) {
        super(MVEngine.instance().getPhysics2D(), x, y);
        this.x = x;
        this.y = y;
        a = radA;
        b = radB;
        rVec = new Vector2f(radA, 0);
        rRatio = b / a;
    }

    public Oval(float x, float y, float radA, float radB, float rotation) {
        super(MVEngine.instance().getPhysics2D(), x, y);
        this.x = x;
        this.y = y;
        a = radA;
        b = radB;
        super.setRotation(rotation);
        rVec = new Vector2f((float) (a * Math.cos(this.rotation)), (float) (a * Math.sin(this.rotation)));
        rRatio = b / a;
    }

    private void calculate(Vector2f focA, Vector2f focB, float c) {
        float distX = focA.x - center.x;
        float distY = focA.y - center.y;
        float dist = Utils.square(distX) + Utils.square(distY);
        a = c / 2;
        b = (float) Math.sqrt(Utils.square(a) - dist);
        super.setRotation((float) Math.toDegrees(Math.atan((double) distY / distX)));
        rVec = new Vector2f((float) (a * Math.cos(rotation)), (float) (a * Math.sin(rotation)));
        rRatio = b / a;
    }

    @Override
    public boolean isCollidingWith(Shape2D shape) {
        return physics.getCollider(this, shape).checkCollision(this, shape);
    }

    @Override
    public boolean equalsType(Shape2D shape) {
        return shape instanceof Oval;
    }

    @Override
    public void updateBoundingBox() {
        float size = Math.max(a, b);
        boundingBox.x = center.x - size;
        boundingBox.y = center.y - size;
        boundingBox.s = size + size;
    }

    @Override
    public void scale(float factor) {
        a *= factor;
        b *= factor;
        rVec.mul(factor);
        updateBoundingBox();
    }

    public float getRadiusA() {
        return a;
    }

    public float getRadiusB() {
        return b;
    }

    public float getRadiusVecX() {
        return rVec.x;
    }

    public float getRadiusVecY() {
        return rVec.y;
    }

    public float getRadiusRatio() {
        return rRatio;
    }

    @Override
    public void setRotation(float rotation) {
        super.setRotation(rotation);
        rVec.x = (float) (a * Math.cos(rotation));
        rVec.y = (float) (a * Math.sin(rotation));
    }
}
