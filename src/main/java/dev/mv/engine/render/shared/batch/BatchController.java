package dev.mv.engine.render.shared.batch;

import dev.mv.engine.MVEngine;
import dev.mv.engine.render.shared.Window;
import dev.mv.engine.render.shared.create.RenderBuilder;
import dev.mv.engine.render.shared.shader.Shader;
import dev.mv.engine.render.shared.texture.Texture;

import java.util.ArrayList;
import java.util.List;

public class BatchController {
    protected Window win;
    protected int maxBatchSize;
    protected Shader defaultShader, prebuildDefaultShader;
    protected int currentBatch;
    protected final List<Batch> batches = new ArrayList<>();

    private final String VERTEX_PATH = "/assets/mvengine/shaders/2d/default.vert";
    private final String FRAGMENT_PATH = "/assets/mvengine/shaders/2d/default.frag";

    public BatchController(Window window, int batchLimit) {
        if (batchLimit < 14) {
            MVEngine.Exceptions.__throw__(new IllegalArgumentException("Batch limit of " + batchLimit + " is too small, at least 14 is required!"));
        }

        defaultShader = RenderBuilder.newShader(VERTEX_PATH, FRAGMENT_PATH);
        defaultShader.make(win);
        defaultShader.bind();
        prebuildDefaultShader = defaultShader;
        defaultShader.use();

        win = window;
        maxBatchSize = batchLimit;
        currentBatch = 0;
    }

    public void start() {
        batches.add(new Batch(maxBatchSize, win, defaultShader));
    }

    protected void nextBatch() {
        currentBatch++;
        try {
            batches.get(currentBatch);
        } catch (IndexOutOfBoundsException e) {
            batches.add(new Batch(maxBatchSize, win, defaultShader));
        }
    }

    public void addVertices(VertexGroup vertexData, boolean useCamera) {

        if (batches.get(currentBatch).isFull(vertexData.length() * Batch.VERTEX_SIZE_FLOATS)) {
            nextBatch();
        }

        batches.get(currentBatch).addVertices(vertexData, useCamera);
    }

    public int addTexture(Texture tex) {
        if (batches.get(currentBatch).isFullOfTextures() || batches.get(currentBatch).isFull(Batch.VERTEX_SIZE_FLOATS * 4)) {
            nextBatch();
        }

        int texID = batches.get(currentBatch).addTexture(tex);

        if (texID == -1) {
            nextBatch();
            texID = batches.get(currentBatch).addTexture(tex);
        }

        return texID;
    }

    public int addTexture(Texture tex, int vertices) {
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

    public int getNumberOfBatches() {
        return batches.size();
    }

    public void finish() {
        for (int i = 0; i <= currentBatch; i++) {
            batches.get(i).finish();
        }
        currentBatch = 0;
    }

    public void render() {
        defaultShader.use();
        for (int i = 0; i <= currentBatch; i++) {
            batches.get(i).render();
        }
        currentBatch = 0;
    }

    public void finishAndRender() {
        defaultShader.use();
        for (int i = 0; i <= currentBatch; i++) {
            batches.get(i).finish();
            batches.get(i).render();
        }
        currentBatch = 0;
    }

    public void rebuildShader() {
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

    public void rebuildShader(String vertexShader, String fragmentShader) {
        defaultShader = RenderBuilder.newShader(vertexShader, fragmentShader);
        defaultShader.make(win);
        defaultShader.use();
        for (Batch batch : batches) {
            batch.setShader(defaultShader);
        }
    }

    public void rebuildShader(Shader shader) {
        defaultShader = shader;
        for (Batch batch : batches) {
            batch.setShader(defaultShader);
        }
    }
}
