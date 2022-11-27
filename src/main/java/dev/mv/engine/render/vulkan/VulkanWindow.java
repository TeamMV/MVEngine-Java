package dev.mv.engine.render.vulkan;

import dev.mv.engine.MVEngine;
import dev.mv.engine.render.WindowCreateInfo;
import dev.mv.engine.render.shared.Camera;
import dev.mv.engine.render.shared.Render2D;
import dev.mv.engine.render.shared.Render3D;
import dev.mv.engine.render.shared.Window;
import dev.mv.engine.render.utils.RenderUtils;
import imgui.ImGui;
import imgui.glfw.ImGuiImplGlfw;
import org.joml.Matrix4f;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.VkDevice;
import org.lwjgl.vulkan.VkInstance;
import org.lwjgl.vulkan.VkPhysicalDevice;
import org.lwjgl.vulkan.VkQueue;

import java.nio.IntBuffer;
import java.nio.LongBuffer;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.GLFWVulkan.glfwCreateWindowSurface;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.NULL;
import static org.lwjgl.vulkan.KHRSurface.vkDestroySurfaceKHR;
import static org.lwjgl.vulkan.VK10.VK_NULL_HANDLE;
import static org.lwjgl.vulkan.VK10.VK_SUCCESS;

public class VulkanWindow implements Window {
    Vulkan vulkan;
    VulkanMemoryManager memoryManager = new VulkanMemoryManager(this);
    VkInstance instance;
    VkPhysicalDevice GPU;
    VkDevice GPUWrapper;
    VkQueue graphicsQueue;
    VkQueue presentQueue;
    VulkanSwapChain swapChain;
    WindowCreateInfo info;
    ImGuiImplGlfw glfwImpl;
    int width, height;
    int currentFPS, currentUPS;
    long currentFrame = 0, currentTime = 0;
    double deltaF;
    long window, surface, renderPass;
    Runnable onStart, onUpdate, onDraw;
    Window fallbackWindow = null;
    private int oW, oH, oX, oY;
    private double timeU, timeF;


    public VulkanWindow(WindowCreateInfo info) {
        this.info = info;
        width = info.width;
        height = info.height;
    }

    @Override
    public void run(Runnable onStart, Runnable onUpdate, Runnable onDraw) {
        this.onStart = onStart;
        this.onUpdate = onUpdate;
        this.onDraw = onDraw;
        run();
    }


    @Override
    public void run() {
        if (!init()) {
            return;
        }

        if (info.fullscreen) {
            //setFullscreen(true);
        }

        //declareProjection();
        //render2D = new OpenGLRender2D(this);
        //render3D = new OpenGLRender3D(this);
        //camera = new Camera();

        if (onStart != null) {
            onStart.run();
        }

        loop();
        terminate();
    }

    public void stop() {
        if (fallbackWindow != null) {
            fallbackWindow.stop();
            return;
        }
        glfwSetWindowShouldClose(window, true);
    }

    private boolean init() {
        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, info.resizeable ? GLFW_TRUE : GLFW_FALSE);
        glfwWindowHint(GLFW_DECORATED, info.decorated ? GLFW_TRUE : GLFW_FALSE);

        window = glfwCreateWindow(width, height, info.title, NULL, NULL);
        if (window == NULL) {
            throw new RuntimeException("Failed to create the GLFW window");
        }

        vulkan = new Vulkan(this);
        if (!vulkan.init()) {
            glfwFreeCallbacks(window);
            glfwDestroyWindow(window);
            MVEngine.rollbackRenderingApi();
            fallbackWindow = MVEngine.createWindow(info);
            fallbackWindow.run();
            return false;
        }

        try (MemoryStack stack = stackPush()) {
            IntBuffer pWidth = stack.mallocInt(1);
            IntBuffer pHeight = stack.mallocInt(1);

            glfwGetWindowSize(window, pWidth, pHeight);

            GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());

