package dev.mv.engine.render.shared.batch;

import dev.mv.engine.MVEngine;
import dev.mv.engine.render.shared.Window;
import dev.mv.engine.render.shared.create.RenderBuilder;
import dev.mv.engine.render.shared.shader.Shader;
import dev.mv.engine.render.shared.texture.Texture;

import java.util.ArrayList;
import java.util.List;

public class BatchController {
    private static Window win;
    private static int maxBatchSize;
    private static Shader defaultShader, prebuildDefaultShader;
    private static int currentBatch;
    private static List<Batch> batches = new ArrayList<>();

    private static String VERTEX_PATH = "/shaders/2d/default.vert", FRAGMENT_PATH = "/shaders/2d/default.frag";

    public static void init(Window window, int batchLimit) {
        if (batchLimit < 14) {
            MVEngine.Exceptions.Throw(new IllegalArgumentException("Batch limit of " + batchLimit + " is too small, at least 14 is required!"));
        }

        defaultShader = RenderBuilder.newShader(VERTEX_PATH, FRAGMENT_PATH);
        defaultShader.make(win);
        defaultShader.bind();
        prebuildDefaultShader = defaultShader;
        defaultShader.use();

        win = window;
        maxBatchSize = batchLimit;
        currentBatch = 0;
        batches.add(new Batch(batchLimit, window, defaultShader));
    }

    private static void nextBatch() {
        currentBatch++;
        try {
            batches.get(currentBatch);
        } catch (IndexOutOfBoundsException e) {
            batches.add(new Batch(maxBatchSize, win, defaultShader));
        }
    }

    public static void addVertices(VertexGroup vertexData) {

        if (batches.get(currentBatch).isFull(vertexData.length() * batches.get(0).VERTEX_SIZE_FLOATS)) {
            nextBatch();
        }

        batches.get(currentBatch).addVertices(vertexData);
    }

    public static int addTexture(Texture tex) {
        if (batches.get(currentBatch).isFullOfTextures() || batches.get(currentBatch).isFull(batches.get(0).VERTEX_SIZE_FLOATS * 4)) {
            nextBatch();
        }

        int texID = batches.get(currentBatch).addTexture(tex);

        if (texID == -1) {
            nextBatch();
            texID = batches.get(currentBatch).addTexture(tex);
        }

        return texID;
    }

    public static int addTexture(Texture tex, int vertices) {
        if (batches.get(currentBatch).isFullOfTextures() || batches.get(currentBatch).isFull(vertices)) {
            nextBatch();
        }

        int texID = batches.get(currentBatch).addTexture(tex);

        if (texID == -1) {
            nextBatch();
            texID = batches.get(currentBatch).addTexture(tex);
        }

        return texID;
    }

    public static int getNumberOfBatches() {
        return batches.size();
    }

    public static void finish() {
        for (int i = 0; i <= currentBatch; i++) {
            batches.get(i).finish();
        }
        currentBatch = 0;
    }

    public static void render() {
        for (int i = 0; i <= currentBatch; i++) {
            batches.get(i).render();
        }
        currentBatch = 0;
    }

    public static void finishAndRender() {
        for (int i = 0; i <= currentBatch; i++) {
            batches.get(i).finish();
            batches.get(i).render();
        }
        currentBatch = 0;
    }

    public static void rebuildShader() {
        if (prebuildDefaultShader != null) {
            defaultShader = prebuildDefaultShader;
            defaultShader.use();
            return;
        }
        defaultShader = RenderBuilder.newShader(VERTEX_PATH, FRAGMENT_PATH);
        defaultShader.make(win);
        prebuildDefaultShader = defaultShader;
        defaultShader.use();
        for (Batch batch : batches) {
            batch.setShader(defaultShader);
        }
    }

    public static void rebuildShader(String vertexShader, String fragmentShader) {
        defaultShader = RenderBuilder.newShader(vertexShader, fragmentShader);
        defaultShader.make(win);
        defaultShader.use();
        for (Batch batch : batches) {
            batch.setShader(defaultShader);
        }
    }

    public static void rebuildShader(Shader shader) {
        defaultShader = shader;
        for (Batch batch : batches) {
            batch.setShader(defaultShader);
        }
    }
}
