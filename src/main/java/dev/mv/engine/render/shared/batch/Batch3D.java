package dev.mv.engine.render.shared.batch;

import dev.mv.engine.render.shared.Window;
import dev.mv.engine.render.shared.shader.Shader;
import dev.mv.engine.render.shared.texture.Texture;
import lombok.Getter;
import lombok.Setter;
import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.Arrays;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.glGenBuffers;

public class Batch3D {
    public static final int POSITION_SIZE = 3;
    public static final int COLOR_SIZE = 4;
    public static final int VERTEX_SIZE_FLOATS = POSITION_SIZE + COLOR_SIZE;
    public static final int VERTEX_SIZE_BYTES = VERTEX_SIZE_FLOATS * Float.BYTES;
    public static final int POSITION_OFFSET = 0;
    public static final int POSITION_OFFSET_BYTES = POSITION_OFFSET * Float.BYTES;
    public static final int COLOR_OFFSET = POSITION_SIZE;
    public static final int COLOR_OFFSET_BYTES = COLOR_OFFSET * Float.BYTES;

    private int maxSize;
    private float[] data;
    private int[] indices;
    private Window win;
    @Getter
    @Setter
    private Shader shader;
    private FloatBuffer vbo;
    private int vbo_id;
    private IntBuffer ibo;
    private int ibo_id;

    private int vertCount = 0;
    private int objCount = 0;
    private boolean isFull = false;

    public Batch3D(int maxSize, Window win, Shader shader) {
        this.maxSize = maxSize;
        this.win = win;
        this.shader = shader;
        initBatch();
    }

    private void initBatch() {
        data = new float[VERTEX_SIZE_FLOATS * maxSize];
        indices = new int[maxSize * 6];

        vbo = BufferUtils.createFloatBuffer(VERTEX_SIZE_BYTES * maxSize);
        vbo_id = glGenBuffers();

        ibo = BufferUtils.createIntBuffer(maxSize * 6);
        ibo_id = glGenBuffers();
    }

    public void clearBatch() {
        vertCount = 0;
        objCount = 0;

        isFull = false;
    }

    public void forceClearBatch() {
        Arrays.fill(data, 0, (vertCount * VERTEX_SIZE_FLOATS) + 1, 0);
        vertCount = 0;
        objCount = 0;

        isFull = false;
    }

    public boolean isFull(int amount) {
        return (vertCount * VERTEX_SIZE_FLOATS) + amount >= maxSize;
    }

    public void addVertex(Vertex vertex) {
        for (int i = 0; i < VERTEX_SIZE_FLOATS; i++) {
            data[i + (vertCount * VERTEX_SIZE_FLOATS)] = vertex.get(i);
        }
        indices[vertCount] = vertCount;
        vertCount++;
    }

    public void finish() {
        vbo.put(data);
        ibo.put(indices);
        vbo.flip();
        ibo.flip();
    }

    public void render() {
        win.getRender3D().retrieveVertexData(indices, data, vbo_id, ibo_id, shader, GL_TRIANGLE_FAN, vertCount);

        forceClearBatch();
    }
}