            glfwSetWindowPos(
                window,
                (vidmode.width() - pWidth.get(0)) / 2,
                (vidmode.height() - pHeight.get(0)) / 2
            );
        }

        glfwSwapInterval(1);

        glfwImpl = new ImGuiImplGlfw();
        glfwImpl.init(window, true);

        glfwShowWindow(window);
        return true;
    }

    private void loop() {
        long initialTime = System.nanoTime();
        long currentTime = initialTime;
        timeU = 1000000000f / info.maxUPS;
        timeF = 1000000000f / info.maxFPS;
        double deltaU = 0, deltaF = 0;
        int frames = 0, ticks = 0;
        long timer = System.currentTimeMillis();
        while (!glfwWindowShouldClose(window)) {
            currentTime = System.nanoTime();
            deltaU += (currentTime - initialTime) / timeU;
            deltaF += (currentTime - initialTime) / timeF;
            initialTime = currentTime;
            glfwPollEvents();
            this.deltaF = deltaF;
            if (deltaU >= 1) {
                if (onUpdate != null) {
                    onUpdate.run();
                }
                if (info.appendFpsToTitle) {
                    String fpsTitle = info.title + info.fpsAppendConfiguration.betweenTitleAndValue + getFPS() + info.fpsAppendConfiguration.afterValue;
                    glfwSetWindowTitle(window, RenderUtils.store(fpsTitle));
                }
                ticks++;
                deltaU--;
            }
            if (deltaF >= 1) {

                glfwImpl.newFrame();
                ImGui.newFrame();

                if (onDraw != null) {
                    onDraw.run();
                }

                ImGui.render();
                //vulkanImpl.renderDrawData(ImGui.getDrawData());

                glfwSwapBuffers(window);
                currentFrame++;
                frames++;
                deltaF--;
            }
            if (System.currentTimeMillis() - timer > 1000) {
                if (true) {
                    currentUPS = ticks;
                    currentFPS = frames;
                }
                frames = 0;
                ticks = 0;
                timer += 1000;
            }
        }
    }

    private void terminate() {
        glfwImpl.dispose();

        vulkan.terminate();

        glfwFreeCallbacks(window);
        glfwDestroyWindow(window);
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public int getHeight() {
        return height;
    }

    @Override
    public int getFPS() {
        return currentFPS;
    }

    @Override
    public int getUPS() {
        return currentUPS;
    }

    @Override
    public int getFPSCap() {
        return info.maxFPS;
    }

    @Override
    public void setFPSCap(int cap) {
        info.maxFPS = cap;
        timeF = 1000000000f / info.maxFPS;
    }

    @Override
    public int getUPSCap() {
        return info.maxUPS;
    }

    @Override
    public void setUPSCap(int cap) {
        info.maxUPS = cap;
        timeU = 1000000000f / info.maxUPS;
    }

    @Override
    public long getGlfwId() {
        return window;
    }

    @Override
    public boolean isFullscreen() {
        return info.fullscreen;
    }

    @Override
    public void setFullscreen(boolean fullscreen) {
        info.fullscreen = fullscreen;
        if (fullscreen) {
            IntBuffer oXb = BufferUtils.createIntBuffer(1).put(0), oYb = BufferUtils.createIntBuffer(1).put(0);
            glfwGetWindowPos(window, oXb, oYb);
            oW = width;
            oH = height;
            oX = oXb.get(0);
            oY = oYb.get(0);
            long monitor = glfwGetPrimaryMonitor();
            GLFWVidMode mode = glfwGetVideoMode(monitor);
            glfwSetWindowMonitor(window, monitor, 0, 0, mode.width(), mode.height(), mode.refreshRate());
            width = mode.width();
            height = mode.height();
        } else {
            long monitor = glfwGetPrimaryMonitor();
            GLFWVidMode mode = glfwGetVideoMode(monitor);
            glfwSetWindowMonitor(window, 0, oX, oY, oW, oH, mode.refreshRate());
        }
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
        return info.title;
    }

    @Override
    public void setTitle(String title) {
        info.title = title;
    }

    @Override
    public Render2D getRender2D() {
        return null;
    }

    @Override
    public Render3D getRender3D() {
        return null;
    }

    @Override
    public Camera getCamera() {
        return null;
    }
}
