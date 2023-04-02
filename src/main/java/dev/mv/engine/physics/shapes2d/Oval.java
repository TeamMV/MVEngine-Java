package dev.mv.engine.physics.shapes2d;

import dev.mv.engine.MVEngine;
import dev.mv.utils.Utils;
import org.joml.Vector2f;

public class Oval extends Shape2D {

    protected Vector2f focA, focB;
    protected float a, b, c;
    protected Vector2f rVec;
    protected float rRatio;

    Oval(Vector2f centre, float rad) {
        super(MVEngine.instance().getPhysics2D(), centre);
        this.focA = centre;
        this.focB = centre;
        this.c = rad * 2;
        a = rad;
        b = rad;
        rVec = new Vector2f(rad, 0);
        rRatio = 1;
    }

    public Oval(Vector2f focA, Vector2f focB, float c) {
        super(MVEngine.instance().getPhysics2D(), (focA.x + focB.x) / 2, (focA.y + focB.y) / 2);
        this.focA = focA;
        this.focB = focB;
        this.c = c;
        calculate();
    }

    private void calculate() {
        float distX = focA.x - center.x;
        float distY = focA.y - center.y;
        float dist = Utils.square(distX) + Utils.square(distY);
        a = c / 2;
        b = (float) Math.sqrt(Utils.square(a) - dist);
        setRotation((float) Math.toDegrees(Math.atan((double) distY / distX)));
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
        boundingBox.w = center.x + size;
        boundingBox.h = center.y + size;
    }

    @Override
    public void scale(float factor) {
        a *= factor;
        b *= factor;
        rVec.mul(factor);
        updateBoundingBox();
    }


    public Vector2f getFocusA() {
        return focA;
    }

    public void setFocusA(Vector2f focA) {
        this.focA = focA;
        calculate();
        updateBoundingBox();
    }

    public Vector2f getFocusB() {
        return focB;
    }

    public void setFocusB(Vector2f focB) {
        this.focB = focB;
        calculate();
        updateBoundingBox();
    }

    public float getConstant() {
        return c;
    }

    public void setConstant(float c) {
        this.c = c;
        calculate();
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
