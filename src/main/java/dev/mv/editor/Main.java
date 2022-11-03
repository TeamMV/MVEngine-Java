package dev.mv.editor;

import dev.mv.editor.launcher.EditorLauncher;
import dev.mv.editor.launcher.LaunchConfig;
import dev.mv.editor.launcher.LauncherScreen;
import dev.mv.editor.loading.LoadingManager;
import dev.mv.engine.MVEngine;
import dev.mv.engine.render.DrawContext2D;
import dev.mv.engine.render.Window;
import dev.mv.engine.render.drawables.Texture;
import dev.mv.engine.render.drawables.text.BitmapFont;
import dev.mv.engine.render.models.Entity;
import dev.mv.engine.render.models.Model;
import dev.mv.engine.render.models.ObjectLoader;
import dev.mv.engine.render.opengl._3d.render.OpenGLRender3D;
import dev.mv.utils.Utils;
import imgui.ImGui;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFWErrorCallback;

import java.io.IOException;

import static dev.mv.utils.Utils.*;
import static org.lwjgl.glfw.GLFW.*;

public class Main {

    private static BitmapFont font;
    private static Entity cruiser;
    public static OpenGLRender3D renderer;

    public static void main(String[] args) {
        if (!glfwInit()) {
            System.err.println("Could not initialise GLFW!");
            System.exit(1);
            return;
        }

        Window window = MVEngine.createWindow(1000, 700, "MVEngine", true);

        window.run(() -> {
            renderer = new OpenGLRender3D(window);
            try {
                ObjectLoader loader = MVEngine.getObjectLoader();
                Model mCruiser = loader.loadExternalModel("src/main/resources/models/cruiser/cruiser.obj");
                Texture cruiserTexture = MVEngine.createTexture("src/main/resources/models/cruiser/cruiser.png");
                mCruiser.setTexture(cruiserTexture);
                cruiser = new Entity(mCruiser, new Vector3f(0, 0, -2.5f), new Vector3f(0, 0, 0), 1);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }, null, () -> {
            renderer.processEntity(cruiser);
            renderer.render();

            cruiser.incrementRotation(0.0f, 0.0f, 0.0f);
        });

        GLFWErrorCallback.createPrint(System.err).set();
        ImGui.createContext();
        ImGui.styleColorsDark();

        LoadingManager.start("", "/LoadingLogo.png");
        LoadingManager.loadingDots();
        await(sleep(2400));
        LoadingManager.stop();
        await(sleep(500)); //We need to wait a little to prevent any problems with multiple windows being open

        LaunchConfig config = new LauncherScreen().run();
        EditorLauncher editor = new EditorLauncher(config);
        editor.launch();

        ImGui.destroyContext();
        glfwTerminate();
    }
}
