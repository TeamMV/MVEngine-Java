package dev.mv.engine.physics.colliders;

import dev.mv.engine.exceptions.Exceptions;
import dev.mv.engine.physics.Collider2D;
import dev.mv.engine.physics.Physics2D;
import dev.mv.engine.physics.shapes2d.Oval;
import dev.mv.engine.physics.shapes2d.Circle;
import dev.mv.engine.physics.shapes2d.Shape2D;

public class SimpleOvalCollider2D implements Collider2D {

    private static final String name = SimpleOvalCollider2D.class.getSimpleName();

    private Physics2D physics;
    private float[] innerPolygonCoef, outerPolygonCoef;
    private final int maxIterations = 10;

    public SimpleOvalCollider2D(Physics2D physics) {
        this.physics = physics;
        innerPolygonCoef = new float[maxIterations+1];
        outerPolygonCoef = new float[maxIterations+1];
        float PI_2 = (float) (Math.PI / 2);
        for (int t = 0; t <= maxIterations; t++) {
            int numNodes = 4 << t;
            innerPolygonCoef[t] = (float) (0.5 / Math.cos(Math.PI / numNodes));
            outerPolygonCoef[t] = (float) (0.5 / (Math.cos(PI_2 / numNodes) * Math.cos(PI_2 / numNodes)));
        }
    }

    @Override
    public boolean checkCollision(Shape2D a, Shape2D b) {
        checkType(a, b);
        if (a instanceof Circle c) {
            return checkOvalCircle((Oval) b, c);
        }
        else if (b instanceof Circle c) {
            return checkOvalCircle((Oval) a, c);
        }
        return checkOvals((Oval) a, (Oval) b);
    }

    private void checkType(Shape2D a, Shape2D b) {
        if (!(a instanceof Oval && b instanceof Oval)) {
            Exceptions.send("BAD_COLLIDER", name, "non oval shapes");
        }
    }

    private boolean checkOvals(Oval a, Oval b) {
        if (a.getRotation() % 90 == 0 && b.getRotation() % 90 == 0) {
            float aRa = a.getRadiusA();
            float aRb = a.getRadiusB();
            float bRa = b.getRadiusA();
            float bRb = b.getRadiusB();
            if (a.getRotation() % 180 == 90) {
                aRa = a.getRadiusB();
                aRb = a.getRadiusA();
            }
            if (b.getRotation() % 180 == 90) {
                bRa = b.getRadiusB();
                bRb = b.getRadiusA();
            }
            return collide(a.getX(), a.getY(), aRa, aRb, b.getX(), b.getY(), bRa, bRb);
        }
        return collide(a.getX(), a.getY(), a.getRadiusVec().x, a.getRadiusVec().y, a.getRadiusRatio(), b.getX(), b.getY(), b.getRadiusVec().x, b.getRadiusVec().y, b.getRadiusRatio());
    }

    private boolean checkOvalCircle(Oval a, Circle b) {
        if (a.getRotation() % 180 == 0) {
            return collide(a.getX(), a.getY(), a.getRadiusA(), a.getRadiusB(), b.getX(), b.getY(), b.getRadius());
        }
        else if (a.getRotation() % 180 == 90) {
            return collide(a.getX(), a.getY(), a.getRadiusB(), a.getRadiusA(), b.getX(), b.getY(), b.getRadius());
        }
        else {
            return checkOvals(a, b);
        }
    }



