package dev.mv.engine.render.shared.batch;

import dev.mv.engine.exceptions.Exceptions;
import dev.mv.engine.render.shared.Window;
import dev.mv.engine.render.shared.create.RenderBuilder;
import dev.mv.engine.render.shared.shader.Shader;
import dev.mv.engine.render.shared.texture.Texture;
import dev.mv.utils.collection.UnsafeVec;
import dev.mv.utils.collection.Vec;

public class SpecificBatchController<T extends Batch> {

    protected final UnsafeVec<T> batches = new Vec<T>().unsafe();
    private final BatchSupplier<T> supplier;

    protected Window win;
    protected int maxBatchSize;
    protected Shader defaultShader, prebuildDefaultShader;
    protected int currentBatch;

    public SpecificBatchController(Window window, int batchLimit, Shader defaultShader, BatchSupplier<T> supplier) {
        this.supplier = supplier;
        if (batchLimit < 14) {
            Exceptions.send(new IllegalArgumentException("Batch limit of " + batchLimit + " is too small, at least 14 is required!"));
        }

        this.defaultShader = defaultShader;
        prebuildDefaultShader = defaultShader;

        win = window;
        maxBatchSize = batchLimit;
        currentBatch = 0;
    }

    public void start() {
        batches.push(supplier.create(maxBatchSize, win, defaultShader));
    }

    protected void nextBatch() {
        currentBatch++;
        try {
            batches.get(currentBatch);
        } catch (IndexOutOfBoundsException e) {
            batches.push(supplier.create(maxBatchSize, win, defaultShader));
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

    public void setShader(Shader shader) {
        defaultShader = shader;
        for (T batch : batches) {
            batch.setShader(defaultShader);
        }
    }

}
