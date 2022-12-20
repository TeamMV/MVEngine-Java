package dev.mv.editor;

import dev.mv.LogFileEliminator;
import dev.mv.editor.launcher.EditorLauncher;
import dev.mv.editor.launcher.LaunchConfig;
import dev.mv.editor.launcher.LauncherScreen;
import dev.mv.editor.loading.LoadingManager;
import dev.mv.engine.ApplicationConfig;
import dev.mv.engine.MVEngine;
import dev.mv.engine.gui.components.TextLine;
import dev.mv.engine.gui.theme.Theme;
import dev.mv.engine.render.WindowCreateInfo;
import dev.mv.engine.render.shared.*;
import dev.mv.engine.render.shared.Color;
import dev.mv.engine.render.shared.Window;
import dev.mv.engine.render.shared.create.RenderBuilder;
import dev.mv.engine.render.shared.font.BitmapFont;
import dev.mv.engine.render.shared.models.Entity;
import dev.mv.engine.render.shared.models.Model;
import dev.mv.engine.render.shared.models.ObjectLoader;
import dev.mv.engine.render.shared.shader.light.DirectionalLight;
import dev.mv.engine.render.shared.shader.light.PointLight;
import dev.mv.engine.render.shared.shader.light.SpotLight;
import dev.mv.engine.render.shared.texture.Texture;
import dev.mv.utils.async.Promise;
import dev.mv.utils.misc.Version;
import imgui.ImGui;
import lombok.SneakyThrows;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.system.Configuration;

import java.awt.*;
import java.io.IOException;

import static dev.mv.utils.Utils.await;
import static dev.mv.utils.Utils.sleep;
import static org.lwjgl.glfw.GLFW.*;

public class Main {
    public static final Vector4f DEFAULT_COLOR = new Vector4f(1.0f, 1.0f, 1.0f, 1.0f);

    public static DrawContext2D renderer2D;
    public static DrawContext3D renderer3D;
    static float r = 0;
    private static BitmapFont font;
    private static Entity cruiser;
    private static PointLight pointlight = new PointLight(new Vector3f(0, 2, -2), new Vector3f(1, 1, 0.5f), 1f, 0, 0, 1f);
    private static SpotLight spotlight = new SpotLight(pointlight, new Vector3f(r, 0, 0), (float) Math.toRadians(180));
    private static DirectionalLight directionalLight = new DirectionalLight(new Vector3f(1, 1, 1), new Vector3f(2, 0, 0), 1.0f);

