package dev.mv.engine.render.shared.batch;

import dev.mv.engine.render.shared.shader.Shader;
import dev.mv.engine.render.shared.Window;
import dev.mv.engine.render.shared.texture.Texture;
import lombok.Getter;
import lombok.Setter;
import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.Arrays;

import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;

public class Batch {
    public static final int POSITION_SIZE = 3;
    public static final int ROTATION_SIZE = 1;
    public static final int ROTATION_ORIGIN_SIZE = 2;
    public static final int COLOR_SIZE = 4;
    public static final int UV_SIZE = 2;
    public static final int TEX_ID_SIZE = 1;
    public static final int VERTEX_SIZE_FLOATS = POSITION_SIZE + ROTATION_SIZE + ROTATION_ORIGIN_SIZE + COLOR_SIZE + UV_SIZE + TEX_ID_SIZE;
    public static final int VERTEX_SIZE_BYTES = VERTEX_SIZE_FLOATS * Float.BYTES;
    public static final int POSITION_OFFSET = 0;
    public static final int POSITION_OFFSET_BYTES = POSITION_OFFSET * Float.BYTES;
    public static final int ROTATION_OFFSET = POSITION_SIZE;
    public static final int ROTATION_OFFSET_BYTES = ROTATION_OFFSET * Float.BYTES;
    public static final int ROTATION_ORIGIN_OFFSET = ROTATION_OFFSET + ROTATION_SIZE;
    public static final int ROTATION_ORIGIN_OFFSET_BYTES = ROTATION_ORIGIN_OFFSET * Float.BYTES;
    public static final int COLOR_OFFSET = ROTATION_ORIGIN_OFFSET + ROTATION_ORIGIN_SIZE;
    public static final int COLOR_OFFSET_BYTES = COLOR_OFFSET * Float.BYTES;
    public static final int UV_OFFSET = COLOR_OFFSET + COLOR_SIZE;
    public static final int UV_OFFSET_BYTES = UV_OFFSET * Float.BYTES;
    public static final int TEX_ID_OFFSET = UV_OFFSET + UV_SIZE;
    public static final int TEX_ID_OFFSET_BYTES = TEX_ID_OFFSET * Float.BYTES;
    private int maxSize;
    private float[] data;
    private int[] indices;
    private Texture[] textures;
    private Window win;
    @Getter
    @Setter
    private Shader shader;
    private FloatBuffer vbo;
    private int vbo_id;
    private IntBuffer ibo;
    private int ibo_id;
    private int[] tex_ids;
    /**
     * The var vertCount is the offset pointer for the incoming data,
     * therefor no data gets overridden.
     * For clearing this var, use clearBatch().
     */

    private int vertCount = 0;
    private int objCount = 0;
    private int nextFreeTexSlot = 1;
    private boolean isFull = false;
    private boolean isFullTex = false;

    public Batch(int maxSize, Window win, Shader shader) {
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

        textures = new Texture[17];
        tex_ids = new int[17];
    }

    /**
     * Important Note:
     * This method does not really clear the data of this batch, it just sets the data offset back to 0.
     * With this change, the batch gets overridden if new data comes in.
     * This is better for performance than actually clearing the array.
     * for really clearing the data, use forceClearBatch().
     */

    public void clearBatch() {
        vertCount = 0;
        objCount = 0;
        nextFreeTexSlot = 1;

        isFull = false;
        isFullTex = false;
    }

    /**
     * This method clears the actual data of the data array.
     * But keep in mind, that this uses some performance and for only resetting the data
     * offset, use clearBatch().
     */

    public void forceClearBatch() {
        Arrays.fill(data, 0, (vertCount * VERTEX_SIZE_FLOATS) + 1, 0);
        Arrays.fill(textures, 0, nextFreeTexSlot, null);
        Arrays.fill(tex_ids, 0, nextFreeTexSlot, 0);
        vertCount = 0;
        objCount = 0;
        nextFreeTexSlot = 1;

        isFull = false;
        isFullTex = false;
    }

    public boolean isFull(int amount) {
        return (vertCount * VERTEX_SIZE_FLOATS) + amount >= maxSize;
    }

    public boolean isFullOfTextures() {
        return isFullTex;
    }

    private void addVertex(Vertex vertex) {
        for (int i = 0; i < VERTEX_SIZE_FLOATS; i++) {
            data[i + (vertCount * VERTEX_SIZE_FLOATS)] = vertex.get(i);
        }
        vertCount++;
    }

    public void addVertices(VertexGroup vertData) {

        if (isFull) return;

        if (vertData.length() == 4) {
            indices[objCount * 6 + 0] = 0 + objCount * 4;
            indices[objCount * 6 + 1] = 1 + objCount * 4;
            indices[objCount * 6 + 2] = 2 + objCount * 4;
            indices[objCount * 6 + 3] = 0 + objCount * 4;
            indices[objCount * 6 + 4] = 2 + objCount * 4;
            indices[objCount * 6 + 5] = 3 + objCount * 4;
        } else {
            indices[objCount * 6 + 0] = 0 + objCount * 4;
            indices[objCount * 6 + 1] = 1 + objCount * 4;
            indices[objCount * 6 + 2] = 2 + objCount * 4;
        }

        for (int i = 0; i < vertData.length(); i++) {
            addVertex(vertData.get(i));
            if (vertCount > maxSize) {
                isFull = true;
                return;
            }
        }
        if (vertData.length() < 4) {
            addVertex(vertData.get(0));
            if (vertCount > maxSize) {
                isFull = true;
                return;
            }
        }

        objCount++;
    }

    public int addTexture(Texture tex) {

        if (isFullTex) return -1;

        for (int i = 0; i < textures.length; i++) {
            if (textures[i] == null) continue;
            if (textures[i].getId() == tex.getId()) {
                return i;
            }
        }

        textures[nextFreeTexSlot] = tex;
        tex_ids[nextFreeTexSlot] = tex.getId();
        nextFreeTexSlot++;

        if (nextFreeTexSlot >= textures.length) isFullTex = true;

        return nextFreeTexSlot - 1;
    }

    public void finish() {
        vbo.put(data);
        ibo.put(indices);
        vbo.flip();
        ibo.flip();
    }

    public void render() {
        win.getRender2D().retrieveVertexData(textures, tex_ids, indices, data, vbo_id, ibo_id, shader, GL_TRIANGLES);

        forceClearBatch();
    }
}