    private boolean iterate(float x, float y, float c0x, float c0y, float c2x, float c2y, float rr) {
        for (int t = 1; t <= maxIterations; t++) {
            float c1x = (c0x + c2x)*innerPolygonCoef[t];
            float c1y = (c0y + c2y)*innerPolygonCoef[t];
            float tx = x - c1x;
            float ty = y - c1y;
            if (tx*tx + ty*ty <= rr) {
                return true;
            }
            float t2x = c2x - c1x;
            float t2y = c2y - c1y;
            if (tx*t2x + ty*t2y >= 0 && tx*t2x + ty*t2y <= t2x*t2x + t2y*t2y &&
                (ty*t2x - tx*t2y >= 0 || rr*(t2x*t2x + t2y*t2y) >= (ty*t2x - tx*t2y)*(ty*t2x - tx*t2y))) {
                return true;
            }
            float t0x = c0x - c1x;
            float t0y = c0y - c1y;
            if (tx*t0x + ty*t0y >= 0 && tx*t0x + ty*t0y <= t0x*t0x + t0y*t0y &&
                (ty*t0x - tx*t0y <= 0 || rr*(t0x*t0x + t0y*t0y) >= (ty*t0x - tx*t0y)*(ty*t0x - tx*t0y))) {
                return true;
            }
            float c3x = (c0x + c1x)*outerPolygonCoef[t];
            float c3y = (c0y + c1y)*outerPolygonCoef[t];
            if ((c3x-x)*(c3x-x) + (c3y-y)*(c3y-y) < rr) {
                c2x = c1x;
                c2y = c1y;
                continue;
            }
            float c4x = c1x - c3x + c1x;
            float c4y = c1y - c3y + c1y;
            if ((c4x-x)*(c4x-x) + (c4y-y)*(c4y-y) < rr) {
                c0x = c1x;
                c0y = c1y;
                continue;
            }
            float t3x = c3x - c1x;
            float t3y = c3y - c1y;
            if (ty*t3x - tx*t3y <= 0 || rr*(t3x*t3x + t3y*t3y) > (ty*t3x - tx*t3y)*(ty*t3x - tx*t3y)) {
                if (tx*t3x + ty*t3y > 0) {
                    if (Math.abs(tx*t3x + ty*t3y) <= t3x*t3x + t3y*t3y || (x-c3x)*(c0x-c3x) + (y-c3y)*(c0y-c3y) >= 0) {
                        c2x = c1x;
                        c2y = c1y;
                        continue;
                    }
                } else if (-(tx*t3x + ty*t3y) <= t3x*t3x + t3y*t3y || (x-c4x)*(c2x-c4x) + (y-c4y)*(c2y-c4y) >= 0) {
                    c0x = c1x;
                    c0y = c1y;
                    continue;
                }
            }
            return false;
        }
        return false;
    }

