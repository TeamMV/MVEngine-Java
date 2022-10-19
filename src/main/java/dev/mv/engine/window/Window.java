package dev.mv.engine.window;

import dev.mv.engine.camera.Camera;
import dev.mv.engine.input.InputCore;
import dev.mv.engine.render.Draw;
import dev.mv.engine.text.FontHolder;
import dev.mv.engine.utils.DefaultTextures;
import lombok.Getter;
import lombok.Setter;
import org.joml.Vector2f;
import org.lwjgl.BufferUtils;
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

public class Window {

    public Draw draw;
    public Camera camera;
    public InputCore input;
    @Getter
    @Setter
    private int UPS = 30, FPS = 60;
    @Getter
    private int currentFPS, currentUPS;
    @Getter
    private int width, height;
    private String title;
    private boolean resize;
    private Renderer mainClass;
    // The window handle
    @Getter
    private long window;

    @Getter
    private long currentFrame = 0, currentTime = 0;

    @Getter
    private boolean fullscreen = false;
    @Getter
    private double deltaF;
    private int oW, oH;
    private int oX, oY;
    private boolean running = true;

    /**
     * Creates a new Window object with
     *
     * @param width      width of the window
     * @param height     height of the window
     * @param title      title of the window
     * @param resizeable if the window is resizeable
     * @param mainClass  the main class of the game
     *                   as the parameters.
     */

    public Window(int width, int height, String title, boolean resizeable, Renderer mainClass) {
        this.width = width;
        this.height = height;
        this.title = title;
        this.resize = resizeable;
        this.mainClass = mainClass;

        camera = new Camera(new Vector2f(0, 0), this, false);
    }

    /**
     * Opens the window and calls the start() method of the interface Renderer.
     */

    public void run() {

        init();
        FontHolder.onStart();
        mainClass.start(this);
        loop();

        // Free the window callbacks and destroy the window
        glfwFreeCallbacks(window);
        glfwDestroyWindow(window);

        // Terminate GLFW and free the error callback
        glfwTerminate();
        glfwSetErrorCallback(null).free();

        System.exit(0);
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
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        draw = new Draw(this.width, this.height, this);
        input = new InputCore(this);
        FontHolder.onStart();
        DefaultTextures.onStart();

        glfwSetWindowSizeCallback(window, (window, w, h) -> {
            width = w;
            height = h;

            glViewport(0, 0, w, h);
            camera.declareProjection();

            mainClass.resize(this, w, h);
        });
    }

    public void drawAndSwapBuffers() {
        glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        draw.draw();
        glfwSwapBuffers(window);
    }

    private void loop() {

        // Set the clear color
        glClearColor(0.0f, 0.0f, 0.0f, 1.0f);

        // Run the rendering loop until the user has attempted to close
        // the window or has pressed the ESCAPE key.

        long initialTime = System.nanoTime();
        final double timeU = 1000000000 / UPS;
        final double timeF = 1000000000 / FPS;
        double deltaU = 0, deltaF = 0;
        int frames = 0, ticks = 0;
        long timer = System.currentTimeMillis();
        while (!glfwWindowShouldClose(window) && running) {
            long currentTime = System.nanoTime();
            deltaU += (currentTime - initialTime) / timeU;
            deltaF += (currentTime - initialTime) / timeF;
            initialTime = currentTime;
            glfwPollEvents();
            this.deltaF = deltaF;
            if (deltaU >= 1) {
                mainClass.update(this);
                currentTime++;
                ticks++;
                deltaU--;
            }
            if (deltaF >= 1) {
                glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
                mainClass.render(this);
                draw.draw();
                mainClass.renderAfter(this);
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

        mainClass.onClose(this);
    }

    public void onKeyDown(int c) {

    }

    public void onKeyUp(int c) {

    }

    public void onKeyTyped(char c) {

    }

    public void onMouseAction(int button, int action, int mods) {

    }

    public void onScroll(int x, int y) {

    }

    public void onMouseMove(int x, int y) {

    }

    public void changeCurrentContext(long windowId) {
        glfwMakeContextCurrent(windowId);
        window = windowId;
    }

    public void close(int code) {
        mainClass.onClose(this);
        running = false;
        System.exit(code);
    }

    public void setFullscreen(boolean fullscreen) {
        this.fullscreen = fullscreen;
        if (fullscreen) {
            IntBuffer oXb = BufferUtils.createIntBuffer(1).put(0), oYb = BufferUtils.createIntBuffer(1).put(0);
            glfwGetWindowPos(window, oXb, oYb);
            oW = width;
            oH = height;
            oX = oXb.get(0);
            oY = oYb.get(0);
            long monitor = glfwGetPrimaryMonitor();
            GLFWVidMode mode = glfwGetVideoMode(monitor);
            System.out.println(mode.width() + ":" + mode.height());
            glfwSetWindowMonitor(window, monitor, 0, 0, mode.width(), mode.height(), mode.refreshRate());
            width = mode.width();
            height = mode.height();
        } else {
            long monitor = glfwGetPrimaryMonitor();
            GLFWVidMode mode = glfwGetVideoMode(monitor);
            glfwSetWindowMonitor(window, 0, oX, oY, oW, oH, mode.refreshRate());
            System.out.println(mode.width() + "_" + mode.height());
        }
    }

}