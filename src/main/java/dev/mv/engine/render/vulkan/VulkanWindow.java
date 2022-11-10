package dev.mv.engine.render.vulkan;

import dev.mv.engine.MVEngine;
import dev.mv.engine.render.Window;
import dev.mv.engine.render.WindowCreateInfo;
import dev.mv.engine.render.utils.RenderUtils;
import imgui.glfw.ImGuiImplGlfw;
import lombok.Getter;
import org.joml.Matrix4f;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.system.MemoryStack;

import java.nio.IntBuffer;
import java.nio.LongBuffer;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.GLFWVulkan.glfwCreateWindowSurface;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.NULL;
import static org.lwjgl.vulkan.KHRSurface.vkDestroySurfaceKHR;
import static org.lwjgl.vulkan.KHRSwapchain.vkDestroySwapchainKHR;
import static org.lwjgl.vulkan.VK10.*;

public class VulkanWindow implements Window {

    @Getter
    VulkanSwapChain swapChain;
    @Getter
    VulkanCommandPool commandPool;
    private int currentFPS, currentUPS;
    private double deltaF;
    private long currentFrame = 0, currentTime = 0;
    private long window, surface;
    @Getter
    private VulkanGraphicsPipeline graphicsPipeline;
    private VulkanRender render;
    private int width, height;
    private Runnable onStart = null, onUpdate = null, onDraw = null;
    private ImGuiImplGlfw glfwImpl;
    private ImGuiImplVulkan vkImpl;
    @Getter
    private WindowCreateInfo info;
    private int oW, oH, oX, oY;

    public VulkanWindow(WindowCreateInfo info) {
        Vulkan.check();
        this.info = info;
        width = info.width;
        height = info.height;
    }

    @Override
    public void run() {
        if (!init()) {
            glfwFreeCallbacks(window);
            glfwDestroyWindow(window);
            Vulkan.terminate();
            MVEngine.disableVulkan();
            Window replacement = MVEngine.createWindow(info);
            replacement.run(onStart, onUpdate, onDraw);
            return;
        }

        if (info.fullscreen) {
            setFullscreen(true);
        }

        if (onStart != null) {
            onStart.run();
        }

        loop();

        terminate();
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
        glfwWindowHint(GLFW_CLIENT_API, GLFW_NO_API);
        glfwWindowHint(GLFW_RESIZABLE, info.resizeable ? GLFW_TRUE : GLFW_FALSE);
        glfwWindowHint(GLFW_DECORATED, info.decorated ? GLFW_TRUE : GLFW_FALSE);

        window = glfwCreateWindow(width, height, info.title, NULL, NULL);
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

        try (MemoryStack stack = stackPush()) {
            LongBuffer pSurface = stack.longs(VK_NULL_HANDLE);
            if (glfwCreateWindowSurface(Vulkan.getInstance(), window, null, pSurface) != VK_SUCCESS) {
                return false;
            }
            surface = pSurface.get(0);
        }

        if (!Vulkan.setupSurfaceSupport(surface))
            return false;
        swapChain = Vulkan.createSwapChain(surface, width, height, info.vsync);
        if (swapChain == null)
            return false;
        if (!Vulkan.createImageViews(swapChain))
            return false;
        graphicsPipeline = Vulkan.createGraphicsPipeline(swapChain, "shaders/3d/default.vert", "shaders/3d/default.frag");
        if (graphicsPipeline == null)
            return false;
        if (!Vulkan.createFramebuffers(swapChain, graphicsPipeline))
            return false;
        commandPool = Vulkan.createCommandPool();
        if (commandPool == null)
            return false;
        if (!Vulkan.createCommandBuffers(commandPool, swapChain, graphicsPipeline))
            return false;
        render = Vulkan.createRender();

        glfwImpl = new ImGuiImplGlfw();
        glfwImpl.init(window, true);
        vkImpl = new ImGuiImplVulkan();
        //vkImpl.init();

        glfwShowWindow(window);

        glfwSetWindowSizeCallback(window, (window, w, h) -> {
            width = w;
            height = h;

            render.framebufferResized = true;
        });
        return true;
    }

