package dev.mv.engine.oldRender.camera;

import dev.mv.engine.oldRender.window.Window;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector4f;

public class Camera {
    public Vector2f position;
    public boolean isStatic;
    public float zoom = 1.0f;
    public float moveSpeed = 1.0f;
    private Matrix4f projectionMatrix, viewMatrix, zoomMatrix;
    private Window window;

    public Camera(Vector2f position, Window window) {
        this.position = position;
        this.window = window;
        isStatic = false;

        projectionMatrix = new Matrix4f();
        viewMatrix = new Matrix4f();
        zoomMatrix = new Matrix4f();

        declareProjection();
    }

    public Camera(Vector2f position, Window window, boolean isStatic) {
        this.position = position;
        this.window = window;
        this.isStatic = isStatic;

        projectionMatrix = new Matrix4f();
        viewMatrix = new Matrix4f();
        zoomMatrix = new Matrix4f();

        declareProjection();
    }

    public void declareProjection() {
        float[] mat = new float[16];
        projectionMatrix.identity();
        projectionMatrix.ortho(0.0f, (float) window.getWidth(), 0.0f, (float) window.getHeight(), 0.0f, 100.0f);
        Vector4f vec = new Vector4f(750, 0, 0, 1);
    }

    public void updateProjection() {
        projectionMatrix.identity();
        projectionMatrix.ortho2DLH(0.0f, (float) window.getWidth(), 0.0f, (float) window.getHeight());
    }

    public Matrix4f getViewMatrix() {
        //Vector3f cameraFront = new Vector3f(0.0f, 0.0f, -1.0f);
        //Vector3f cameraUp = new Vector3f(0.0f, 1.0f, 0.0f);
        viewMatrix.identity();
        //viewMatrix.lookAt(new Vector3f(position.x, position.y, 20.0f), cameraFront.add(position.x, position.y, 0.0f), cameraUp);
        return viewMatrix.mul(getZoomMatrix());
    }

    public Matrix4f getProjectionMatrix() {
        return projectionMatrix;
    }

    public Matrix4f getZoomMatrix() {
        zoomMatrix.set(zoom, 0, 0, 0, 0, zoom, 0, 0, 0, 0, zoom, 0, 0, 0, 0, 1);

        return zoomMatrix;
    }
}
