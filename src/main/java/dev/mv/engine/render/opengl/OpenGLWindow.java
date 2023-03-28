package dev.mv.engine.render.opengl;

import dev.mv.engine.ApplicationLoop;
import dev.mv.engine.Loopable;
import dev.mv.engine.MVEngine;
import dev.mv.engine.exceptions.Exceptions;
import dev.mv.engine.gui.GuiManager;
import dev.mv.engine.input.Input;
import dev.mv.engine.render.WindowCreateInfo;
import dev.mv.engine.render.shared.Camera;
import dev.mv.engine.render.shared.Render2D;
import dev.mv.engine.render.shared.Render3D;
import dev.mv.engine.render.shared.Window;
import dev.mv.engine.render.shared.batch.BatchController;
import dev.mv.engine.render.shared.batch.BatchController3D;
import dev.mv.engine.render.utils.RenderUtils;
import org.joml.Matrix4f;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryStack;

import java.nio.IntBuffer;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.NULL;

public class OpenGLWindow implements Window {
    private final float FOV = (float) Math.toRadians(60);
    private final float Z_NEAR = 0.01f;
    private final float Z_FAR = 2000f;
    BatchController batchController;
    BatchController3D batchController3D;
    private int currentFPS, currentUPS;
    private int width, height;
    private double deltaF;
    private long window;
    private long currentFrame = 0, currentTime = 0;
    private ApplicationLoop applicationLoop = null;
    private OpenGLRender2D render2D = null;
    private OpenGLRender3D render3D = null;
    private Matrix4f projectionMatrix = null;
    private WindowCreateInfo info;
    private int oW, oH, oX, oY;
    private double timeU, timeF;
    private MVEngine engine;

    private Camera camera;
    private String fpsStringBefore = "";

    public OpenGLWindow(WindowCreateInfo info) {
        this.info = info;
        width = info.width;
        height = info.height;
        currentFPS = info.maxFPS;
        currentUPS = info.maxUPS;
        this.engine = MVEngine.instance();
    }

    @Override
    public void run() {
        init();

        if (info.fullscreen) {
            setFullscreen(true);
        }

        updateProjection2D();
        render2D = new OpenGLRender2D(this);
        render3D = new OpenGLRender3D(this);
        camera = new Camera();
        batchController3D = new BatchController3D(this, 1000);
        batchController3D.start();
        batchController = new BatchController(this, 1000);
        batchController.start();

        MVEngine.instance().handleInputs(this);
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

    @Override
    public void run(ApplicationLoop applicationLoop) {
        this.applicationLoop = applicationLoop;
        run();
    }

    @Override
    public void stop() {
        glfwSetWindowShouldClose(window, true);
    }

    private void init() {
        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_CLIENT_API, GLFW_OPENGL_API);
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

            glfwSetWindowPos(
                window,
                (vidmode.width() - pWidth.get(0)) / 2,
                (vidmode.height() - pHeight.get(0)) / 2
            );
        }

        glfwMakeContextCurrent(window);
        glfwSwapInterval(1);

        GL.createCapabilities();

        glfwShowWindow(window);

        //glEnable(GL_CULL_FACE);
        glCullFace(GL_BACK);
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glEnable(GL_DEPTH_TEST);
        glDepthMask(true);
        glDepthFunc(GL_LEQUAL);
        glDepthRange(0.0f, Z_FAR);

        glfwSetWindowSizeCallback(window, (window, w, h) -> {
            width = w;
            height = h;

            glViewport(0, 0, w, h);
            updateProjection2D();

            GuiManager.sendResizeEvent(w, h);
        });
    }

    public void drawAndSwapBuffers() {
        glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        glfwSwapBuffers(window);
    }

    private void loop() {
        // Set the clear color
        glClearColor(0.0f, 0.0f, 0.0f, 1.0f);

        // Run the rendering loop until the user has attempted to close
        // the window or has pressed the ESCAPE key.

        long initialTime = System.nanoTime();
        long currentTime = initialTime;
        timeU = 1000000000f / info.maxUPS;
        timeF = 1000000000f / info.maxFPS;
        double deltaU = 0, deltaF = 0;
        int frames = 0, ticks = 0;
        long timer = System.currentTimeMillis();
        GuiManager.sendResizeEvent(width, height);
        while (!glfwWindowShouldClose(window)) {
            currentTime = System.nanoTime();
            deltaU += (currentTime - initialTime) / timeU;
            deltaF += (currentTime - initialTime) / timeF;
            initialTime = currentTime;
            glfwPollEvents();
            this.deltaF = deltaF;
            if (deltaU >= 1) {
                if (applicationLoop != null) {
                    try {
                        applicationLoop.update(engine, this);
                        MVEngine.instance().getLoopers().forEach(Loopable::loop);
                    } catch (Exception e) {
                        Exceptions.send(e);
                    }
                }
                if (info.appendFpsToTitle) {
                    String fpsTitle = info.title + info.fpsAppendConfiguration.betweenTitleAndValue + getFPS() + info.fpsAppendConfiguration.afterValue;
                    if (!fpsStringBefore.equals(fpsTitle)) {
                        fpsStringBefore = fpsTitle;
                        glfwSetWindowTitle(window, RenderUtils.store(fpsTitle));
                    }
                }

                ticks++;
                deltaU--;
            }
            if (deltaF >= 1) {
                glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

                if (applicationLoop != null) {
                    try {
                        applicationLoop.draw(engine, this);
                    } catch (Exception e) {
                        Exceptions.send(e);
                    }
                }

                updateInputs();

                batchController3D.finishAndRender();
                render3D.render();
                updateProjection2D();
                batchController.finishAndRender();

                glfwSwapBuffers(window);
                currentFrame++;
                frames++;
                deltaF--;
            }
            if (System.currentTimeMillis() - timer > 1000) {
                currentUPS = ticks;
                currentFPS = frames;
                frames = 0;
                ticks = 0;
                timer += 1000;
            }
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

        System.exit(0);
    }

    public void updateProjection2D() {
        if (projectionMatrix == null) {
            projectionMatrix = new Matrix4f();
        }
        projectionMatrix.identity();
        projectionMatrix.ortho(0.0f, (float) this.getWidth(), 0.0f, (float) this.getHeight(), 0.0f, Z_FAR);
    }

    @Override
    public boolean isFullscreen() {
        return info.fullscreen;
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
    public Matrix4f getProjectionMatrix2D() {
        return projectionMatrix;
    }

    @Override
    public Matrix4f getProjectionMatrix3D() {
        float aspect = (float) width / (float) height;
        return projectionMatrix.setPerspective(FOV, aspect, Z_NEAR, Z_FAR);
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
    public long getCurrentFrame() {
        return currentFrame;
    }

    @Override
    public long getGlfwId() {
        return window;
    }

    @Override
    public String getTitle() {
        return info.title;
    }

    @Override
    public void setTitle(String title) {
        info.title = title;
        glfwSetWindowTitle(window, RenderUtils.store(info.title));
    }

    @Override
    public Render2D getRender2D() {
        return render2D;
    }

    @Override
    public Render3D getRender3D() {
        return render3D;
    }

    @Override
    public Camera getCamera() {
        return camera;
    }

    @Override
    public BatchController getBatchController() {
        return batchController;
    }

    @Override
    public BatchController3D getBatchController3D() {
        return batchController3D;
    }

    public WindowCreateInfo getWindowCreateInfo() {
        return info;
    }
}
