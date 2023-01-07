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
import dev.mv.engine.gui.components.layouts.*;
import dev.mv.engine.gui.event.ClickListener;
import dev.mv.engine.gui.event.ProgressListener;
import dev.mv.engine.gui.parsing.theme.ThemeParser;
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
import dev.mv.engine.resources.R;
import dev.mv.utils.async.PromiseNull;
import dev.mv.utils.misc.Version;
import lombok.SneakyThrows;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.io.File;
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
        createInfo.width = 1200;
        createInfo.height = 1000;
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

        //Theme theme = new Theme();
        //theme.setBaseColor(new Color(125, 122, 100, 255));
        //theme.setOutlineColor(new Color(200, 200, 200, 255));
        //theme.setText_base(new Color(255, 255, 255, 255));
        //theme.setExtraColor(new Color(255, 217, 0, 255));
        //theme.setDisabledBaseColor(new Color(82, 81, 81, 255));
        //theme.setDisabledOutlineColor(new Color(100, 100, 100, 255));
        //theme.setDisabledTextColor(new Color(59, 59, 57, 255));
        //theme.setDiabledExtraColor(new Color(100, 50, 0, 255));
        //theme.setShouldChoiceUseTextColor(true);
        //theme.setShouldCheckboxUseTextColor(true);
        //theme.setShouldPasswordInputBoxButtonUseTextColor(true);
        //theme.setGuiAssetPath("/gui/assets/guiassets.png");
        //theme.setGuiAssetsIconWidth(32);
        //theme.setGuiAssetsIconHeight(32);
        //theme.setHasOutline(true);
        //theme.setEdgeStyle(Theme.EdgeStyle.ROUND);
        //theme.setEdgeRadius(10);
        //theme.setOutlineThickness(3);
        //theme.setAnimationFrames(10);
        //theme.setAnimationInTime(50);
        //theme.setAnimationOutTime(50);
        //theme.setAnimator(new ElementAnimation() {
        //    @Override
        //    public AnimationState transform(int frame, int totalFrames, AnimationState lastState) {
        //        lastState.baseColor.setAlpha(lastState.baseColor.getAlpha() - 20);
        //        lastState.outlineColor.setAlpha(lastState.outlineColor.getAlpha() - 20);
//
        //        lastState.width -= 4;
        //        lastState.height -= 2;
        //        lastState.posX += 2;
        //        lastState.posY += 1;
        //        lastState.rotation += 2;
        //        return lastState;
        //    }
//
        //    @Override
        //    public AnimationState transformBack(int frame, int totalFrames, AnimationState lastState) {
        //        lastState.baseColor.setAlpha(lastState.baseColor.getAlpha() + 20);
        //        lastState.outlineColor.setAlpha(lastState.outlineColor.getAlpha() + 20);
