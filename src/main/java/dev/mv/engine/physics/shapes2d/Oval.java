package dev.mv.engine.physics.shapes2d;

import dev.mv.engine.MVEngine;
import dev.mv.utils.Utils;
import org.joml.Vector2f;
import org.joml.Vector2i;

public class Oval extends Shape2D {

    protected Vector2i focA, focB;
    protected int a, b, c;
    protected Vector2f rVec;
    protected float rRatio;

    Oval(Vector2i centre, int rad) {
        super(MVEngine.instance().getPhysics2D(), centre);
        this.focA = centre;
        this.focB = centre;
        this.c = rad * 2;
        a = rad;
        b = rad;
        rVec = new Vector2f(rad, 0);
        rRatio = 1;
    }

    public Oval(Vector2i focA, Vector2i focB, int c) {
        super(MVEngine.instance().getPhysics2D(), (focA.x + focB.x) / 2, (focA.y + focB.y) / 2);
        this.focA = focA;
        this.focB = focB;
        this.c = c;
        calculate();
    }

    private void calculate() {
        int distX = focA.x - center.x;
        int distY = focA.y - center.y;
        int dist = Utils.square(distX) + Utils.square(distY);
        a = c / 2;
        b = (int) Math.sqrt(Utils.square(a) - dist);
        setRotation((float) Math.toDegrees(Math.atan((double) distY / distX)));
        rVec = new Vector2f((float) (a * Math.cos(rotation)), (float) (a * Math.sin(rotation)));
        rRatio = (float) b / a;
    }

    @Override
    public boolean isCollidingWith(Shape2D shape) {
        return physics.getCollider(this, shape).checkCollision(this, shape);
    }

    @Override
    public boolean isSameType(Shape2D shape) {
        return shape instanceof Oval;
    }

    @Override
    protected void recalculateVertices() {}


    public Vector2i getFocusA() {
        return focA;
    }

    public void setFocusA(Vector2i focA) {
        this.focA = focA;
        calculate();
    }

    public Vector2i getFocusB() {
        return focB;
    }

    public void setFocusB(Vector2i focB) {
        this.focB = focB;
        calculate();
    }

    public int getConstant() {
        return c;
    }

    public void setConstant(int c) {
        this.c = c;
        calculate();
    }

    public int getRadiusA() {
        return a;
    }

    public int getRadiusB() {
        return b;
    }

    public Vector2f getRadiusVec() {
        return rVec;
    }

    public float getRadiusRatio() {
        return rRatio;
    }
}
