package dev.mv.engine.physics.colliders;

import dev.mv.engine.exceptions.Exceptions;
import dev.mv.engine.physics.Collider2D;
import dev.mv.engine.physics.Physics2D;
import dev.mv.engine.physics.shapes2d.Rectangle;
import dev.mv.engine.physics.shapes2d.Shape2D;
import dev.mv.engine.physics.shapes2d.Square;

public class SimpleRectangleCollider2D implements Collider2D {

    private static final String name = SimpleRectangleCollider2D.class.getSimpleName();
    private Physics2D physics2D;

    public SimpleRectangleCollider2D(Physics2D physics2D) {
        this.physics2D = physics2D;
    }

    @Override
    public boolean checkCollision(Shape2D a, Shape2D b) {
        checkType(a, b);
        Rectangle rectA = (Rectangle) a;
        Rectangle rectB = (Rectangle) b;
        checkRotation(rectA, rectB);
        if (check00(rectA, rectB)) return check00C(rectA, rectB);
        if (check090(rectA, rectB)) return check090C(rectA, rectB);
        if (check900(rectA, rectB)) return check900C(rectA, rectB);
        if (check9090(rectA, rectB)) return check9090C(rectA, rectB);
        return false;
    }

    private void checkType(Shape2D a, Shape2D b) {
        if (!(a instanceof Rectangle && b instanceof Rectangle)) {
            Exceptions.send("BAD_COLLIDER", name, "non rectangular shapes");
        }
    }

    private void checkRotation(Rectangle a, Rectangle b) {
        if (a.getRotation() % 90 == 0 && b.getRotation() % 90 == 0) return;
        Exceptions.send("BAD_COLLIDER", name, "rectangles with non right angle rotations!");
    }

    private boolean check00(Rectangle a, Rectangle b) {
        return (a instanceof Square || a.getRotation() == 0 || a.getRotation() == 180) && (b instanceof Square || b.getRotation() == 0 || b.getRotation() == 180);
    }

    private boolean check00C(Rectangle a, Rectangle b) {
        return check(a.getX(), a.getY(), a.getWidth(), a.getHeight(), b.getX(), b.getY(), b.getWidth(), b.getHeight());
    }

    private boolean check090(Rectangle a, Rectangle b) {
        return (a instanceof Square || a.getRotation() == 0 || a.getRotation() == 180) && (b.getRotation() == 90 || b.getRotation() == 270);
    }

    private boolean check090C(Rectangle a, Rectangle b) {
        return check(wrap(a), transform(b));
    }

    private boolean check900(Rectangle a, Rectangle b) {
        return (a.getRotation() == 90 || a.getRotation() == 270) && (b instanceof Square || b.getRotation() == 0 || b.getRotation() == 180);
    }

    private boolean check900C(Rectangle a, Rectangle b) {
        return check(transform(a), wrap(b));
    }

    private boolean check9090(Rectangle a, Rectangle b) {
        return (a.getRotation() == 90 || a.getRotation() == 270) && (b.getRotation() == 90 || b.getRotation() == 270);
    }

    private boolean check9090C(Rectangle a, Rectangle b) {
        return check(transform(a), transform(b));
    }

    private boolean check(float ax, float ay, float aw, float ah, float bx, float by, float bw, float bh) {
        return ax < bx + bw &&
            ax + aw > bx &&
            ay < by + bh &&
            ay + ah > by;
    }

    private float[] transform(Rectangle r) {
        return new float[]{
            r.getCenterX() - r.getHeight() / 2,
            r.getCenterY() - r.getWidth() / 2,
            r.getHeight(),
            r.getWidth()
        };
    }

    private float[] wrap(Rectangle r) {
        return new float[]{
            r.getX(),
            r.getY(),
            r.getWidth(),
            r.getHeight()
        };
    }

    private boolean check(float[] a, float[] b) {
        return a[0] < b[0] + b[2] &&
            a[0] + a[2] > b[0] &&
            a[1] < b[1] + b[3] &&
            a[1] + a[3] > b[1];
    }

}
