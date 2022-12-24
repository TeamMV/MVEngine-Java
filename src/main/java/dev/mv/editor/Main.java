package dev.mv.editor;

import dev.mv.LogFileEliminator;
import dev.mv.editor.launcher.EditorLauncher;
import dev.mv.editor.launcher.LaunchConfig;
import dev.mv.editor.launcher.LauncherScreen;
import dev.mv.editor.loading.LoadingManager;
import dev.mv.engine.ApplicationConfig;
import dev.mv.engine.MVEngine;
import dev.mv.engine.gui.Gui;
import dev.mv.engine.gui.GuiRegistry;
import dev.mv.engine.gui.components.*;
import dev.mv.engine.gui.components.animations.ElementAnimation;
import dev.mv.engine.gui.components.assets.GuiAssets;
import dev.mv.engine.gui.components.layouts.VerticalLayout;
import dev.mv.engine.gui.event.ClickListener;
import dev.mv.engine.gui.theme.Theme;
import dev.mv.engine.input.Input;
import dev.mv.engine.input.InputCollector;
import dev.mv.engine.input.InputProcessor;
import dev.mv.engine.render.WindowCreateInfo;
import dev.mv.engine.render.shared.*;
import dev.mv.engine.render.shared.create.RenderBuilder;
import dev.mv.engine.render.shared.font.BitmapFont;
import dev.mv.engine.render.shared.models.Entity;
import dev.mv.engine.render.shared.models.Model;
import dev.mv.engine.render.shared.models.ObjectLoader;
import dev.mv.engine.render.shared.shader.light.DirectionalLight;
import dev.mv.engine.render.shared.shader.light.PointLight;
import dev.mv.engine.render.shared.shader.light.SpotLight;
import dev.mv.engine.render.shared.texture.Texture;
import dev.mv.utils.misc.Version;
import lombok.SneakyThrows;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.io.IOException;

import static dev.mv.utils.Utils.await;
import static dev.mv.utils.Utils.sleep;
import static org.lwjgl.glfw.GLFW.*;

public class Main {
    public static final Vector4f DEFAULT_COLOR = new Vector4f(1.0f, 1.0f, 1.0f, 1.0f);

    public static DrawContext2D renderer2D;
    public static DrawContext3D renderer3D;
    static float r = 0;
    private static Texture texture;
    private static GuiRegistry guiRegistry = new GuiRegistry();
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
        createInfo.width = 1000;
        createInfo.height = 800;
        createInfo.title = "MVEngine";
        createInfo.resizeable = true;
        createInfo.maxFPS = 60;
        createInfo.maxUPS = 30;
        createInfo.fullscreen = false;
        createInfo.decorated = true;

        Window window = MVEngine.createWindow(createInfo);

        Gradient text = new Gradient();
        text.setTop(new Color(255, 255, 255, 255));
        text.setBottom(new Color(255, 255, 255, 255));

        Theme theme = new Theme();
        theme.setBaseColor(new Color(47, 169, 235, 255));
        theme.setOutlineColor(new Color(255, 255, 255, 255));
        theme.setText_base(new Color(255, 255, 255, 255));
        theme.setDisabledBaseColor(new Color(100, 100, 100, 255));
        theme.setDisabledOutlineColor(new Color(70, 70, 70, 255));
        theme.setDisabledTextColor(new Color(70, 70, 70, 255));
        theme.setGuiAssetPath("/gui/assets/guiassets.png");
        theme.setGuiAssetsIconWidth(32);
        theme.setGuiAssetsIconHeight(32);
        theme.setHasOutline(true);
        theme.setEdgeStyle(Theme.EdgeStyle.ROUND);
        theme.setEdgeRadius(10);
        theme.setOutlineThickness(1);
        theme.setAnimationFrames(5);
        theme.setAnimationInTime(50);
        theme.setAnimationOutTime(50);
        theme.setButtonAnimator(new ElementAnimation() {
            @Override
            public AnimationState transform(int frame, int totalFrames, AnimationState lastState) {
                lastState.baseColor.setAlpha(lastState.baseColor.getAlpha() - 20);
                lastState.outlineColor.setAlpha(lastState.outlineColor.getAlpha() - 20);

                lastState.width -= 4;
                lastState.height -= 2;
                lastState.posX += 2;
                lastState.posY += 1;
                return lastState;
            }

            @Override
            public AnimationState transformBack(int frame, int totalFrames, AnimationState lastState) {
                lastState.baseColor.setAlpha(lastState.baseColor.getAlpha() + 20);
                lastState.outlineColor.setAlpha(lastState.outlineColor.getAlpha() + 20);

                lastState.width += 4;
                lastState.height += 2;
                lastState.posX -= 2;
                lastState.posY -= 1;
                return lastState;
            }
        });

        TextLine line = new TextLine(window, 100, 100, 100);
        ImageButton button1 = new ImageButton(window, 100, 300, 120, 60);
        Button button2 = new Button(window, 100, 150, 120, 60);
        Checkbox checkbox = new Checkbox(window, 100, 225, 60, 60);

        VerticalLayout layout = new VerticalLayout(window, 200, 500);

        window.run(() -> {
            System.out.println(MVEngine.getRenderingApi());

            InputProcessor processor = new InputProcessor();
            new InputCollector(processor, window).start();
            Input.init();

            renderer2D = new DrawContext2D(window);
            try {
                font = new BitmapFont("/fonts/FreeSans/FreeSans.png", "/fonts/FreeSans/FreeSans.fnt");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            renderer3D = new DrawContext3D(window);

            try {
                texture = RenderBuilder.newTexture("/LoadingLogo.png");

                ObjectLoader loader = MVEngine.getObjectLoader();
                Model mCruiser = loader.loadExternalModel("/models/cruiser/cruiser.obj");
                Texture tCruiser = RenderBuilder.newTexture("/models/cruiser/cruiser.bmp");
                mCruiser.setTexture(tCruiser, 1.0f);
                cruiser = new Entity(mCruiser, new Vector3f(0, 0, -2.5f), new Vector3f(0, 0, 0), 1);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            //Gui test
            theme.setFont(font);

            line.setHeight(64);
            line.setFont(font);
            line.setText("Hello World!");

            button1.setTexture(texture);

            button2.setFont(font);
            button2.setText("Click me!");
            button2.attachListener(new ClickListener() {
                @Override
                public void onCLick(Element element, int button) {

                }

                @Override
                public void onRelease(Element element, int button) {
                    if(button == Input.BUTTON_LEFT) {
                        button1.toggle();
                    }
                }
            });

            layout.addElement(button1);
            layout.addElement(checkbox);
            layout.addElement(button2);
            layout.alignContent(VerticalLayout.Align.RIGHT);
            layout.setSpacing(5);

            Gui gui = new Gui(renderer2D, "test");
            gui.addElement(layout);

            try {
                gui.applyTheme(theme);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            guiRegistry.addGui(gui);

        }, null, () -> {

            r += 1f;
            if(r >= 100) {
                r = 0;
            }

            //layout.setX(Input.mouse[Input.MOUSE_X]);
            //layout.setY(Input.mouse[Input.MOUSE_Y]);

            guiRegistry.renderGuis();

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
