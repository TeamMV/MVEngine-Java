package dev.mv.engine.render.shared.batch;

import dev.mv.engine.exceptions.Exceptions;
import dev.mv.engine.render.shared.Window;
import dev.mv.engine.render.shared.create.RenderBuilder;
import dev.mv.engine.render.shared.shader.Shader;
import dev.mv.engine.render.shared.texture.Texture;

import java.util.EnumMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class BatchController {
    protected final Map<BatchType, SpecificBatchController> batchTypes = new EnumMap<>(BatchType.class);
    private final String VERTEX_PATH = "/assets/mvengine/shaders/2d/default.vert";
    private final String FRAGMENT_PATH = "/assets/mvengine/shaders/2d/default.frag";
    protected Window win;
    protected int maxBatchSize;
    protected Shader defaultShader, prebuildDefaultShader;

    public BatchController(Window window, int batchLimit) {
        if (batchLimit < 14) {
            Exceptions.send(new IllegalArgumentException("Batch limit of " + batchLimit + " is too small, at least 14 is required!"));
        }

        defaultShader = RenderBuilder.newShader(VERTEX_PATH, FRAGMENT_PATH);
        defaultShader.make(win);
        defaultShader.bind();
        prebuildDefaultShader = defaultShader;
        defaultShader.use();

        win = window;
        maxBatchSize = batchLimit;
    }

    public void start() {
        batchTypes.put(BatchType.TRIANGLES, new SpecificBatchController<>(win, maxBatchSize, defaultShader, TriangleBatch::new));
        batchTypes.put(BatchType.TRIANGLE_STRIP, new SpecificBatchController<>(win, maxBatchSize, defaultShader, ChainedTriangleBatch::new));
        batchTypes.put(BatchType.QUADS, new SpecificBatchController<>(win, maxBatchSize, defaultShader, QuadBatch::new));
        batchTypes.put(BatchType.QUAD_STRIP, new SpecificBatchController<>(win, maxBatchSize, defaultShader, ChainedQuadBatch::new));

        batchTypes.forEach((s, b) -> b.start());
    }

    public void addVertices(VertexGroup vertexData, boolean useCamera) {
        addVertices(vertexData, useCamera, false);
    }

    public void addVertices(VertexGroup vertexData, boolean useCamera, boolean strip) {
        batchTypes.get(BatchType.from(vertexData.length(), strip)).addVertices(vertexData, useCamera);
    }

    public int addTexture(Texture tex, BatchType type) {
        return batchTypes.get(type).addTexture(tex);
    }

    public int addTexture(Texture tex, int vertices, BatchType type) {
        return batchTypes.get(type).addTexture(tex, vertices);
    }

    public int getTotalNumberOfBatches() {
        AtomicInteger amount = new AtomicInteger(0);
        batchTypes.forEach((s, b) -> amount.addAndGet(b.getNumberOfBatches()));
        return amount.get();
    }

    public int getNumberOfBatches(BatchType type) {
        return batchTypes.get(type).getNumberOfBatches();
    }

    public void finish() {
        batchTypes.forEach((s, b) -> b.finish());
    }

    public void render() {
        batchTypes.forEach((s, b) -> b.render());
    }

    public void finishAndRender() {
        batchTypes.forEach((s, b) -> b.finishAndRender());
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
        batchTypes.forEach((s, b) -> b.setShader(defaultShader));
    }

    public void rebuildShader(String vertexShader, String fragmentShader) {
        defaultShader = RenderBuilder.newShader(vertexShader, fragmentShader);
        defaultShader.make(win);
        defaultShader.use();
        batchTypes.forEach((s, b) -> b.setShader(defaultShader));
    }

    public void rebuildShader(Shader shader) {
        defaultShader = shader;
        batchTypes.forEach((s, b) -> b.setShader(defaultShader));
    }
}
