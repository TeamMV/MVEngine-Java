package dev.mv.engine.terrain;

import dev.mv.engine.render.shared.Color;
import dev.mv.engine.render.shared.DrawContext3D;
import dev.mv.engine.render.shared.Window;
import dev.mv.utils.Utils;
import dev.mv.utils.misc.Noise;
import lombok.Getter;
import lombok.Setter;
import org.joml.SimplexNoise;

public class Terrain {
    private final float islandScale = 20f;
    private int w, h, x, y;
    @Getter
    @Setter
    private int buffer = 10;

    @Getter
    @Setter
    private float scl;

    private Noise noiseGen;
    private Noise randomTileGen;

    private boolean v22, mqxf;

    private float xRO, yRO, xTO, yTO;
    private int width, height, tileSize;

    private Window window;
    private DrawContext3D renderer;

    private Color color;

    int[] tiles;


    public Terrain(DrawContext3D renderer, Window window, String seed) {
        v22 = false;
        mqxf = false;

        this.renderer = renderer;
        this.window = window;

        color = new Color(0, 0, 0, 255);

        this.width = 100;
        this.height = 100;

        tileSize = 5;

        noiseGen = new Noise(seed.hashCode());
        randomTileGen = new Noise((int) (noiseGen.noise(1, 1, 1) * 1000));

        if (seed.hashCode() == -1) v22 = true;
        if (seed.hashCode() == -2) mqxf = true;

        w = (width / tileSize) + buffer;
        h = (height / tileSize) + buffer;
    }

    public void setDimensions(int w, int h) {
        width = w;
        height = h;
    }

    public int[] generateTerrain(int xVal, int yVal) {

        //int xVal = (int) (xVall + Data.MAIN_WINDOW.camera.position.x);
        //int yVal = (int) (yVall + Data.MAIN_WINDOW.camera.position.y);

        updateDimensions();
        tiles = new int[w * h];

        xRO = xVal % tileSize;
        yRO = yVal % tileSize;
        xTO = xVal / tileSize;
        yTO = yVal / tileSize;

        x = xVal;
        y = yVal;

        for (int i = 0; i < w; i++) {
            for (int j = 0; j < h; j++) {
                tiles[i + j * w] = getTile(i, j);
            }
        }

        return tiles;
    }

    private int getTile(int x, int y) {

        float v = noiseGen.noise((xTO + x) * scl, (yTO + y) * scl, x);
        //float t = randomTileGen.noise((xTO + x) * 0.7f, (yTO + y) * 0.7f, 0.0f);
        //System.out.println(v);
        return (int) ((v + 1) * islandScale);
    }

    public void render(int[] tiles) {
        for (int i = 0; i < w; i++) {
            for (int j = 0; j < h; j++) {
                try {
                    int x;
                    int z;
                    x = (int) ((i - buffer / 2) * tileSize - xRO);
                    z = (int) ((j - buffer / 2) * tileSize - yRO);
                    renderer.color(color.toRGB(Utils.overlap((int) (tiles[i + j * w] * islandScale), 0, 359), 1, 1));
                    renderer.point(x, tiles[i + j * w], z);
                    x = (int) ((i - buffer / 2 + 1) * tileSize - xRO);
                    z = (int) ((j - buffer / 2) * tileSize - yRO);
                    renderer.color(color.toRGB(Utils.overlap((int) (tiles[i + 1 + j * w] * islandScale), 0, 359), 1, 1));
                    renderer.point(x, tiles[i + 1 + j * w], z);
                } catch (IndexOutOfBoundsException e) {

                }
            }
        }
    }

    public void updateDimensions() {
        w = (width / tileSize) + buffer;
        h = (height / tileSize) + buffer;
    }
}
