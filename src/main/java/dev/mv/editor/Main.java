package dev.mv.editor;

import dev.mv.editor.loading.LoadingManager;

import static dev.mv.utils.Utils.*;
import static org.lwjgl.glfw.GLFW.*;

public class Main {

    public static void main(String[] args) {
        if (!glfwInit()) {
            System.err.println("Could not initialise GLFW!");
            System.exit(1);
            return;
        }
        LoadingManager.start("", "/LoadingLogo.png");
        LoadingManager.loadingDots();
        await(sleep(2400));
        LoadingManager.stop();
        glfwTerminate();
    }

}
