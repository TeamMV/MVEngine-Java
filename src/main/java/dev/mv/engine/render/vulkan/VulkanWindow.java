package dev.mv.engine.render.vulkan;

import dev.mv.engine.MVEngine;
import dev.mv.engine.render.DrawContext2D;
import dev.mv.engine.render.DrawContext3D;
import dev.mv.engine.render.Window;
import imgui.gl3.ImGuiImplGl3;
import imgui.glfw.ImGuiImplGlfw;
import org.joml.Matrix4f;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.system.MemoryStack;

import java.nio.IntBuffer;
import java.nio.LongBuffer;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.GLFWVulkan.glfwCreateWindowSurface;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.NULL;
import static org.lwjgl.vulkan.KHRSurface.*;
import static org.lwjgl.vulkan.VK10.VK_NULL_HANDLE;
import static org.lwjgl.vulkan.VK10.VK_SUCCESS;

public class VulkanWindow implements Window {

    private long window, surface;
    private String title;
    private int width, height;
    private boolean resizeable;
    private Runnable onStart = null, onUpdate = null, onDraw = null;
    private ImGuiImplGlfw glfwImpl;
    private ImGuiImplVulkan vkImpl;

    public VulkanWindow(String title, int width, int height, boolean resizeable) {
        this.title = title;
        this.width = width;
        this.height = height;
        this.resizeable = resizeable;
    }

    @Override
    public void run() {
        if (!init()) return;

        if (onStart != null) {
            onStart.run();
        }

        loop();

        vkDestroySurfaceKHR(Vulkan.getInstance(), surface, null);

        glfwFreeCallbacks(window);
        glfwDestroyWindow(window);
    }

    @Override
    public void run(Runnable onStart, Runnable onUpdate, Runnable onDraw) {
        this.onStart = onStart;
        this.onUpdate = onUpdate;
        this.onDraw = onDraw;
        run();
    }

    private boolean init() {
        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, resizeable ? GLFW_TRUE : GLFW_FALSE);

        window = glfwCreateWindow(width, height, title, NULL, NULL);
        if (window == NULL) {
            throw new RuntimeException("Failed to create the GLFW window");
        }

        try (MemoryStack stack = stackPush()) {
            IntBuffer pWidth = stack.mallocInt(1);
            IntBuffer pHeight = stack.mallocInt(1);

            glfwGetWindowSize(window, pWidth, pHeight);

            GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());

            glfwSetWindowPos(window,
                    (vidmode.width() - pWidth.get(0)) / 2,
                    (vidmode.height() - pHeight.get(0)) / 2
            );
        }

        glfwMakeContextCurrent(window);
        glfwSwapInterval(1);

        try(MemoryStack stack = stackPush()) {
            LongBuffer pSurface = stack.longs(VK_NULL_HANDLE);
            if(glfwCreateWindowSurface(Vulkan.getInstance(), window, null, pSurface) != VK_SUCCESS) {
                throw new RuntimeException("Failed to create window surface");
            }
            surface = pSurface.get(0);
        }

        if (!Vulkan.setupSurfaceSupport(surface)) {
            MVEngine.disableVulkan();
            return false;
        }

        glfwImpl = new ImGuiImplGlfw();
        glfwImpl.init(window, true);
        vkImpl = new ImGuiImplVulkan();
        vkImpl.init("#version 450");

        glfwShowWindow(window);
        return true;
    }

    private void loop() {

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
    public void setFPSCap(int cap) {

    }

    @Override
    public int getUPSCap() {
        return 0;
    }

    @Override
    public void setUPSCap(int cap) {

    }

    @Override
    public long getGlfwId() {
        return window;
    }

    public long getVulkanSurfaceId() {
        return surface;
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
    public String getTitle() {
        return title;
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
