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

import static dev.mv.utils.Utils.await;
import static dev.mv.utils.Utils.sleep;
import static org.lwjgl.glfw.GLFW.glfwInit;
import static org.lwjgl.glfw.GLFW.glfwTerminate;

public class Main {

    private static BitmapFont font;
    private static Entity cruiser;
    private static OpenGLRender3D renderer;
    private static Model mCruiser;

    public static void main(String[] args) {
        if (!glfwInit()) {
            System.err.println("Could not initialise GLFW!");
            System.exit(1);
            return;
        }

        Window window = MVEngine.createWindow(800, 600, "MVEngine", true);

        window.run(() -> {
            font = MVEngine.createFont(Utils.getInnerPath("fonts", "minecraft", "minecraft.png"), Utils.getInnerPath("fonts", "minecraft", "minecraft.fnt"));
            renderer = new OpenGLRender3D();
            try {
                ObjectLoader loader = MVEngine.getObjectLoader();
                //Model mCruiser = loader.loadExternalModel("src/main/resources/models/cruiser/cruiser.obj");
                mCruiser = loader.loadModel(new float[] {-0.5f, 0.5f, 0f,
                    -0.5f, -0.5f, 0f,
                    0.5f, -0.5f, 0f,
                    0.5f, -0.5f, 0f,
                    0.5f, 0.5f, 0f,
                    -0.5f, 0.5f, 0f});
                Texture cruiserTexture = MVEngine.createTexture("src/main/resources/models/cruiser/cruiser.bmp");
                mCruiser.setTexture(cruiserTexture);
                cruiser = new Entity(mCruiser, new Vector3f(0, 0, -1), new Vector3f(0, 0, 0), 1);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }, null, () -> {
            //renderer.processEntity(cruiser);
            renderer.render(mCruiser);
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
