package dev.mv.engine.render.opengl._3d.camera;

import org.joml.Math;
import org.joml.Vector3f;
import org.joml.Vector3i;

public class OpenGLCamera3D {
    private Vector3f location;
    private Vector3f rotation;

    public OpenGLCamera3D() {
        location = new Vector3f(0, 0, -1);
        rotation = new Vector3f(0, 0, 0);
    }

    public OpenGLCamera3D(Vector3f location, Vector3f direction) {
        this.location = location;
        this.rotation = direction;
    }

    public void move(float x, float y, float z) {
        if(z != 0) {
            location.x += (float) Math.sin(Math.toRadians(rotation.y)) * -1.0f * z;
            location.z += (float) Math.cos(Math.toRadians(rotation.y)) * z;
        }
        if(x != 0) {
            location.x += (float) Math.sin(Math.toRadians(rotation.y - 90)) * -1.0f * x;
            location.z += (float) Math.cos(Math.toRadians(rotation.y - 90)) * x;
        }
        location.y += y;
    }

    public void moveTo(float x, float y, float z) {
        location.x = x;
        location.y = y;
        location.z = z;
    }

    public void rotate(float x, float y, float z) {
        rotation.x += x;
        rotation.y += y;
        rotation.z += z;
    }

    public void rotateTo(float x, float y, float z) {
        rotation.x = x;
        rotation.y = y;
        rotation.z = z;
    }

    public Vector3f getLocation() {
        return location;
    }

    public Vector3f getRotation() {
        return rotation;
    }
}
