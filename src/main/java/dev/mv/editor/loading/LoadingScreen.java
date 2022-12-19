package dev.mv.editor.loading;

import dev.mv.engine.render.shared.create.RenderBuilder;
import dev.mv.engine.render.shared.texture.Texture;
import imgui.ImGui;
import imgui.ImVec2;
import imgui.flag.ImGuiWindowFlags;
import imgui.gl3.ImGuiImplGl3;
import imgui.glfw.ImGuiImplGlfw;
import lombok.SneakyThrows;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryStack;

import java.nio.IntBuffer;

import static dev.mv.utils.Utils.ifNotNull;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.NULL;

public class LoadingScreen {
    private volatile long window;
    private ImGuiImplGlfw glfwImpl;
    private ImGuiImplGl3 glImpl;
    private String message = "";
    private int width, height;
    private volatile Texture texture = null;

    private volatile String file = null;

    public LoadingScreen() {
    }

    public LoadingScreen(String message) {
        this.message = message;
        if (message == null) this.message = "";
    }

    public LoadingScreen(String message, String texture) {
        this.message = message;
        if (message == null) this.message = "";
        file = texture;
    }

    public void run() {
        init();
        ifNotNull(file).then(str -> setTexture(str));
        loop();

        glImpl.dispose();
        glfwImpl.dispose();

        glfwDestroyWindow(window);
    }

    private void init() {
        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_FALSE);
        glfwWindowHint(GLFW_DECORATED, GLFW_FALSE);
        glfwWindowHint(GLFW_TRANSPARENT_FRAMEBUFFER, GLFW_TRUE);

        window = glfwCreateWindow(600, 400, "Loading", NULL, NULL);

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

        glfwSetWindowSizeCallback(window, (window, w, h) -> {
            width = w;
            height = h;

            glViewport(0, 0, w, h);
        });
    }

    private void loop() {
        glClearColor(0.0f, 0.0f, 0.0f, 1.0f);

        long initialTime = System.currentTimeMillis();
        double timeF = 1000 / 60;
        double deltaF = 0;
        while (!glfwWindowShouldClose(window)) {
            long currentTime = System.currentTimeMillis();
            deltaF += (currentTime - initialTime) / timeF;
            initialTime = currentTime;
            glfwPollEvents();
            if (deltaF >= 1) {
                glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
                glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
                render();
                glfwSwapBuffers(window);
                deltaF--;
            }
        }
    }

    public void stop() {
        glfwSetWindowShouldClose(window, true);
    }

    public void setMessage(String message) {
        this.message = message;
        if (message == null) this.message = "";
    }

    @SneakyThrows
    public void setTexture(String file) {
        if (file == null) {
            texture = null;
            return;
        }
        try {
            texture = RenderBuilder.newTexture(LoadingScreen.class.getResourceAsStream(file));
        } catch (Throwable t) {
            texture = null;
        }
    }

    private void render() {
        int width = 600, height = 400;

        glfwImpl.newFrame();
        ImGui.newFrame();

        ImVec2 messageSize = new ImVec2(0, 0);
        ImGui.calcTextSize(messageSize, message);

        ImGui.setNextWindowPos(0, 0);
        ImGui.setNextWindowSize(600, 400);
        ImGui.setNextWindowBgAlpha(0);
        ImGui.begin("Loading Screen", ImGuiWindowFlags.NoMove | ImGuiWindowFlags.NoCollapse | ImGuiWindowFlags.NoResize | ImGuiWindowFlags.NoTitleBar | ImGuiWindowFlags.NoBackground | ImGuiWindowFlags.NoScrollbar);
        if (texture != null) {
            ImGui.image(texture.getId(), width, height);
        }
        ImGui.setCursorPos((width - messageSize.x) * 0.5f, (height - messageSize.y) - 10);
        ImGui.textUnformatted(message);
        ImGui.end();
        ImGui.render();
        glImpl.renderDrawData(ImGui.getDrawData());
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}
