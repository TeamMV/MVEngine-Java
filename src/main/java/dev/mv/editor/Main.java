package dev.mv.editor;

import dev.mv.editor.launcher.EditorLauncher;
import dev.mv.editor.launcher.LaunchConfig;
import dev.mv.editor.launcher.LauncherScreen;
import dev.mv.editor.loading.LoadingManager;
import dev.mv.engine.ApplicationConfig;
import dev.mv.engine.MVEngine;
import dev.mv.engine.render.Window;
import dev.mv.engine.render.drawables.text.BitmapFont;
import dev.mv.engine.render.light.DirectionalLight;
import dev.mv.engine.render.light.PointLight;
import dev.mv.engine.render.light.SpotLight;
import dev.mv.engine.render.models.Entity;
import dev.mv.engine.render.opengl._3d.render.OpenGLRender3D;
import dev.mv.mvln.lexer.Lexer;
import dev.mv.utils.misc.Version;
import org.joml.Vector3f;

import static dev.mv.engine.MVEngine.usesVulkan;
import static dev.mv.utils.Utils.await;
import static dev.mv.utils.Utils.sleep;

public class Main {

    public static OpenGLRender3D renderer;
    static float r = 0;
    private static BitmapFont font;
    private static Entity cruiser;
    private static Entity plane;
    private static PointLight pointlight = new PointLight(new Vector3f(0, 2, -2), new Vector3f(1, 1, 0.5f), 1f, 0, 0, 1f);
    private static SpotLight spotlight = new SpotLight(pointlight, new Vector3f(r, 0, 0), (float) Math.toRadians(180));
    private static DirectionalLight directionalLight = new DirectionalLight(new Vector3f(1, 1, 1), new Vector3f(2, 0, 0), 1.0f);

    public static void main(String[] args) {
        MVEngine.init(new ApplicationConfig().setName("MVEngine").setVersion(Version.parse("v0.1.0")).setVulkan(true));
        System.out.println(usesVulkan());

        //Window window = MVEngine.createWindow(800, 600, "MVEngine", true);
        //window.run(() -> {
//
        //}, () -> {
//
        //}, () -> {
//
        //});

        System.out.println(usesVulkan());

        System.out.println("--------------------------------");
        Lexer.tokenize("/home/v22/Schreibtisch/coding/java/MVEngine/src/main/resources/mvln-files/test.mvln").stream().forEach(t -> System.out.println(t.getType() + ":" + t.getValue()));

        /*
        Window window = MVEngine.createWindow(1000, 700, "MVEngine", true);

        window.run(() -> {
            renderer = new OpenGLRender3D(window);
            try {
                ObjectLoader loader = MVEngine.getObjectLoader();
                Model mCruiser = loader.loadExternalModel("src/main/resources/models/cruiser/cruiser.obj");
                Model mPlane = loader.loadExternalModel("src/main/resources/models/f16/f16.obj");
                Texture tCruiser = MVEngine.createTexture("src/main/resources/models/cruiser/cruiser.bmp");
                Texture tPLane = MVEngine.createTexture("src/main/resources/models/f16/F-16.bmp");
                Material material = new Material();
                material.setReflectance(1f);
                material.setAmbientColor(RenderConstansts.DEFAULT_COLOR);
                material.setTexture(tPLane);
                mCruiser.setTexture(tCruiser, 1.0f);
                mPlane.setMaterial(material);
                cruiser = new Entity(mCruiser, new Vector3f(0, 0, -2.5f), new Vector3f(0, 0, 0), 1);
                plane = new Entity(mPlane, new Vector3f(2, 0, -2.5f), new Vector3f(0, 0, 0), 1);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }, null, () -> {
            renderer.processEntity(cruiser);
            renderer.processEntity(plane);
            renderer.processPointLight(pointlight);
            renderer.processSpotLight(spotlight);
            renderer.processDirectionalLight(directionalLight);
            renderer.render();

            r -= 0.1f;
            if(r <= 0) {
                r = 360f;
            }

            //cruiser.incrementRotation(0.1f, 0.1f, 0.1f);

            OpenGLCamera3D camera = window.getDrawContext3D().getCamera();

            if (glfwGetKey(window.getGlfwId(), GLFW_KEY_W) == GLFW_PRESS) {
                camera.move(0.0f, 0.0f, -0.01f);
            }
            if (glfwGetKey(window.getGlfwId(), GLFW_KEY_A) == GLFW_PRESS) {
                camera.move(-0.01f, 0.0f, 0.0f);
            }
            if (glfwGetKey(window.getGlfwId(), GLFW_KEY_S) == GLFW_PRESS) {
                camera.move(0.0f, 0.0f, 0.01f);
            }
            if (glfwGetKey(window.getGlfwId(), GLFW_KEY_D) == GLFW_PRESS) {
                camera.move(0.01f, 0.0f, 0.0f);
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
        */

        System.exit(0);

        LoadingManager.start("", "/LoadingLogo.png");
        LoadingManager.loadingDots();
        await(sleep(2400));
        LoadingManager.stop();
        await(sleep(500)); //We need to wait a little to prevent any problems with multiple windows being open

        LaunchConfig config = new LauncherScreen().run();
        EditorLauncher editor = new EditorLauncher(config);
        editor.launch();

        MVEngine.terminate();
    }
}
