package dev.mv.engine.render.opengl;

import dev.mv.engine.render.DrawContext2D;
import dev.mv.engine.render.DrawContext3D;
import dev.mv.engine.render.Window;
import dev.mv.engine.render.opengl._2d.OpenGLDrawContext2D;
import dev.mv.engine.render.opengl._2d.batch.OpenGLBatchController2D;
import dev.mv.engine.render.opengl._3d.OpenGLDrawContext3D;
import org.joml.Matrix4f;
import org.joml.Vector4f;
import org.lwjgl.glfw.GLFWErrorCallback;
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
    private int UPS = 30, FPS = 60;

    private int currentFPS, currentUPS;

    private int width, height;
    private String title;
    private boolean resize;
    private double deltaF;
    private long window;
    private long currentFrame = 0, currentTime = 0;

    private Runnable onStart = null, onUpdate = null, onDraw = null;

    private OpenGLDrawContext2D context2D = null;
    private OpenGLDrawContext3D context3D = null;

    private Matrix4f projectionMatrix = null;

    private final float FOV = (float) Math.toRadians(60);
    private final float Z_NEAR = 0.1f;
    private final float Z_FAR = -100f;

    public OpenGLWindow(int width, int heigth, String title, boolean resizeable) {
        this.width = width;
        this.height = heigth;
        this.title = title;
        this.resize = resizeable;
    }

    @Override
    public void run() {
        init();

        if(onStart != null) {
            onStart.run();
        }

        declareProjection();
        context2D = new OpenGLDrawContext2D(this);
        context3D = new OpenGLDrawContext3D(this);
        loop();

        // Free the window callbacks and destroy the window
        glfwFreeCallbacks(window);
        glfwDestroyWindow(window);

        // Terminate GLFW and free the error callback
        glfwTerminate();
        glfwSetErrorCallback(null).free();

        System.exit(0);
    }

    @Override
    public void run(Runnable onStart, Runnable onUpdate, Runnable onDraw) {
        this.onStart = onStart;
        this.onUpdate = onUpdate;
        this.onDraw = onDraw;
        run();
    }

    private void init() {
        // Setup an error callback. The default implementation
        // will print the error message in System.err.
        GLFWErrorCallback.createPrint(System.err).set();

        // Initialize GLFW. Most GLFW functions will not work before doing this.
        if (!glfwInit())
            throw new IllegalStateException("Unable to initialize GLFW");

        // Configure GLFW
        glfwDefaultWindowHints(); // optional, the current window hints are already the default
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE); // the window will stay hidden after creation
        glfwWindowHint(GLFW_RESIZABLE, resize ? GLFW_TRUE : GLFW_FALSE); // the window will be resizable

        // Create the window
        window = glfwCreateWindow(width, height, title, NULL, NULL);
        if (window == NULL)
            throw new RuntimeException("Failed to create the GLFW window");

        // Get the thread stack and push a new frame
        try (MemoryStack stack = stackPush()) {
            IntBuffer pWidth = stack.mallocInt(1); // int*
            IntBuffer pHeight = stack.mallocInt(1); // int*

            // Get the window size passed to glfwCreateWindow
            glfwGetWindowSize(window, pWidth, pHeight);

            // Get the resolution of the primary monitor
            GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());

            // Center the window
            glfwSetWindowPos(
                window,
                (vidmode.width() - pWidth.get(0)) / 2,
                (vidmode.height() - pHeight.get(0)) / 2
            );
        } // the stack frame is popped automatically

        // Make the OpenGL context current
        glfwMakeContextCurrent(window);
        // Enable v-sync
        glfwSwapInterval(1);

        // Make the window visible
        glfwShowWindow(window);

        // This line is critical for LWJGL's interoperation with GLFW's
        // OpenGL context, or any context that is managed externally.
        // LWJGL detects the context that is current in the current thread,
        // creates the GLCapabilities instance and makes the OpenGL
        // bindings available for use.
        GL.createCapabilities();

        glEnable(GL_CULL_FACE_MODE);
        //glEnable(GL_DEPTH_TEST);
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
        glClearColor(1.0f, 0.0f, 0.0f, 1.0f);

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
                if(onUpdate != null) {
                    onUpdate.run();
                }
                ticks++;
                deltaU--;
            }
            if (deltaF >= 1) {
                glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
                //glClearColor(1.0f, 0.0f, 0.0f, 1.0f);
                if(onDraw != null) {
                    onDraw.run();
                }
                //OpenGLBatchController2D.finishAndRender();
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
        if(projectionMatrix == null) {
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
        return projectionMatrix;//.setPerspective(FOV, aspect, Z_NEAR, Z_FAR);
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
    public void setFPSCap(int cap) {
        FPS = cap;
    }

    @Override
    public void setUPSCap(int cap) {
        UPS = cap;
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
    public int getUPSCap() {
        return UPS;
    }

    @Override
    public long getGlfwId() {
        return window;
    }

    @Override
    public String getName() {
        return null;
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
