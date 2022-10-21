package dev.mv.editor;

import dev.mv.editor.launcher.EditorLauncher;
import dev.mv.editor.launcher.LaunchConfig;
import dev.mv.editor.launcher.LauncherScreen;
import dev.mv.editor.loading.LoadingManager;
import imgui.ImGui;
import org.lwjgl.glfw.GLFWErrorCallback;

import static dev.mv.utils.Utils.*;
import static org.lwjgl.glfw.GLFW.*;

public class Main {

    public static void main(String[] args) {
        if (!glfwInit()) {
            System.err.println("Could not initialise GLFW!");
            System.exit(1);
            return;
        }
        GLFWErrorCallback.createPrint(System.err).set();
        ImGui.createContext();
        ImGui.styleColorsDark();

        LoadingManager.start("", "/LoadingLogo.png");
        LoadingManager.loadingDots();
        await(sleep(4800));
        LoadingManager.stop();
        await(sleep(500)); //We need to wait a little to prevent any problems with multiple windows being open

        LaunchConfig config = new LauncherScreen().run();
        EditorLauncher editor = new EditorLauncher(config);
        editor.launch();

        ImGui.destroyContext();
        glfwTerminate();
    }

}
