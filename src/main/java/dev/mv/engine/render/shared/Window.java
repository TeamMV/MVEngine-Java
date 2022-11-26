package dev.mv.engine.render.shared;

import org.joml.Matrix4f;

import java.awt.image.BufferedImage;

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

    RenderingContext getRenderingContext();
}
