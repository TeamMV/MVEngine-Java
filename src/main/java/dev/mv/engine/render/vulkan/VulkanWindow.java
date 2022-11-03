package dev.mv.engine.render.vulkan;

import dev.mv.engine.render.DrawContext2D;
import dev.mv.engine.render.DrawContext3D;
import dev.mv.engine.render.Window;
import org.joml.Matrix4f;

public class VulkanWindow implements Window {

    @Override
    public void run() {

    }

    @Override
    public void run(Runnable onStart, Runnable onUpdate, Runnable onDraw) {

    }

    @Override
    public void setFPSCap(int cap) {

    }

    @Override
    public void setUPSCap(int cap) {

    }

    @Override
    public int getWidth() {
        return 0;
    }

    @Override
    public int getHeight() {
        return 0;
    }

    @Override
    public int getFPS() {
        return 0;
    }

    @Override
    public int getUPS() {
        return 0;
    }

    @Override
    public int getFPSCap() {
        return 0;
    }

    @Override
    public int getUPSCap() {
        return 0;
    }

    @Override
    public long getGlfwId() {
        return 0;
    }

    @Override
    public Matrix4f getProjectionMatrix2D() {
        return null;
    }

    @Override
    public Matrix4f getProjectionMatrix3D() {
        return null;
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public DrawContext2D getDrawContext2D() {
        return null;
    }

    @Override
    public DrawContext3D getDrawContext3D() {
        return null;
    }
}
