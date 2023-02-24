package dev.mv.editor;

import dev.mv.engine.ApplicationLoop;
import dev.mv.engine.MVEngine;
import dev.mv.engine.gui.GuiRegistry;
import dev.mv.engine.gui.components.layouts.CollapseMenu;
import dev.mv.engine.gui.parsing.GuiConfig;
import dev.mv.engine.gui.parsing.gui.GuiParser;
import dev.mv.engine.gui.parsing.theme.ThemeParser;
import dev.mv.engine.gui.screens.Pager;
import dev.mv.engine.gui.screens.transitions.LinearShiftTransition;
import dev.mv.engine.gui.theme.Theme;
import dev.mv.engine.input.Input;
import dev.mv.engine.input.InputCollector;
import dev.mv.engine.input.InputProcessor;
import dev.mv.engine.render.opengl.OpenGLTextureMap;
import dev.mv.engine.render.shared.*;
import dev.mv.engine.render.shared.create.RenderBuilder;
import dev.mv.engine.render.shared.font.BitmapFont;
import dev.mv.engine.render.shared.models.Entity;
import dev.mv.engine.render.shared.models.Model;
import dev.mv.engine.render.shared.models.ObjectLoader;
import dev.mv.engine.render.shared.texture.Texture;
import dev.mv.engine.resources.R;
import dev.mv.engine.terrain.Terrain;
import dev.mv.utils.generic.Pair;
import org.joml.Vector3f;

import java.util.HashMap;

public class Loop implements ApplicationLoop {
    private DrawContext3D drawContext3D;
    private DrawContext2D drawContext2D;
    private Terrain terrain;
    private Entity cruiser;
    private BitmapFont font;
    private Texture minecraftBG;

    @Override
    public void start(MVEngine engine, Window window) throws Exception {
        drawContext3D = new DrawContext3D(window);
        drawContext2D = new DrawContext2D(window);
        InputProcessor inputProcessor = InputProcessor.defaultProcessor();
        InputCollector inputCollector = new InputCollector(inputProcessor, window);
        inputCollector.start();
        Input.init();

        terrain = new Terrain(drawContext3D, window, "v22");
        terrain.setScl(0.1f);
        terrain.setTileSize(20);
        terrain.setIslandScale(20);
        terrain.setDimensions(500, 500);


        ObjectLoader loader = engine.getObjectLoader();
        Model cruiserModel = loader.loadExternalModel("/models/cruiser/cruiser.obj");
        cruiserModel.setTexture(RenderBuilder.newTexture("/images/gold/gold paper_baseColor.jpeg"));
        cruiserModel.maps.setNormal(RenderBuilder.newTextureMap("/images/gold/gold paper_normal.jpeg", OpenGLTextureMap.Quality.HIGH));
        cruiserModel.maps.setSpecular(RenderBuilder.newTextureMap("/images/gold/gold paper_specular.jpeg", OpenGLTextureMap.Quality.LOW));
        cruiser = new Entity(cruiserModel, new Vector3f(0, 0, -5), new Vector3f(0, 0, 0), 1f);

        font = new BitmapFont("/fonts/FreeSans/FreeSans.png", "/fonts/FreeSans/FreeSans.fnt");

        minecraftBG = RenderBuilder.newTexture("/images/minecraft.jpeg");

        GuiConfig guiConfig = new GuiConfig("/gui/guiconfig.xml");

        ThemeParser themeParser = new ThemeParser(guiConfig);
        Theme theme = themeParser.parse("testTheme.xml");
        theme.setFont(font);
        GuiParser parser = new GuiParser(guiConfig);
        GuiRegistry guiRegistry = parser.parse(window, drawContext2D);
        Pager pager = new Pager(window);
        pager.map(new HashMap<>() {
            {
                put("myGui", new Pair<>(new LinearShiftTransition(0, -20), 1f));
                put("2ndGui", new Pair<>(new LinearShiftTransition(0, -20), 1f));
            }
        });
        guiRegistry.applyTheme(theme);
        guiRegistry.applyPager(pager);
        guiRegistry.swap("", "myGui");
        R.GUIS = guiRegistry;

        R.GUIS.findGui("myGui").getRoot().findElementsByType(CollapseMenu.class).forEach(cm -> cm.setBaseColor(Color.BLACK));
    }

    @Override
    public void update(MVEngine engine, Window window) throws Exception {

    }

    @Override
    public void draw(MVEngine engine, Window window) throws Exception {
        /*
        int[] terrainTiles = terrain.generateTerrain(1, 1);
        terrain.render(terrainTiles);
         */

        //drawContext3D.object(cruiser);

        drawContext2D.color(0, 0, 0, 0);
        drawContext2D.image(0, 0, window.getWidth(), window.getHeight(), minecraftBG);

        R.GUIS.renderGuis();

        Camera camera = window.getCamera();
        if(Input.isKeyPressed(Input.KEY_W))              camera.move(0, 0, -1);
        if(Input.isKeyPressed(Input.KEY_A))              camera.move(-1, 0, 0);
        if(Input.isKeyPressed(Input.KEY_S))              camera.move(0, 0, 1);
        if(Input.isKeyPressed(Input.KEY_D))              camera.move(1, 0, 0);
        if(Input.isKeyPressed(Input.KEY_SPACE))          camera.move(0, 1, 0);
        if(Input.isKeyPressed(Input.KEY_CTRL_LEFT))      camera.move(0, -1, 0);
        if(Input.isKeyPressed(Input.KEY_ARROW_UP))       camera.rotate(-2, 0, 0);
        if(Input.isKeyPressed(Input.KEY_ARROW_DOWN))     camera.rotate(2, 0, 0);
        if(Input.isKeyPressed(Input.KEY_ARROW_LEFT))     camera.rotate(0, -2, 0);
        if(Input.isKeyPressed(Input.KEY_ARROW_RIGHT))    camera.rotate(0, 2, 0);
    }
}
