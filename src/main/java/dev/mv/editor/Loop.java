package dev.mv.editor;

import dev.mv.engine.input.Input;
import dev.mv.engine.input.InputCollector;
import dev.mv.engine.input.InputProcessor;
import dev.mv.engine.render.shared.Camera;
import dev.mv.engine.render.shared.DrawContext3D;
import dev.mv.engine.render.shared.Window;
import dev.mv.engine.terrain.Terrain;

public class Loop implements ApplicationLoop{
    private DrawContext3D drawContext3D;
    private Terrain terrain;

    @Override
    public void start(Window window) {
        drawContext3D = new DrawContext3D(window);
        InputProcessor inputProcessor = InputProcessor.defaultProcessor();
        InputCollector inputCollector = new InputCollector(inputProcessor, window);
        inputCollector.start();
        Input.init();

        terrain = new Terrain(drawContext3D, window, "v22");
        terrain.setScl(0.5f);
        terrain.setTileSize(50);
        terrain.setIslandScale(100);
        terrain.setDimensions(window.getWidth(), window.getHeight());

        window.getCamera().moveTo(0, 100, 0);
        window.getCamera().rotateTo(20f, 130, 0);
    }

    @Override
    public void update(Window window) {

    }

    @Override
    public void draw(Window window) {
        int[] terrainTiles = terrain.generateTerrain(1, 1);
        terrain.render(terrainTiles);

        Camera camera = window.getCamera();
        if(Input.isKeyPressed(Input.KEY_W))              camera.move(0, 0, -1);
        if(Input.isKeyPressed(Input.KEY_A))              camera.move(-1, 0, 0);
        if(Input.isKeyPressed(Input.KEY_S))              camera.move(0, 0, 1);
        if(Input.isKeyPressed(Input.KEY_D))              camera.move(1, 0, 0);
        if(Input.isKeyPressed(Input.KEY_SPACE))          camera.move(0, 1, 0);
        if(Input.isKeyPressed(Input.KEY_CTRL_LEFT))     camera.move(0, -1, 0);
        if(Input.isKeyPressed(Input.KEY_ARROW_UP))       camera.rotate(-2, 0, 0);
        if(Input.isKeyPressed(Input.KEY_ARROW_DOWN))     camera.rotate(2, 0, 0);
        if(Input.isKeyPressed(Input.KEY_ARROW_LEFT))     camera.rotate(0, -2, 0);
        if(Input.isKeyPressed(Input.KEY_ARROW_RIGHT))    camera.rotate(0, 2, 0);
    }
}
