package dev.mv.engine.render.opengl;

import dev.mv.engine.render.shared.Camera;
import dev.mv.engine.render.shared.Render2D;
import dev.mv.engine.render.shared.Render3D;
import dev.mv.engine.render.shared.Window;
import dev.mv.engine.render.WindowCreateInfo;
import dev.mv.engine.render.utils.RenderUtils;
import imgui.ImGui;
import imgui.gl3.ImGuiImplGl3;
import imgui.glfw.ImGuiImplGlfw;
import lombok.Getter;
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
    private final float Z_NEAR = 0.0f;
    private final float Z_FAR = 100f;
    private int currentFPS, currentUPS;
    private int width, height;
    private double deltaF;
    private long window;
    private long currentFrame = 0, currentTime = 0;
    private Runnable onStart = null, onUpdate = null, onDraw = null;
    private OpenGLRender2D render2D = null;
    private OpenGLRender3D render3D = null;
    private Matrix4f projectionMatrix = null;
    private ImGuiImplGlfw glfwImpl;
    private ImGuiImplGl3 glImpl;
    @Getter
    private WindowCreateInfo info;
    private int oW, oH, oX, oY;

    private Camera camera;

    public OpenGLWindow(WindowCreateInfo info) {
        this.info = info;
        width = info.width;
        height = info.height;
    }

    @Override
    public void run() {
        init();

        if (info.fullscreen) {
            setFullscreen(true);
        }

        declareProjection();
        //render2D = new OpenGLRender2D(this);
        render3D = new OpenGLRender3D(this);
        camera = new Camera();

        if (onStart != null) {
            onStart.run();
        }

        loop();

        // Free the window callbacks and destroy the window
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

    private void init() {
        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
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

        // This line is critical for LWJGL's interoperation with GLFW's
        // OpenGL context, or any context that is managed externally.
        // LWJGL detects the context that is current in the current thread,
        // creates the GLCapabilities instance and makes the OpenGL
        // bindings available for use.
        GL.createCapabilities();

        glfwImpl = new ImGuiImplGlfw();
        glfwImpl.init(window, true);
        glImpl = new ImGuiImplGl3();
        glImpl.init("#version 400");

        // Make the window visible
        glfwShowWindow(window);

        glEnable(GL_CULL_FACE_MODE);
        glEnable(GL_DEPTH_TEST);
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        glfwSetWindowSizeCallback(window, (window, w, h) -> {
            width = w;
            height = h;

            glViewport(0, 0, w, h);
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
                glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

                //glfwImpl.newFrame();
                //ImGui.newFrame();

                //glClearColor(1.0f, 0.0f, 0.0f, 1.0f);
                if (onDraw != null) {
                    onDraw.run();
                }
                //BatchController.finishAndRender();
                render3D.render();

                //ImGui.render();
                //glImpl.renderDrawData(ImGui.getDrawData());

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

    public void declareProjection() {
        if (projectionMatrix == null) {
            projectionMatrix = new Matrix4f();
        }
        float[] mat = new float[16];
        projectionMatrix.identity();
        projectionMatrix.ortho(0.0f, (float) this.getWidth(), 0.0f, (float) this.getHeight(), Z_NEAR, Z_FAR);
    }

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

    @Override
    public String getTitle() {
        return info.title;
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
}
