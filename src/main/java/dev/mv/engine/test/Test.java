package dev.mv.engine.test;

import dev.mv.engine.ApplicationLoop;
import dev.mv.engine.MVEngine;
import dev.mv.engine.audio.Music;
import dev.mv.engine.files.Directory;
import dev.mv.engine.files.FileManager;
import dev.mv.engine.gui.GuiRegistry;
import dev.mv.engine.gui.components.Button;
import dev.mv.engine.gui.components.FreeSlider;
import dev.mv.engine.gui.components.layouts.ChoiceGroup;
import dev.mv.engine.gui.components.layouts.UpdateSection;
import dev.mv.engine.gui.pages.Page;
import dev.mv.engine.gui.paging.Pager;
import dev.mv.engine.gui.paging.transitions.LinearShiftTransition;
import dev.mv.engine.gui.parsing.GuiConfig;
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

public class Test implements ApplicationLoop {

    public static final Test INSTANCE = new Test();
    FreeSlider dir;
    FreeSlider range;
    FreeSlider speed;
    FreeSlider hue;
    ChoiceGroup shape;
    private DrawContext2D ctx2D;
    private DrawContext3D ctx3D;
    private Camera camera;
    private DefaultCameraController cameraController;
    private ObjectLoader objectLoader;
    private Directory gameDirectory;
    private CircularParticleSystem particleSystem;

    private Test() {
    }

    @Override
    public void start(MVEngine engine, Window window) {
        gameDirectory = FileManager.getDirectory("factoryisland");
        ctx2D = new DrawContext2D(window);
        ctx3D = new DrawContext3D(window);
        camera = window.getCamera();
        camera.setSpeed(0.2f);
        cameraController = new DefaultCameraController(camera);
        objectLoader = engine.getObjectLoader();
        Music music = engine.getAudio().newMusic("/assets/mvengine/sound/11.wav");
        R.music.register("bestSong", music);

        //engine.getAudio().getDJ().loop("bestSong");

        try {
            ResourceLoader.markTheme("defaultTheme", "testTheme.xml");
            ResourceLoader.markPage("main", "main.xml");
            ResourceLoader.markLayout("test", "testLayout.xml");
            ResourceLoader.markLayout("quit", "quit.xml");
            ResourceLoader.markLayout("particle", "particle.xml");
            ResourceLoader.markFont("defaultFont", "/assets/mvengine/font/defaultfont.png", "/assets/mvengine/font/defaultfont.fnt");
            ResourceLoader.load(engine, new GuiConfig("/gui/guiConfig.xml"));
            Page main = R.pages.get("main");
            GuiRegistry registry = main.getRegistry();
            registry.applyRenderer(ctx2D);
            Pager pager = main.getPager();
            pager.map(new HashMap<>() {
                {
                    put("myGui", new Pair<>(new LinearShiftTransition(0, 0), 0.5f));
                }
            });
            pager.open("myGui");
            Theme theme = R.themes.get("defaultTheme");
            theme.setFont(R.fonts.get("defaultFont"));
            registry.applyTheme(theme);

            registry.findGui("myGui").getRoot().<Button>findElementById("chromaButton").setUseChroma(true);

            particleSystem = new CircularParticleSystem(300, 500, 100, ParticleSystem.Shape.SQUARE, 1, 360);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        UpdateSection root = R.pages.get("main").getRegistry().findGui("particle").getRoot();
        dir = root.findElementById("direction");
        range = root.findElementById("range");
        speed = root.findElementById("speed");
        hue = root.findElementById("hue");
        shape = root.findElementById("shape");

        //R.pages.get("main").getRegistry().findGui("myGui").getRoot().<CollapseMenu>findElementById("collapse").enable();
    }

    @Override
    public void update(MVEngine engine, Window window) {
        particleSystem.setDirection((int) dir.getValue());
        particleSystem.setRange((int) range.getValue());
        particleSystem.setSpeed((int) speed.getValue());
        particleSystem.setColor(particleSystem.getColor().toRGB((int) hue.getValue(), 1, 1));
        switch (shape.getCurrentChoice()) {
            case 1 -> particleSystem.setShape(ParticleSystem.Shape.TRIANGLE);
            case 2 -> particleSystem.setShape(ParticleSystem.Shape.SQUARE);
            case 3 -> particleSystem.setShape(ParticleSystem.Shape.CIRCLE);
        }
    }

    @Override
    public void draw(MVEngine engine, Window window) {
        R.guis.get("default").renderGuis();
        //ctx2D.inflatableGuy(200, 200, 400, 400);
        //ctx2D.canvas(300, 400, 200, 10);
        //ctx2D.color(Color.WHITE);
        //ctx2D.rectangle(300, 400, 200, 10);
        //ctx2D.mqxf(200, 200, 400, 400);
        //particleSystem.draw(ctx2D);
        //ctx2D.color(Color.RED);
        //ctx2D.triangularRectangle(100, 100, 300, 600, 100);
        //ctx2D.color(255, 255, 255, 255);
        //ctx2D.voidTriangularRectangle(100 - 3, 100 - 3, 300 + 6, 600 + 6, 3, 100 + 3);
        //ctx2D.voidCircle(300, 300, 200, 50, 50);
    }

    @Override
    public void exit(MVEngine engine, Window window) {

    }
}