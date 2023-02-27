package dev.mv.engine.render.shared.batch;

import dev.mv.engine.render.shared.Window;
import dev.mv.engine.render.shared.create.RenderBuilder;
import dev.mv.engine.render.shared.shader.Shader;

import java.util.ArrayList;
import java.util.List;

public class BatchController3D{
    private final String VERTEX_PATH = "/assets/mvengine/shaders/3d/geometryPass.vert";
    private final String FRAGMENT_PATH = "/assets/mvengine/shaders/3d/default.frag";
    private final List<Batch3D> batches = new ArrayList<>();
    private Shader defaultShader, prebuildDefaultShader;
    private int maxBatchSize = 0, currentBatch = 0;
    private Window win;

    public BatchController3D(Window window, int batchLimit) {
        this.win = window;
        this.maxBatchSize = batchLimit;
        defaultShader = RenderBuilder.newShader(VERTEX_PATH, FRAGMENT_PATH);
        defaultShader.make(win);
        defaultShader.bind();
        prebuildDefaultShader = defaultShader;
        defaultShader.use();
    }

    public void start() {
        batches.add(new Batch3D(maxBatchSize, win, defaultShader));
    }

    protected void nextBatch() {
        currentBatch++;
        try {
            batches.get(currentBatch);
        } catch (IndexOutOfBoundsException e) {
            batches.add(new Batch3D(maxBatchSize, win, defaultShader));
        }
    }

    public void pushVertex(Vertex vertex) {
        if (batches.get(currentBatch).isFull(Batch3D.VERTEX_SIZE_FLOATS)) {
            nextBatch();
        }

        batches.get(currentBatch).addVertex(vertex);
    }

    public void end() {
        nextBatch();
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
        for (Batch3D batch : batches) {
            batch.setShader(defaultShader);
        }
    }

    public void rebuildShader(String vertexShader, String fragmentShader) {
        defaultShader = RenderBuilder.newShader(vertexShader, fragmentShader);
        defaultShader.make(win);
        defaultShader.use();
        for (Batch3D batch : batches) {
            batch.setShader(defaultShader);
        }
    }

    public void rebuildShader(Shader shader) {
        defaultShader = shader;
        for (Batch3D batch : batches) {
            batch.setShader(defaultShader);
        }
    }
}
