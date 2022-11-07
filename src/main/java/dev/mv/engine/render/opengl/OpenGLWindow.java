package dev.mv.engine.render.opengl;

import dev.mv.engine.render.DrawContext2D;
import dev.mv.engine.render.DrawContext3D;
import dev.mv.engine.render.Window;
import dev.mv.engine.render.opengl._2d.OpenGLDrawContext2D;
import dev.mv.engine.render.opengl._3d.OpenGLDrawContext3D;
import imgui.ImGui;
import imgui.gl3.ImGuiImplGl3;
import imgui.glfw.ImGuiImplGlfw;
import org.joml.Matrix4f;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryStack;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.charset.StandardCharsets;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.NULL;

public class OpenGLWindow implements Window {
    private final float FOV = (float) Math.toRadians(60);
    private final float Z_NEAR = 0.1f;
    private final float Z_FAR = 100f;
    private int UPS = 30, FPS = 60;
    private int currentFPS, currentUPS;
    private int width, height;
    private String title;
    private boolean resizeable;
    private double deltaF;
    private long window;
    private long currentFrame = 0, currentTime = 0;
    private Runnable onStart = null, onUpdate = null, onDraw = null;
    private OpenGLDrawContext2D context2D = null;
    private OpenGLDrawContext3D context3D = null;
    private Matrix4f projectionMatrix = null;
    private ImGuiImplGlfw glfwImpl;
    private ImGuiImplGl3 glImpl;

    public OpenGLWindow(int width, int heigth, String title, boolean resizeable) {
        this.width = width;
        this.height = heigth;
        this.title = title;
        this.resizeable = resizeable;
    }

    @Override
    public void run() {
        init();

        if (onStart != null) {
            onStart.run();
        }

        declareProjection();
        context2D = new OpenGLDrawContext2D(this);
        context3D = new OpenGLDrawContext3D(this);
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
                buffer.put((byte) 0x0); //the stupid terminator which is required lel
                buffer.flip();
                glfwSetWindowTitle(window, buffer);
                ticks++;
                deltaU--;
            }
            if (deltaF >= 1) {
                glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

                glfwImpl.newFrame();
                ImGui.newFrame();

                //glClearColor(1.0f, 0.0f, 0.0f, 1.0f);
                if (onDraw != null) {
                    onDraw.run();
                }
                //OpenGLBatchController2D.finishAndRender();

                ImGui.render();
                glImpl.renderDrawData(ImGui.getDrawData());

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

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public DrawContext2D getDrawContext2D() {
        return context2D;
    }

    @Override
    public DrawContext3D getDrawContext3D() {
        return context3D;
    }
}
