package dev.mv.engine.render.vulkan;

import dev.mv.engine.MVEngine;
import dev.mv.engine.render.DrawContext2D;
import dev.mv.engine.render.DrawContext3D;
import dev.mv.engine.render.Window;
import dev.mv.engine.render.utils.RenderUtils;
import imgui.glfw.ImGuiImplGlfw;
import lombok.Getter;
import org.joml.Matrix4f;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.system.MemoryStack;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.LongBuffer;
import java.nio.charset.StandardCharsets;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.GLFWVulkan.glfwCreateWindowSurface;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.NULL;
import static org.lwjgl.vulkan.KHRSurface.*;
import static org.lwjgl.vulkan.KHRSwapchain.vkDestroySwapchainKHR;
import static org.lwjgl.vulkan.VK10.*;

public class VulkanWindow implements Window {

    private int UPS = 30, FPS = 60;
    private int currentFPS, currentUPS;
    private double deltaF;
    private long currentFrame = 0, currentTime = 0;
    private long window, surface;
    @Getter
    VulkanSwapChain swapChain;
    @Getter
    private VulkanGraphicsPipeline graphicsPipeline;
    @Getter
    VulkanCommandPool commandPool;
    private VulkanRender render;
    private String title;
    private int width, height;
    private boolean resizeable;
    private Runnable onStart = null, onUpdate = null, onDraw = null;
    private ImGuiImplGlfw glfwImpl;
    private ImGuiImplVulkan vkImpl;

    public VulkanWindow(String title, int width, int height, boolean resizeable) {
        Vulkan.check();
        this.title = title;
        this.width = width;
        this.height = height;
        this.resizeable = resizeable;
    }

    @Override
    public void run() {
        if (!init()) {
            Vulkan.terminate();
            MVEngine.disableVulkan();
            Window replacement = MVEngine.createWindow(width, height, title, resizeable);
            replacement.run(onStart, onUpdate, onDraw);
            return;
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

        try (MemoryStack stack = stackPush()) {
            LongBuffer pSurface = stack.longs(VK_NULL_HANDLE);
            if(glfwCreateWindowSurface(Vulkan.getInstance(), window, null, pSurface) != VK_SUCCESS) {
                return false;
            }
            surface = pSurface.get(0);
        }

        if (!Vulkan.setupSurfaceSupport(surface))
            return false;
        swapChain = Vulkan.createSwapChain(surface, width, height);
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

            glViewport(0, 0, w, h);
        });
        return true;
    }

    private void loop() {
        //clear colour vulkan?

        long initialTime = System.nanoTime();
        final double timeU = 1000000000f / UPS;
        final double timeF = 1000000000f / FPS;
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
                String fpsTitle = title + " - " + getFPS() + " fps";
                byte[] bytes = fpsTitle.getBytes(StandardCharsets.UTF_8);
                ByteBuffer buffer = BufferUtils.createByteBuffer(bytes.length + 1);
                buffer.put(bytes);
                buffer.put((byte) 0x0);
                buffer.flip();
                glfwSetWindowTitle(window, buffer);
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
        vkDeviceWaitIdle(Vulkan.getLogicalDevice());
    }

    void recreateSwapChain() {
        try (MemoryStack stack = stackPush()) {
            IntBuffer width = stack.ints(0);
            IntBuffer height = stack.ints(0);
            glfwGetFramebufferSize(window, width, height);
            while (width.get(0) == 0 || height.get(0) == 0) {
                glfwGetFramebufferSize(window, width, height);
                glfwWaitEvents();
            }
        }
        vkDeviceWaitIdle(Vulkan.getLogicalDevice());

        terminateSwapChain();

        swapChain = Vulkan.createSwapChain(surface, width, height);
        Vulkan.createImageViews(swapChain);
        graphicsPipeline = Vulkan.createGraphicsPipeline(swapChain, "shaders/3d/default.vert", "shaders/3d/default.frag");
        Vulkan.createFramebuffers(swapChain, graphicsPipeline);
        commandPool = Vulkan.createCommandPool();
        Vulkan.createCommandBuffers(commandPool, swapChain, graphicsPipeline);
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
        return FPS;
    }

    @Override
    public void setFPSCap(int cap) {
        FPS = cap;
    }

    @Override
    public int getUPSCap() {
        return UPS;
    }

    @Override
    public void setUPSCap(int cap) {
        UPS = cap;
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
