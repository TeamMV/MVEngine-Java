package dev.mv.engine.test;

import dev.mv.engine.ApplicationLoop;
import dev.mv.engine.MVEngine;
import dev.mv.engine.files.Directory;
import dev.mv.engine.files.FileManager;
import dev.mv.engine.gui.GuiRegistry;
import dev.mv.engine.gui.pages.Page;
import dev.mv.engine.gui.parsing.GuiConfig;
import dev.mv.engine.gui.screens.Pager;
import dev.mv.engine.gui.screens.transitions.LinearShiftTransition;
import dev.mv.engine.gui.theme.Theme;
import dev.mv.engine.render.shared.*;
import dev.mv.engine.render.shared.graphics.CircularParticleSystem;
import dev.mv.engine.render.shared.graphics.ParticleSystem;
import dev.mv.engine.render.shared.models.Entity;
import dev.mv.engine.render.shared.models.Model;
import dev.mv.engine.render.shared.models.ObjectLoader;
import dev.mv.engine.resources.R;
import dev.mv.engine.resources.ResourceLoader;
import dev.mv.utils.generic.pair.Pair;
import org.joml.Vector3f;

import java.io.IOException;
import java.util.HashMap;

public class Test implements ApplicationLoop {

    public static final Test INSTANCE = new Test();

    private DrawContext2D ctx2D;
    private DrawContext3D ctx3D;
    private Camera camera;
    private DefaultCameraController cameraController;
    private ObjectLoader objectLoader;
    private Directory gameDirectory;
    private CircularParticleSystem particleSystem;

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
            pager.open("myGui");
            Theme theme = R.themes.get("defaultTheme");
            theme.setFont(R.fonts.get("defaultFont"));
            registry.applyTheme(theme);

            particleSystem = new CircularParticleSystem(500, 500, 10, ParticleSystem.Shape.TRIANGLE, -30, 45);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void update(MVEngine engine, Window window) {

    }

    @Override
    public void draw(MVEngine engine, Window window) {
        R.guis.get("default").renderGuis();
        particleSystem.draw(ctx2D);
    }

    public Directory getGameDirectory() {
        return gameDirectory;
    }
}