package dev.mv.engine.render.shared;

import imgui.gl3.ImGuiImplGl3;
import imgui.glfw.ImGuiImplGlfw;
import org.joml.Matrix4f;

public interface Window {
    void run();

    void run(Runnable onStart, Runnable onUpdate, Runnable onDraw);

    void stop();

    int getWidth();

    int getHeight();

    int getFPS();

    int getUPS();

    int getFPSCap();

    void setFPSCap(int cap);

    int getUPSCap();

    void setUPSCap(int cap);

    long getCurrentFrame();

    long getGlfwId();

    boolean isFullscreen();

    void setFullscreen(boolean fullscreen);

    Matrix4f getProjectionMatrix2D();

    Matrix4f getProjectionMatrix3D();

    String getTitle();

    void setTitle(String title);

    Render2D getRender2D();

    Render3D getRender3D();

    Camera getCamera();

    ImGuiImplGl3 getImGuiGlImpl();

    ImGuiImplGlfw getImGuiGlfwImpl();
}
