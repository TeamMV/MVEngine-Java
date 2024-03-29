package dev.mv.engine.render.vulkan;

import dev.mv.engine.ApplicationLoop;
import dev.mv.engine.MVEngine;
import dev.mv.engine.exceptions.Exceptions;
import dev.mv.engine.input.GlfwClipboard;
import dev.mv.engine.input.Input;
import dev.mv.engine.render.WindowCreateInfo;
import dev.mv.engine.render.shared.*;
import dev.mv.engine.render.shared.batch.BatchController;
import dev.mv.engine.render.shared.batch.BatchController3D;
import dev.mv.engine.render.utils.RenderUtils;
import org.joml.Matrix4f;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.system.MemoryStack;

import java.nio.IntBuffer;
import java.util.function.Consumer;

import static java.sql.Types.NULL;
import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.system.MemoryStack.stackPush;

public class VulkanWindow implements Window {
    private WindowCreateInfo info;
    private int width, height;
    private int currentFPS, currentUPS;
    private long currentFrame = 0, currentTime = 0;
    private double deltaF;
    private long window, surface, renderPass;
    private ApplicationLoop applicationLoop = null;
    private Window fallbackWindow = null;
    private int oW, oH, oX, oY;
    private double timeU, timeF;
    private MVEngine engine;


    public VulkanWindow(WindowCreateInfo info) {
        this.info = info;
        width = info.width;
        height = info.height;
        engine = MVEngine.instance();
    }

    @Override
    public void run(ApplicationLoop applicationLoop) {
        this.applicationLoop = applicationLoop;
        run();
    }

    @Override
    public void run() {
        if (!init()) {
            glfwFreeCallbacks(window);
            glfwDestroyWindow(window);
            engine.rollbackRenderingApi();
            fallbackWindow = engine.createWindow(info);
            fallbackWindow.run(applicationLoop);
            return;
        }

        if (info.fullscreen) {
            setFullscreen(true);
        }

        //declareProjection();
        //render2D = new OpenGLRender2D(this);
        //render3D = new OpenGLRender3D(this);
        //camera = new Camera();

        if (applicationLoop != null) {
            try {
                applicationLoop.start(engine, this);
            } catch (Exception e) {
                Exceptions.send(e);
            }
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

        glfwShowWindow(window);
        return true;
    }

    private void loop() {
        try {
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
                    if (applicationLoop != null) {
                        applicationLoop.update(engine, this);
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

                    if (applicationLoop != null) {
                        applicationLoop.draw(engine, this);
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
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void updateInputs() {
        if (Input.mouse[Input.MOUSE_SCROLL_X] == 1.0 || Input.mouse[Input.MOUSE_SCROLL_X] == -1.0) {
            Input.mouse[Input.MOUSE_SCROLL_X] = 0;
        }
        if (Input.mouse[Input.MOUSE_SCROLL_Y] == 1.0 || Input.mouse[Input.MOUSE_SCROLL_Y] == -1.0) {
            Input.mouse[Input.MOUSE_SCROLL_Y] = 0;
        }

        for (int i = 0; i < Input.keys.length; i++) {
            if (Input.keys[i] == Input.State.ONPRESSED) {
                Input.keys[i] = Input.State.PRESSED;
            }
            if (Input.keys[i] == Input.State.ONRELEASED) {
                Input.keys[i] = Input.State.RELEASED;
            }
        }

        for (int i = 0; i < Input.buttons.length; i++) {
            if (Input.buttons[i] == Input.State.ONPRESSED) {
                Input.buttons[i] = Input.State.PRESSED;
            }
            if (Input.buttons[i] == Input.State.ONRELEASED) {
                Input.buttons[i] = Input.State.RELEASED;
            }
        }
    }

    private void terminate() {
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
    public boolean isVsync() {
        return false;
    }

    @Override
    public void setVsync(boolean vsync) {

    }

    @Override
    public long getCurrentFrame() {
        return 0;
    }

    @Override
    public long getGlfwId() {
        return window;
    }

    @Override
    public void addResizeCallback(Consumer<Window> callback) {

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
    public RenderAdapter getAdapter() {
        return null;
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
    public GlfwClipboard getClipboard() {
        return null;
    }

    @Override
    public BatchController getBatchController() {
        return null;
    }

    @Override
    public BatchController3D getBatchController3D() {
        return null;
    }
}
