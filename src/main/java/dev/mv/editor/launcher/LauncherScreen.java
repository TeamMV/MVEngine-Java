package dev.mv.editor.launcher;

import imgui.ImGui;
import imgui.flag.ImGuiWindowFlags;
import imgui.gl3.ImGuiImplGl3;
import imgui.glfw.ImGuiImplGlfw;
import imgui.type.ImString;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryStack;

import java.io.File;
import java.io.IOException;
import java.nio.IntBuffer;
import java.util.Arrays;
import java.util.List;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.NULL;

public class LauncherScreen {
    private long window;
    private ImGuiImplGlfw glfwImpl;
    private ImGuiImplGl3 glImpl;
    private LaunchConfig config;
    private int width, height;

    private List<File> rootFiles;
    private String rootDirectory = "/home/v22/Schreibtisch/coding/java/MVEngine/";
    ImString rootStr = new ImString();

    public LauncherScreen() {
        config = new LaunchConfig();
    }

    public LaunchConfig run() {
        init();

        rootFiles = findRootFiles(rootDirectory);
        try {
            FileTextures.setupTextures();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

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
        width = 900;
        height = 600;

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
        glClearColor(0.3f, 0.3f, 0.3f, 1.0f);

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
                glClearColor(0.3f, 0.3f, 0.3f, 1.0f);
                glfwImpl.newFrame();
                ImGui.newFrame();
                render();
                ImGui.render();
                glImpl.renderDrawData(ImGui.getDrawData());
                glfwSwapBuffers(window);
                deltaF--;
            }
        }
    }

    private void render() {
        ImGui.setNextWindowSize(400, height);
        ImGui.setNextWindowPos(0, 0);
        ImGui.begin("File explorer", ImGuiWindowFlags.NoMove | ImGuiWindowFlags.NoResize);
            ImGui.textUnformatted("Project root:");
            ImGui.sameLine();
            ImGui.setNextItemWidth(200);
            ImGui.inputText("<-", rootStr);
            ImGui.sameLine();
            if(ImGui.button("open")) {
                rootDirectory = rootStr.get();
                rootFiles = findRootFiles(rootDirectory);
            }
        int fileTex = FileTextures.getType(".dir");
        if(fileTex != -1) {
            ImGui.image(fileTex, 16, 16);
            ImGui.sameLine();
        }
            ImGui.treeNode("root");
            ImGui.treePush();
            uiDirectory(rootFiles);
            ImGui.treePop();
        ImGui.end();
    }

    private void uiDirectory(List<File> files) {
        for(File file : files) {
            if(!file.isDirectory()) {
                int fileTex = FileTextures.getType(file.getName());
                if(fileTex != -1) {
                    ImGui.image(fileTex, 16, 16);
                    ImGui.sameLine();
                }
                ImGui.textUnformatted(file.getName());
            } else {
                int fileTex = FileTextures.getType(".dir");
                if(fileTex != -1) {
                    ImGui.image(fileTex, 16, 16);
                    ImGui.sameLine();
                }
                ImGui.treeNode(file.getName());
                ImGui.treePush();
                if(file.listFiles() != null) {
                    uiDirectory(Arrays.asList(file.listFiles()));
                }
                ImGui.treePop();
            }
        }
    }

    private List<File> findRootFiles(String directory) {
        File layoutDir = new File(directory);
        if (!layoutDir.exists()) {
            layoutDir.mkdirs();
        }
        File[] files = layoutDir.listFiles();
        return Arrays.asList(files);
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}