    @SneakyThrows
    public static void main(String[] args) {
        LogFileEliminator.__void__();
        MVEngine.init(new ApplicationConfig()
            .setName("MVEngine")
            .setVersion(Version.parse("v0.1.0"))
            .setRenderingApi(ApplicationConfig.RenderingAPI.OPENGL));

        WindowCreateInfo createInfo = new WindowCreateInfo();
        createInfo.width = 800;
        createInfo.height = 600;
        createInfo.title = "MVEngine";
        createInfo.resizeable = true;
        createInfo.appendFpsToTitle = true;
        createInfo.fpsAppendConfiguration.betweenTitleAndValue = " - ";
        createInfo.fpsAppendConfiguration.afterValue = " frames";
        createInfo.maxFPS = 60;
        createInfo.maxUPS = 30;
        createInfo.fullscreen = false;
        createInfo.decorated = true;

        Window window = MVEngine.createWindow(createInfo);

        Gradient base = new Gradient();
        base.bottomLeft = new Color(255, 255, 255, 255);
        base.topRight = new Color(0, 0, 0, 0);
        base.topLeft = new Color(125, 125, 125, 255);
        base.bottomRight = new Color(125, 125, 125, 255);

        Gradient text = new Gradient();
        text.setTop(new Color(255, 200, 200, 255));
        text.setBottom(new Color(255, 255, 200, 255));

        Theme.Normal normal = new Theme.Normal(
            null,
            new Color(255, 255, 255, 255),
            base,
            null,
            null,
            text
        );

        Theme theme = new Theme(normal, null);

        TextLine line = new TextLine(100, 100);


        window.run(() -> {
            System.out.println(MVEngine.getRenderingApi());
            renderer2D = new DrawContext2D(window);
            try {
                font = new BitmapFont("/fonts/minecraft/minecraft.png", "/fonts/minecraft/minecraft.fnt");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            line.setHeight(64);
            line.setText("Hello World!");
            line.setFont(font);
            renderer3D = new DrawContext3D(window);
            try {
                ObjectLoader loader = MVEngine.getObjectLoader();
                Model mCruiser = loader.loadExternalModel("/models/cruiser/cruiser.obj");
                Texture tCruiser = RenderBuilder.newTexture("/models/cruiser/cruiser.bmp");
                mCruiser.setTexture(tCruiser, 1.0f);
                cruiser = new Entity(mCruiser, new Vector3f(0, 0, -2.5f), new Vector3f(0, 0, 0), 1);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }, null, () -> {
            renderer2D.color(255, 0, 0, 255);
            renderer2D.voidCircle(100, 100, 200, 3, 50f);
            renderer2D.color(255, 255, 0, 255);
            renderer2D.circle(300, 300, 100, 20f);
            renderer2D.color(0, 255, 0, 100);
            renderer2D.circle(350, 250, 75, 20f);
            renderer2D.color(255, 255, 255, 255);
            renderer2D.font(font);
            renderer2D.text(300, 20, 32, "Alpha circles :D");
            renderer2D.color(0, 0, 255, 255);
            renderer2D.arc(500, 300, 50, 90, (int)r, 20);
            renderer2D.arc(500, 300, 50, 90, (int)r + 180, 20);

            r += 5f;
            if(r >= 360) {
                r = 0;
            }
            //line.draw(renderer2D, theme);
            //renderer3D.object(cruiser);
            //renderer3D.processPointLight(pointlight);
            //renderer3D.processSpotLight(spotlight);
            //renderer3D.processDirectionalLight(directionalLight);

            //cruiser.incrementRotation(0.1f, 0.1f, 0.1f);

            Camera camera = window.getCamera();

            if (glfwGetKey(window.getGlfwId(), GLFW_KEY_W) == GLFW_PRESS) {
                camera.move(0.0f, 0.0f, -1f);
            }
            if (glfwGetKey(window.getGlfwId(), GLFW_KEY_A) == GLFW_PRESS) {
                camera.move(-1f, 0.0f, 0.0f);
            }
            if (glfwGetKey(window.getGlfwId(), GLFW_KEY_S) == GLFW_PRESS) {
                camera.move(0.0f, 0.0f, 1f);
            }
            if (glfwGetKey(window.getGlfwId(), GLFW_KEY_D) == GLFW_PRESS) {
                camera.move(1f, 0.0f, 0.0f);
            }

            if (glfwGetKey(window.getGlfwId(), GLFW_KEY_RIGHT) == GLFW_PRESS) {
                camera.rotate(0.0f, 1.0f, 0.0f);
            }
            if (glfwGetKey(window.getGlfwId(), GLFW_KEY_LEFT) == GLFW_PRESS) {
                camera.rotate(0.0f, -1.0f, 0.0f);
            }
            if (glfwGetKey(window.getGlfwId(), GLFW_KEY_UP) == GLFW_PRESS) {
                camera.rotate(-1.0f, 0.0f, 0.0f);
            }
            if (glfwGetKey(window.getGlfwId(), GLFW_KEY_DOWN) == GLFW_PRESS) {
                camera.rotate(1.0f, 0.0f, 0.0f);
            }

            if (glfwGetKey(window.getGlfwId(), GLFW_KEY_SPACE) == GLFW_PRESS) {
                camera.move(0.0f, 0.01f, 0.0f);
            }
            if (glfwGetKey(window.getGlfwId(), GLFW_KEY_LEFT_SHIFT) == GLFW_PRESS) {
                camera.move(0.0f, -0.01f, 0.0f);
            }
        });

        System.exit(0);

        LoadingManager.start("", "/LoadingLogo.png");
        LoadingManager.loadingDots();
        await(sleep(500));
        LoadingManager.stop();
        await(sleep(50)); //We need to wait a little to prevent any problems with multiple windows being open

        LaunchConfig config = new LauncherScreen().run();
        EditorLauncher editor = new EditorLauncher(config);
        editor.launch();

        MVEngine.terminate();
    }
}
