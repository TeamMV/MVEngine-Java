package dev.mv.engine.render;

import org.joml.Matrix4f;

public interface Window {
    void run();

    void run(Runnable onStart, Runnable onUpdate, Runnable onDraw);

    int getWidth();

    int getHeight();

    int getFPS();

    int getUPS();

    int getFPSCap();

    void setFPSCap(int cap);

    int getUPSCap();

    void setUPSCap(int cap);

    long getGlfwId();

    Matrix4f getProjectionMatrix2D();

    Matrix4f getProjectionMatrix3D();

    String getTitle();

    DrawContext2D getDrawContext2D();

    DrawContext3D getDrawContext3D();
}