    private void loop() {
        //clear colour vulkan?

        long initialTime = System.nanoTime();
        final double timeU = 1000000000f / info.maxUPS;
        final double timeF = 1000000000f / info.maxFPS;
        double deltaU = 0, deltaF = 0;
        int frames = 0, ticks = 0;
        long timer = System.currentTimeMillis();
        while (!glfwWindowShouldClose(window)) {
            long currentTime = System.nanoTime();
            deltaU += (currentTime - initialTime) / timeU;
            deltaF += (currentTime - initialTime) / timeF;
            initialTime = currentTime;
            glfwPollEvents();
            this.deltaF = deltaF;
            if (deltaU >= 1) {
                currentTime++;
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
                //clear vulkan?

                //glfwImpl.newFrame();
                //ImGui.newFrame();

                if (onDraw != null) {
                    onDraw.run();
                }

                //ImGui.render();
                //vkImpl.renderDrawData(ImGui.getDrawData());

                render.drawFrame(this);

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
        vkDeviceWaitIdle(Vulkan.getLogicalDevice());
    }

    void recreateSwapChain() {
        vkDeviceWaitIdle(Vulkan.getLogicalDevice());

        terminateSwapChain();

        swapChain = Vulkan.createSwapChain(surface, width, height, info.vsync);
        Vulkan.createImageViews(swapChain);
        graphicsPipeline = Vulkan.createGraphicsPipeline(swapChain, "shaders/3d/default.vert", "shaders/3d/default.frag");
        Vulkan.createFramebuffers(swapChain, graphicsPipeline);
        commandPool = Vulkan.createCommandPool();
        Vulkan.createCommandBuffers(commandPool, swapChain, graphicsPipeline);
        render = Vulkan.createRender();
    }

    private void terminate() {
        terminateSwapChain();
        vkDestroySurfaceKHR(Vulkan.getInstance(), surface, null);

        glfwFreeCallbacks(window);
        glfwDestroyWindow(window);
    }

    private void terminateSwapChain() {
        render.inFlightFrames.forEach(frame -> {
            vkDestroySemaphore(Vulkan.getLogicalDevice(), frame.renderFinishedSemaphore(), null);
            vkDestroySemaphore(Vulkan.getLogicalDevice(), frame.imageAvailableSemaphore(), null);
            vkDestroyFence(Vulkan.getLogicalDevice(), frame.fence(), null);
        });
        render.imagesInFlight.clear();
        vkDestroyCommandPool(Vulkan.getLogicalDevice(), commandPool.id, null);
        swapChain.framebuffers.forEach(framebuffer -> vkDestroyFramebuffer(Vulkan.getLogicalDevice(), framebuffer, null));
        vkDestroyPipeline(Vulkan.getLogicalDevice(), graphicsPipeline.id, null);
        vkDestroyPipelineLayout(Vulkan.getLogicalDevice(), graphicsPipeline.layout, null);
        vkDestroyRenderPass(Vulkan.getLogicalDevice(), graphicsPipeline.renderPass, null);
        swapChain.imageViews.forEach(imageView -> vkDestroyImageView(Vulkan.getLogicalDevice(), imageView, null));
        vkDestroySwapchainKHR(Vulkan.getLogicalDevice(), swapChain.id, null);
    }

    public void setFullscreen(boolean fullscreen) {
        info.fullscreen = fullscreen;
        if (fullscreen) {
            IntBuffer oXb = BufferUtils.createIntBuffer(1), oYb = BufferUtils.createIntBuffer(1);
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
    }

    @Override
    public int getUPSCap() {
        return info.maxUPS;
    }

    @Override
    public void setUPSCap(int cap) {
        info.maxUPS = cap;
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
        return info.title;
    }
}
