package dev.mv.editor.launcher;

import dev.mv.editor.general.FileOpenDialogue;
import dev.mv.editor.general.FileTextures;
import dev.mv.utils.Utils;
import dev.mv.utils.async.Promise;
import imgui.*;
import imgui.flag.ImGuiCol;
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

        initUi();

        loop();

        glImpl.dispose();
        glfwImpl.dispose();

        glfwDestroyWindow(window);

        return config;
    }

    private void initUi() {
        float[][] colors = ImGui.getStyle().getColors();
        colors[ImGuiCol.Text]                   = new float[]{1.00f, 1.00f, 1.00f, 1.00f};
        colors[ImGuiCol.TextDisabled]           = new float[]{0.50f, 0.50f, 0.50f, 1.00f};
        colors[ImGuiCol.WindowBg]               = new float[]{0.10f, 0.10f, 0.10f, 1.00f};
        colors[ImGuiCol.ChildBg]                = new float[]{0.00f, 0.00f, 0.00f, 0.00f};
        colors[ImGuiCol.PopupBg]                = new float[]{0.19f, 0.19f, 0.19f, 0.92f};
        colors[ImGuiCol.Border]                 = new float[]{0.19f, 0.19f, 0.19f, 0.29f};
        colors[ImGuiCol.BorderShadow]           = new float[]{0.00f, 0.00f, 0.00f, 0.24f};
        colors[ImGuiCol.FrameBg]                = new float[]{0.05f, 0.05f, 0.05f, 0.54f};
        colors[ImGuiCol.FrameBgHovered]         = new float[]{0.19f, 0.19f, 0.19f, 0.54f};
        colors[ImGuiCol.FrameBgActive]          = new float[]{0.20f, 0.22f, 0.23f, 1.00f};
        colors[ImGuiCol.TitleBg]                = new float[]{0.00f, 0.00f, 0.00f, 1.00f};
        colors[ImGuiCol.TitleBgActive]          = new float[]{0.06f, 0.06f, 0.06f, 1.00f};
        colors[ImGuiCol.TitleBgCollapsed]       = new float[]{0.00f, 0.00f, 0.00f, 1.00f};
        colors[ImGuiCol.MenuBarBg]              = new float[]{0.14f, 0.14f, 0.14f, 1.00f};
        colors[ImGuiCol.ScrollbarBg]            = new float[]{0.05f, 0.05f, 0.05f, 0.54f};
        colors[ImGuiCol.ScrollbarGrab]          = new float[]{0.34f, 0.34f, 0.34f, 0.54f};
        colors[ImGuiCol.ScrollbarGrabHovered]   = new float[]{0.40f, 0.40f, 0.40f, 0.54f};
        colors[ImGuiCol.ScrollbarGrabActive]    = new float[]{0.56f, 0.56f, 0.56f, 0.54f};
        colors[ImGuiCol.CheckMark]              = new float[]{0.33f, 0.67f, 0.86f, 1.00f};
        colors[ImGuiCol.SliderGrab]             = new float[]{0.34f, 0.34f, 0.34f, 0.54f};
        colors[ImGuiCol.SliderGrabActive]       = new float[]{0.56f, 0.56f, 0.56f, 0.54f};
        colors[ImGuiCol.Button]                 = new float[]{0.05f, 0.05f, 0.05f, 0.54f};
        colors[ImGuiCol.ButtonHovered]          = new float[]{0.19f, 0.19f, 0.19f, 0.54f};
        colors[ImGuiCol.ButtonActive]           = new float[]{0.20f, 0.22f, 0.23f, 1.00f};
        colors[ImGuiCol.Header]                 = new float[]{0.00f, 0.00f, 0.00f, 0.52f};
        colors[ImGuiCol.HeaderHovered]          = new float[]{0.00f, 0.00f, 0.00f, 0.36f};
        colors[ImGuiCol.HeaderActive]           = new float[]{0.20f, 0.22f, 0.23f, 0.33f};
        colors[ImGuiCol.Separator]              = new float[]{0.28f, 0.28f, 0.28f, 0.29f};
        colors[ImGuiCol.SeparatorHovered]       = new float[]{0.44f, 0.44f, 0.44f, 0.29f};
        colors[ImGuiCol.SeparatorActive]        = new float[]{0.40f, 0.44f, 0.47f, 1.00f};
        colors[ImGuiCol.ResizeGrip]             = new float[]{0.28f, 0.28f, 0.28f, 0.29f};
        colors[ImGuiCol.ResizeGripHovered]      = new float[]{0.44f, 0.44f, 0.44f, 0.29f};
        colors[ImGuiCol.ResizeGripActive]       = new float[]{0.40f, 0.44f, 0.47f, 1.00f};
        colors[ImGuiCol.Tab]                    = new float[]{0.00f, 0.00f, 0.00f, 0.52f};
        colors[ImGuiCol.TabHovered]             = new float[]{0.14f, 0.14f, 0.14f, 1.00f};
        colors[ImGuiCol.TabActive]              = new float[]{0.20f, 0.20f, 0.20f, 0.36f};
        colors[ImGuiCol.TabUnfocused]           = new float[]{0.00f, 0.00f, 0.00f, 0.52f};
        colors[ImGuiCol.TabUnfocusedActive]     = new float[]{0.14f, 0.14f, 0.14f, 1.00f};
        colors[ImGuiCol.DockingPreview]         = new float[]{0.33f, 0.67f, 0.86f, 1.00f};
        colors[ImGuiCol.DockingEmptyBg]         = new float[]{1.00f, 0.00f, 0.00f, 1.00f};
        colors[ImGuiCol.PlotLines]              = new float[]{1.00f, 0.00f, 0.00f, 1.00f};
        colors[ImGuiCol.PlotLinesHovered]       = new float[]{1.00f, 0.00f, 0.00f, 1.00f};
        colors[ImGuiCol.PlotHistogram]          = new float[]{1.00f, 0.00f, 0.00f, 1.00f};
        colors[ImGuiCol.PlotHistogramHovered]   = new float[]{1.00f, 0.00f, 0.00f, 1.00f};
        colors[ImGuiCol.TableHeaderBg]          = new float[]{0.00f, 0.00f, 0.00f, 0.52f};
        colors[ImGuiCol.TableBorderStrong]      = new float[]{0.00f, 0.00f, 0.00f, 0.52f};
        colors[ImGuiCol.TableBorderLight]       = new float[]{0.28f, 0.28f, 0.28f, 0.29f};
        colors[ImGuiCol.TableRowBg]             = new float[]{0.00f, 0.00f, 0.00f, 0.00f};
        colors[ImGuiCol.TableRowBgAlt]          = new float[]{1.00f, 1.00f, 1.00f, 0.06f};
        colors[ImGuiCol.TextSelectedBg]         = new float[]{0.20f, 0.22f, 0.23f, 1.00f};
        colors[ImGuiCol.DragDropTarget]         = new float[]{0.33f, 0.67f, 0.86f, 1.00f};
        colors[ImGuiCol.NavHighlight]           = new float[]{1.00f, 0.00f, 0.00f, 1.00f};
        colors[ImGuiCol.NavWindowingHighlight]  = new float[]{1.00f, 0.00f, 0.00f, 0.70f};
        colors[ImGuiCol.NavWindowingDimBg]      = new float[]{1.00f, 0.00f, 0.00f, 0.20f};
        colors[ImGuiCol.ModalWindowDimBg]       = new float[]{1.00f, 0.00f, 0.00f, 0.35f};

        ImGui.getStyle().setColors(colors);

        ImGuiStyle style = ImGui.getStyle();
        style.setWindowPadding(8.00f, 8.00f);
        style.setFramePadding(5.00f, 2.00f);
        style.setCellPadding(6.00f, 6.00f);
        style.setItemSpacing(6.00f, 6.00f);
        style.setItemInnerSpacing(6.00f, 6.00f);
        style.setTouchExtraPadding(0.00f, 0.00f);
        style.setIndentSpacing(25);
        style.setScrollbarSize(15);
        style.setGrabMinSize(10);
        style.setWindowBorderSize(1);
        style.setChildBorderSize(1);
        style.setPopupBorderSize(1);
        style.setFrameBorderSize(1);
        style.setTabBorderSize(1);
        style.setWindowRounding(7);
        style.setChildRounding(4);
        style.setFrameRounding(3);
        style.setPopupRounding(4);
        style.setScrollbarRounding(9);
        style.setGrabRounding(3);
        style.setLogSliderDeadzone(4);
        style.setTabRounding(4);

        //ImGuiIO io = ImGui.getIO();
        //ImFont vigaFont = io.getFonts().addFontFromFileTTF("src/main/resources/fonts/Viga-Regular.ttf", 16);
        //io.getFonts().build();

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

        glfwMakeContextCurrent(window);

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
        FileOpenDialogue.open("/home", FileOpenDialogue.Target.FILE);
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
