package dev.mv.engine.render.vulkan;

import dev.mv.engine.MVEngine;
import dev.mv.engine.input.Input;
import dev.mv.engine.render.WindowCreateInfo;
import dev.mv.engine.render.shared.Camera;
import dev.mv.engine.render.shared.Render2D;
import dev.mv.engine.render.shared.Render3D;
import dev.mv.engine.render.shared.Window;
import dev.mv.engine.render.utils.RenderUtils;
import imgui.gl3.ImGuiImplGl3;
import imgui.glfw.ImGuiImplGlfw;
import lombok.SneakyThrows;
import org.joml.Matrix4f;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.system.MemoryStack;

import java.nio.IntBuffer;

import static java.sql.Types.NULL;
import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.system.MemoryStack.stackPush;

public class VulkanWindow implements Window {

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
            glfwFreeCallbacks(window);
            glfwDestroyWindow(window);
            MVEngine.rollbackRenderingApi();
            fallbackWindow = MVEngine.createWindow(info);
            fallbackWindow.run(onStart, onUpdate, onDraw);
            return;
        }

        if (info.fullscreen) {
            setFullscreen(true);
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
        glfwWindowHint(GLFW_CLIENT_API, GLFW_NO_API);
        glfwWindowHint(GLFW_RESIZABLE, info.resizeable ? GLFW_TRUE : GLFW_FALSE);
        glfwWindowHint(GLFW_DECORATED, info.decorated ? GLFW_TRUE : GLFW_FALSE);

        window = glfwCreateWindow(info.width, info.height, info.title, NULL, NULL);
        if (window == NULL) {
            throw new RuntimeException("Failed to create the GLFW window");
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

        glfwImpl = new ImGuiImplGlfw();
        glfwImpl.init(window, true);
        //vulkanImpl = new ImGuiVulkanImpl();
        //vulkanImpl.init("#version 450");

        glfwShowWindow(window);
        return true;
    }

    @SneakyThrows
    private void loop() {
        long initialTime = System.nanoTime();
        long currentTime = initialTime;
        timeU = 1000000000f / info.maxUPS;
        timeF = 1000000000f / info.maxFPS;
        double deltaU = 0, deltaF = 0;
        int frames = 0, ticks = 0;
        long timer = System.currentTimeMillis();
        VulkanRender.VulkanRenderInfo renderInfo = new VulkanRender.VulkanRenderInfo();
        renderInfo.physicalDeviceName = "";
        renderInfo.requestedImages = 3;
        renderInfo.vsync = true;
        renderInfo.shouldValidate = true;
        VulkanRender vulkanRender = new VulkanRender(this, renderInfo);
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
                //glfwImpl.newFrame();
                //ImGui.newFrame();

                if (onDraw != null) {
                    //onDraw.run();
                }

                //updateInputs();

                //ImGui.render();
                //vulkanImpl.renderDrawData(ImGui.getDrawData());

                //glfwSwapBuffers(window);
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

    private void updateInputs() {
        if(Input.mouse[Input.MOUSE_SCROLL_X] == 1.0 || Input.mouse[Input.MOUSE_SCROLL_X] == -1.0) {
            Input.mouse[Input.MOUSE_SCROLL_X] = 0;
        }
        if(Input.mouse[Input.MOUSE_SCROLL_Y] == 1.0 || Input.mouse[Input.MOUSE_SCROLL_Y] == -1.0) {
            Input.mouse[Input.MOUSE_SCROLL_Y] = 0;
        }

        for (int i = 0; i < Input.keys.length; i++) {
            if(Input.keys[i] == Input.State.ONPRESSED) {
                Input.keys[i] = Input.State.PRESSED;
            }
            if(Input.keys[i] == Input.State.ONRELEASED) {
                Input.keys[i] = Input.State.RELEASED;
            }
        }

        for (int i = 0; i < Input.buttons.length; i++) {
            if(Input.buttons[i] == Input.State.ONPRESSED) {
                Input.buttons[i] = Input.State.PRESSED;
            }
            if(Input.buttons[i] == Input.State.ONRELEASED) {
                Input.buttons[i] = Input.State.RELEASED;
            }
        }
    }

    private void terminate() {
        glfwImpl.dispose();

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
        return null;
    }

    @Override
    public void setTitle(String title) {

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

    @Override
    public ImGuiImplGl3 getImGuiGlImpl() {
        return null;
    }

    @Override
    public ImGuiImplGlfw getImGuiGlfwImpl() {
        return glfwImpl;
    }
}