    private boolean collide(float x0, float y0, float wx0, float wy0, float hw0, float x1, float y1, float wx1, float wy1, float hw1) {
        float rr = hw1*hw1*(wx1*wx1 + wy1*wy1)*(wx1*wx1 + wy1*wy1)*(wx1*wx1 + wy1*wy1);
        float tmpX = wy1*(y1 - y0) + wx1*(x1 - x0);
        float tmpY  = wx1*(y1 - y0) - wy1*(x1 - x0);
        float x = hw1*wx1*tmpX - wy1*tmpY;
        float y = hw1*wy1*tmpX + wx1*tmpY;
        float temp = wx0;
        wx0 = hw1*wx1*(wy1*wy0 + wx1*wx0) - wy1*(wx1*wy0 - wy1*wx0);
        float temp2 = wy0;
        wy0 = hw1*wy1*(wy1*wy0 + wx1*temp) + wx1*(wx1*wy0 - wy1*temp);
        tmpX = wy1*(temp*hw0)-wx1*temp2*hw0;
        tmpY = wx1*(temp*hw0)+wy1*temp2*hw0;
        float hx0 = hw1*wx1*tmpX-wy1*tmpY;
        float hy0 = hw1*wy1*tmpX+wx1*tmpY;

        if (wx0*y - wy0*x < 0) {
            x = -x;
            y = -y;
        }

        if ((wx0 - x)*(wx0 - x) + (wy0 - y)*(wy0 - y) <= rr) {
            return true;
        } else if ((wx0 + x)*(wx0 + x) + (wy0 + y)*(wy0 + y) <= rr) {
            return true;
        } else if ((hx0 - x)*(hx0 - x) + (hy0 - y)*(hy0 - y) <= rr) {
            return true;
        } else if ((hx0 + x)*(hx0 + x) + (hy0 + y)*(hy0 + y) <= rr) {
            return true;
        }

        float xwh = x*(hy0 - wy0) + y*(wx0 - hx0);
        float ywh = x*(wx0-hx0) - y*(hy0-wy0);
        float v2 = xwh - hy0*wx0 + hx0*wy0;
        float v3 = x*(wx0+hx0) + y*(wy0+hy0);

        if (xwh <= hy0*wx0 - hx0*wy0 &&
            y*(wx0 + hx0) - x*(wy0 + hy0) <= hy0*wx0 - hx0*wy0) {
            return true;
        } else if (ywh > hx0*(wx0-hx0) - hy0*(hy0-wy0)
            && ywh < wx0*(wx0-hx0) - wy0*(hy0-wy0)
            && v2 * v2
            <= rr*((wx0-hx0)*(wx0-hx0) + (wy0-hy0)*(wy0-hy0))) {
            return true;
        } else {
            float wvh = y * (wx0 + hx0) - x * (wy0 + hy0) - hy0 * wx0 + hx0 * wy0;
            if (v3 > -wx0*(wx0+hx0) - wy0*(wy0+hy0)
                && v3 < hx0*(wx0+hx0) + hy0*(wy0+hy0)
                && wvh * wvh
                <= rr*((wx0+hx0)*(wx0+hx0) + (wy0+hy0)*(wy0+hy0))) {
                return true;
            } else {
                if ((hx0-wx0 - x)*(hx0-wx0 - x) + (hy0-wy0 - y)*(hy0-wy0 - y) <= rr) {
                    return iterate(x, y, hx0, hy0, -wx0, -wy0, rr);
                } else if ((hx0+wx0 - x)*(hx0+wx0 - x) + (hy0+wy0 - y)*(hy0+wy0 - y) <= rr) {
                    return iterate(x, y, wx0, wy0, hx0, hy0, rr);
                } else if ((wx0-hx0 - x)*(wx0-hx0 - x) + (wy0-hy0 - y)*(wy0-hy0 - y) <= rr) {
                    return iterate(x, y, -hx0, -hy0, wx0, wy0, rr);
                } else if ((-wx0-hx0 - x)*(-wx0-hx0 - x) + (-wy0-hy0 - y)*(-wy0-hy0 - y) <= rr) {
                    return iterate(x, y, -wx0, -wy0, -hx0, -hy0, rr);
                } else if (wx0*y - wy0*x < wx0*hy0 - wy0*hx0 && Math.abs(hx0*y - hy0*x) < hy0*wx0 - hx0*wy0) {
                    if (hx0*y - hy0*x > 0) {
                        return iterate(x, y, hx0, hy0, -wx0, -wy0, rr);
                    }
                    return iterate(x, y, wx0, wy0, hx0, hy0, rr);
                } else {
                    float v = wx0 * y - wy0 * x - hy0 * wx0 + hx0 * wy0;
                    if (wx0*x + wy0*y > wx0*(hx0-wx0) + wy0*(hy0-wy0) && wx0*x + wy0*y < wx0*(hx0+wx0) + wy0*(hy0+wy0)
                        && v * v < rr*(wx0*wx0 + wy0*wy0)) {
                        if (wx0*x + wy0*y > wx0*hx0 + wy0*hy0) {
                            return iterate(x, y, wx0, wy0, hx0, hy0, rr);
                        }
                        return iterate(x, y, hx0, hy0, -wx0, -wy0, rr);
                    } else {
                        if (hx0*y - hy0*x < 0) {
                            x = -x;
                            y = -y;
                        }
                        float v1 = hx0 * y - hy0 * x - hy0 * wx0 + hx0 * wy0;
                        if (hx0*x + hy0*y > -hx0*(wx0+hx0) - hy0*(wy0+hy0) && hx0*x + hy0*y < hx0*(hx0-wx0) + hy0*(hy0-wy0)
                            && v1 * v1 < rr*(hx0*hx0 + hy0*hy0)) {
                            if (hx0*x + hy0*y > -hx0*wx0 - hy0*wy0) {
                                return iterate(x, y, hx0, hy0, -wx0, -wy0, rr);
                            }
                            return iterate(x, y, -wx0, -wy0, -hx0, -hy0, rr);
                        }
                        return false;
                    }
                }
            }
        }
    }

    private boolean collide(float x0, float y0, float w0, float h0, float x1, float y1, float w1, float h1) {
        float x = Math.abs(x1 - x0)*h1;
        float y = Math.abs(y1 - y0)*w1;
        w0 *= h1;
        h0 *= w1;
        float r = w1*h1;

        return b(w0, h0, r, x, y);
    }

    private boolean a(float w0, float h0, float x, float y, float r) {
        if ((x-w0)*(x-w0) + (y-h0)*(y-h0) <= r*r || (x <= w0 && y - r <= h0) || (y <= h0 && x - r <= w0)) {
            return iterate(x, y, w0, 0, 0, h0, r*r);
        }
        return false;
    }

    private boolean collide(float x0, float y0, float w, float h, float x1, float y1, float r) {
        float x = Math.abs(x1 - x0);
        float y = Math.abs(y1 - y0);

        return b(w, h, r, x, y);
    }

    private boolean b(float w, float h, float r, float x, float y) {
        float v = x * h + y * w - w * h;
        if (x*x + (h - y)*(h - y) <= r*r || (w - x)*(w - x) + y*y <= r*r || x*h + y*w <= w*h
            || (v * v <= r*r*(w*w + h*h) && x*w - y*h >= -h*h && x*w - y*h <= w*w)) {
            return true;
        } else {
            return a(w, h, x, y, r);
        }
    }
}
