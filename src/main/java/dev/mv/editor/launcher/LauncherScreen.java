package dev.mv.editor.launcher;

import imgui.gl3.ImGuiImplGl3;
import imgui.glfw.ImGuiImplGlfw;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryStack;

import java.nio.IntBuffer;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.NULL;

public class LauncherScreen {
    private long window;
    private ImGuiImplGlfw glfwImpl;
    private ImGuiImplGl3 glImpl;
    private LaunchConfig config;

    public LauncherScreen() {
        config = new LaunchConfig();
    }

    public LaunchConfig run() {
        init();
        loop();

        glImpl.dispose();
        glfwImpl.dispose();

        glfwDestroyWindow(window);

        return config;
    }

    private void init() {
        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);

        window = glfwCreateWindow(900, 600, "Project selection", NULL, NULL);

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

        glfwImpl = new ImGuiImplGlfw();
        glfwImpl.init(window, true);
        glImpl = new ImGuiImplGl3();
        glImpl.init("#version 400");

        glfwShowWindow(window);

        glEnable(GL_CULL_FACE_MODE);
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
    }

    private void loop() {
        glClearColor(0.0f, 0.0f, 0.0f, 1.0f);

        long initialTime = System.currentTimeMillis();
        double timeF = 1000 / 144;
        double deltaF = 0;
        while (!glfwWindowShouldClose(window)) {
            long currentTime = System.currentTimeMillis();
            deltaF += (currentTime - initialTime) / timeF;
            initialTime = currentTime;
            glfwPollEvents();
            if (deltaF >= 1) {
                glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
                glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
                render();
                glfwSwapBuffers(window);
                deltaF--;
            }
        }
    }

    private void render() {

    }

}
