package dev.mv.editor;

import dev.mv.engine.MVEngine;
import dev.mv.engine.input.Input;
import dev.mv.engine.input.InputCollector;
import dev.mv.engine.input.InputProcessor;
import dev.mv.engine.render.opengl.OpenGLTextureMap;
import dev.mv.engine.render.shared.Camera;
import dev.mv.engine.render.shared.DrawContext3D;
import dev.mv.engine.render.shared.Window;
import dev.mv.engine.render.shared.create.RenderBuilder;
import dev.mv.engine.render.shared.models.Entity;
import dev.mv.engine.render.shared.models.Model;
import dev.mv.engine.render.shared.models.ObjectLoader;
import dev.mv.engine.terrain.Terrain;
import org.joml.Vector3f;

public class Loop implements ApplicationLoop{
    private DrawContext3D drawContext3D;
    private Terrain terrain;
    private Entity cruiser;

    @Override
    public void start(Window window) throws Exception {
        drawContext3D = new DrawContext3D(window);
        InputProcessor inputProcessor = InputProcessor.defaultProcessor();
        InputCollector inputCollector = new InputCollector(inputProcessor, window);
        inputCollector.start();
        Input.init();

        terrain = new Terrain(drawContext3D, window, "v22");
        terrain.setScl(0.1f);
        terrain.setTileSize(20);
        terrain.setIslandScale(20);
        terrain.setDimensions(500, 500);

        window.getCamera().moveTo(0, 0, 0);
        window.getCamera().rotateTo(0, 0, 0);
        window.getCamera().setSpeed(0.1f);

        ObjectLoader loader = MVEngine.getObjectLoader();
        Model cruiserModel = loader.loadExternalModel("/models/cruiser/cruiser.obj");
        cruiserModel.setTexture(RenderBuilder.newTexture("/images/gold/gold paper_baseColor.jpeg"));
        cruiserModel.maps.setNormal(RenderBuilder.newTextureMap("/images/gold/gold paper_normal.jpeg", OpenGLTextureMap.Quality.HIGH));
        cruiserModel.maps.setSpecular(RenderBuilder.newTextureMap("/images/gold/gold paper_specular.jpeg", OpenGLTextureMap.Quality.LOW));
        cruiser = new Entity(cruiserModel, new Vector3f(0, 0, -5), new Vector3f(0, 0, 0), 1f);
    }

    @Override
    public void update(Window window) throws Exception {

    }

    @Override
    public void draw(Window window) throws Exception {
        /*
        int[] terrainTiles = terrain.generateTerrain(1, 1);
        terrain.render(terrainTiles);
         */

        drawContext3D.object(cruiser);

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
