package dev.mv.engine.test;

import dev.mv.engine.ApplicationLoop;
import dev.mv.engine.MVEngine;
import dev.mv.engine.files.Directory;
import dev.mv.engine.files.FileManager;
import dev.mv.engine.render.shared.*;
import dev.mv.engine.render.shared.models.ObjectLoader;

public class Test implements ApplicationLoop {

    public static final Test INSTANCE = new Test();

    private DrawContext2D ctx2D;
    private DrawContext3D ctx3D;
    private Camera camera;
    private DefaultCameraController cameraController;
    private ObjectLoader objectLoader;
    private Directory gameDirectory;

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
    }

    @Override
    public void update(MVEngine engine, Window window) {

    }

    @Override
    public void draw(MVEngine engine, Window window) {
        ctx3D.color(Color.RED);
        ctx3D.point(0, -3, 0);
        ctx3D.point(0, -3, -50);
        ctx3D.point(50, -3, -50);

        ctx2D.color(255, 255, 0, 100);
        ctx2D.rectangle(100, 100, 100, 100);

        cameraController.update();
    }

    public Directory getGameDirectory() {
        return gameDirectory;
    }
}