//
        //        lastState.width += 4;
        //        lastState.height += 2;
        //        lastState.posX -= 2;
        //        lastState.posY -= 1;
        //        lastState.rotation -= 2;
        //        return lastState;
        //    }
        //});

        ThemeParser parser = new ThemeParser();

        Theme theme = parser.parse(new File("./src/main/resources/gui/themes/test.xml"));
        theme.setShouldCheckboxUseTextColor(true);
        theme.setShouldChoiceUseTextColor(true);
        theme.setShouldPasswordInputBoxButtonUseTextColor(true);

        Space space = new Space(window, 0, 0, 0, 10);

        ImageButton button1 = new ImageButton(window, 100, 300, 120, 60);
        Button button2 = new Button(window, 100, 150, 120, 60);
        Checkbox checkbox = new Checkbox(window, 100, 225, 60, 60);
        InputBox inputBox = new InputBox(window, 100, 25, 400, 60);
        Separator separator = new Separator(window, 0, 0, 400, 1);
        inputBox.setPlaceholderText("E-Mail address");
        PasswordInputBox passwordInputBox = new PasswordInputBox(window, 100, 0, 400, 60);
        passwordInputBox.setPlaceholderText("Password");

        CollapseMenu collapseMenu = new CollapseMenu(window, 60, 60, null);
        collapseMenu.setText("Options");
        Checkbox checkbox1 = new Checkbox(window, collapseMenu, 60, 60);
        checkbox1.setText("check1");
        collapseMenu.addElement(checkbox1);
        Checkbox checkbox2 = new Checkbox(window, collapseMenu, 60, 60);
        checkbox2.setText("check1");
        collapseMenu.addElement(checkbox2);
        Checkbox checkbox3 = new Checkbox(window, collapseMenu, 60, 60);
        checkbox3.setText("check1");
        collapseMenu.addElement(checkbox3);

        HorizontalLayout horizontalLayout = new HorizontalLayout(window, -1, -1);
        horizontalLayout.addElement(new Checkbox(window, 100, 225, 60, 60));
        horizontalLayout.addElement(new Checkbox(window, 100, 225, 60, 60));
        horizontalLayout.addElement(new Checkbox(window, 100, 225, 60, 60));
        horizontalLayout.addElement(new Checkbox(window, 100, 225, 60, 60));
        horizontalLayout.alignContent(HorizontalLayout.Align.CENTER);
        horizontalLayout.setSpacing(5);
        horizontalLayout.setPadding(5, 5, 5, 5);
        horizontalLayout.showFrame();

        ProgressBar progressBar = new ProgressBar(window, 100, 100, 200, 60);
        ChoiceGroup choiceGroup = new ChoiceGroup(window, null);
        Choice choice1 = new Choice(window, null, 60, 60);
        Choice choice2 = new Choice(window, null, 60, 60);
        Choice choice3 = new Choice(window, null, 60, 60);
        choiceGroup.addChoice(choice1);
        choiceGroup.addChoice(choice2);
        choiceGroup.addChoice(choice3);

        VerticalLayout layout = new VerticalLayout(window, 200, 100);

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

            button1.setTexture(texture);

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

            button2.attachListener(new ClickListener() {
                @Override
                public void onCLick(Element element, int button) {

                }

                @Override
                public void onRelease(Element element, int button) {
                    progressBar.incrementByPercentage(1);
                }
            });

            inputBox.setLimit(32);

            passwordInputBox.setLimit(32);

            progressBar.setTotalValue(100);
            progressBar.setCurrentValue(10);
            progressBar.attachListener(new ProgressListener() {
                @Override
                public void onIncrement(Element e, int currentValue, int totalValue, int percentage) {
                    if(currentValue >= totalValue) progressBar.setCurrentValue(1);
                }

                @Override
                public void onDecrement(Element e,int currentValue, int totalValue, int percentage) {

                }
            });

            new PromiseNull((res, rej) -> {
                while(true) {
                    progressBar.incrementByPercentage(1);
                    await(sleep(100));
                }
            });

            checkbox.setText("Toggle");

            choice1.setText("Choice1");
            choice2.setText("Choice2");
            choice3.setText("Choice3");

            layout.addElement(button1);
            layout.addElement(checkbox);
            layout.addElement(button2);
            layout.addElement(space);
            layout.addElement(separator);
            layout.addElement(inputBox);
            layout.addElement(passwordInputBox);
            layout.addElement(collapseMenu);
            layout.addElement(separator);
            layout.addElement(space);
            layout.addElement(horizontalLayout);
            layout.addElement(progressBar);
            layout.addElement(choiceGroup);
            layout.alignContent(VerticalLayout.Align.LEFT);
            layout.setSpacing(5);
            layout.setPadding(10, 10, 10, 10);
            //layout.showFrame();

            Gui gui = new Gui(renderer2D, window, "test");
            gui.addElement(layout);

            button1.setId("myButton");
            button1.addTag("tag1");
            button1.addTag("tag2");
            button1.addTag("tag3");
            System.out.println(button1.getGui().toString());
            button2.getGui().getRoot().enable();

            try {
                gui.applyTheme(theme);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            guiRegistry.addGui(gui);

            R.GUIS = guiRegistry;

        }, null, () -> {

            r += 1f;
            if(r >= 100) {
                r = 0;
            }

            //layout.setX(Input.mouse[Input.MOUSE_X]);
            //layout.setY(Input.mouse[Input.MOUSE_Y]);

            R.GUIS.renderGuis();

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
