package dev.mv.engine.test;

import dev.mv.engine.ApplicationLoop;
import dev.mv.engine.MVEngine;
import dev.mv.engine.files.Directory;
import dev.mv.engine.files.FileManager;
import dev.mv.engine.gui.GuiRegistry;
import dev.mv.engine.gui.components.Button;
import dev.mv.engine.gui.components.Element;
import dev.mv.engine.gui.components.FreeSlider;
import dev.mv.engine.gui.components.extras.Text;
import dev.mv.engine.gui.components.layouts.UpdateSection;
import dev.mv.engine.gui.components.layouts.VerticalLayout;
import dev.mv.engine.gui.event.ProgressListener;
import dev.mv.engine.gui.pages.Page;
import dev.mv.engine.gui.parsing.GuiConfig;
import dev.mv.engine.gui.screens.Pager;
import dev.mv.engine.gui.screens.transitions.LinearShiftTransition;
import dev.mv.engine.gui.theme.Theme;
import dev.mv.engine.render.shared.*;
import dev.mv.engine.render.shared.graphics.CircularParticleSystem;
import dev.mv.engine.render.shared.graphics.ParticleSystem;
import dev.mv.engine.render.shared.models.ObjectLoader;
import dev.mv.engine.resources.R;
import dev.mv.engine.resources.ResourceLoader;
import dev.mv.utils.generic.pair.Pair;

import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.CountDownLatch;

public class Test implements ApplicationLoop {

    public static final Test INSTANCE = new Test();

    private DrawContext2D ctx2D;
    private DrawContext3D ctx3D;
    private Camera camera;
    private DefaultCameraController cameraController;
    private ObjectLoader objectLoader;
    private Directory gameDirectory;
    private CircularParticleSystem particleSystem;
    FreeSlider dir;
    FreeSlider range;
    FreeSlider speed;
    FreeSlider hue;

    private Test() {}

    @Override
    public void start(MVEngine engine, Window window) {
        gameDirectory = FileManager.getDirectory("factoryisland");
        ctx2D = new DrawContext2D(window);
        ctx3D = new DrawContext3D(window);
        camera = window.getCamera();
        camera.setSpeed(0.2f);
        cameraController = new DefaultCameraController(camera);
        objectLoader = engine.getObjectLoader();

        try {
            ResourceLoader.markTheme("defaultTheme", "testTheme.xml");
            ResourceLoader.markFont("defaultFont", "/assets/mvengine/defaultfont.png", "/assets/mvengine/defaultfont.fnt");
            ResourceLoader.markPage("main", "main.xml");
            ResourceLoader.markLayout("test", "testLayout.xml");
            ResourceLoader.markLayout("quit", "quit.xml");
            ResourceLoader.markLayout("particle", "particle.xml");
            ResourceLoader.load(engine, new GuiConfig("/gui/guiConfig.xml"));
            Page main = R.pages.get("main");
            GuiRegistry registry = main.getRegistry();
            registry.applyRenderer(ctx2D);
            Pager pager = main.getPager();
            pager.map(new HashMap<>(){
                {
                    put("myGui", new Pair<>(new LinearShiftTransition(0, 0), 0.5f));
                }
            });
            pager.open("particle");
            Theme theme = R.themes.get("defaultTheme");
            theme.setFont(R.fonts.get("defaultFont"));
            registry.applyTheme(theme);

            registry.findGui("myGui").getRoot().findElementsBySuperType(Text.class).forEach(t -> t.setUseChroma(true));

            particleSystem = new CircularParticleSystem(300, 500, 100, ParticleSystem.Shape.SQUARE, 1, 360);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        UpdateSection root = R.pages.get("main").getRegistry().findGui("particle").getRoot();
        dir = root.findElementById("direction");
        range = root.findElementById("range");
        speed = root.findElementById("speed");
        hue = root.findElementById("hue");
    }

    @Override
    public void update(MVEngine engine, Window window) {
        particleSystem.setDirection((int) dir.getValue());
        particleSystem.setRange((int) range.getValue());
        particleSystem.setSpeed((int) speed.getValue());
        particleSystem.setColor(particleSystem.getColor().toRGB((int) hue.getValue(), 1, 1));
    }

    @Override
    public void draw(MVEngine engine, Window window) {
        R.guis.get("default").renderGuis();
        particleSystem.draw(ctx2D);
        //ctx2D.rectangle(100, 100, 20, 50, 45);
    }

    public Directory getGameDirectory() {
        return gameDirectory;
    }
}