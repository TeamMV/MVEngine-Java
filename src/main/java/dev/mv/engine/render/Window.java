package dev.mv.engine.render;

import org.joml.Matrix4f;

import java.util.concurrent.Executor;

public interface Window {
    void run();
    void run(Runnable onStart, Runnable onUpdate, Runnable onDraw);

    void setFPSCap(int cap);
    void setUPSCap(int cap);
    int getWidth();
    int getHeight();
    int getFPS();
    int getUPS();
    int getFPSCap();
    int getUPSCap();
    long getGlfwId();

    Matrix4f getProjectionMatrix2D();
    Matrix4f getProjectionMatrix3D();

    String getName();

    DrawContext2D getDrawContext2D();
    DrawContext3D getDrawContext3D();
}
