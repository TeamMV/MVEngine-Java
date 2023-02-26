package dev.mv.test.terrain;

import dev.mv.engine.render.shared.Color;
import dev.mv.engine.render.shared.DrawContext3D;
import dev.mv.engine.render.shared.Window;
import dev.mv.utils.Utils;
import dev.mv.utils.misc.Noise;
import lombok.Getter;
import lombok.Setter;

public class Terrain {
    @Getter @Setter
    private float islandScale = 20f;
    private int w, h;

    @Getter
    @Setter
    private float scl;

    private Noise noiseGen;
    private Noise randomTileGen;

    private int width, height;
    @Getter @Setter
    private int tileSize;

    private Window window;
    private DrawContext3D renderer;

    private Color color;

    int[] tiles;


    public Terrain(DrawContext3D renderer, Window window, String seed) {

        this.renderer = renderer;
        this.window = window;

        color = new Color(0, 0, 0, 255);

        noiseGen = new Noise(seed.hashCode());
        randomTileGen = new Noise((int) (noiseGen.noise(1, 1, 1) * 1000));
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

        for (int i = 0; i < w; i++) {
            for (int j = 0; j < h; j++) {
                tiles[i + j * w] = getTile(i, j);
            }
        }

        return tiles;
    }

    private int getTile(int x, int y) {
        float v = noiseGen.noise(x * scl, y * scl);
        //float t = randomTileGen.noise((xTO + x) * 0.7f, (yTO + y) * 0.7f, 0.0f);
        return (int) ((v + 1) * islandScale);
    }

    public void render(int[] tiles) {
        System.out.println(w);
        for (int i = 0; i < w; i++) {
            for (int j = 0; j < h; j++) {
                try {
                    int x;
                    int z;

                    x = (i + 1) * tileSize;
                    z = j * tileSize;
                    renderer.color(color.toRGB(Utils.overlap(132, 0, 359), tiles[i + 1 + j * w] / islandScale * 1.2f, 48));
                    renderer.point(x, tiles[i + 1 + j * w], z);
                    x = i * tileSize;
                    z = j * tileSize;
                    renderer.color(color.toRGB(Utils.overlap(132, 0, 359), tiles[i + j * w] / islandScale * 1.2f, 48));
                    renderer.point(x, tiles[i + j * w], z);
                    /*
                    x = (int) (((i + 1) - buffer / 2) * tileSize - xRO);
                    z = (int) (((j + 1) - buffer / 2) * tileSize - yRO);
                    renderer.color(color.toRGB(Utils.overlap((int) (tiles[i + j * w] * islandScale), 0, 359), 1, 1));
                    renderer.point(x, tiles[i + 1 + (j + 1) * w], z);
                    x = (int) ((i - buffer / 2) * tileSize - xRO);
                    z = (int) (((j + 1) - buffer / 2) * tileSize - yRO);
                    renderer.color(color.toRGB(Utils.overlap((int) (tiles[i + 1 + j * w] * islandScale), 0, 359), 1, 1));
                    renderer.point(x, tiles[i + (j + 1) * w], z);*/
                } catch (IndexOutOfBoundsException e) {

                }
            }
            renderer.end();
        }
    }

    public void updateDimensions() {
        w = (width / tileSize);
        h = (height / tileSize);
    }
}
