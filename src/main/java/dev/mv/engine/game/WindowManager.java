package dev.mv.engine.game;

import dev.mv.engine.ApplicationLoop;
import dev.mv.engine.MVEngine;
import dev.mv.engine.render.shared.*;
import dev.mv.engine.render.shared.models.ObjectLoader;

class WindowManager implements ApplicationLoop {

    private Game game;
    private DrawContext2D ctx2D;
    private DrawContext3D ctx3D;
    private Camera camera;
    private DefaultCameraController cameraController;
    private ObjectLoader objectLoader;

    WindowManager(Game game) {
        this.game = game;
    }

    @Override
    public void start(MVEngine engine, Window window) {
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

    }

    @Override
    public void exit(MVEngine engine, Window window) {

    }
}
