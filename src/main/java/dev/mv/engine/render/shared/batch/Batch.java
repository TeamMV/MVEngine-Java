package dev.mv.engine.render.shared.batch;

import dev.mv.engine.render.shared.Window;
import dev.mv.engine.render.shared.shader.Shader;
import dev.mv.engine.render.shared.texture.Texture;
import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.Arrays;

public abstract class Batch {
    public static final int POSITION_SIZE = 3;
    public static final int ROTATION_SIZE = 1;
    public static final int ROTATION_ORIGIN_SIZE = 2;
    public static final int COLOR_SIZE = 4;
    public static final int UV_SIZE = 2;
    public static final int TEX_ID_SIZE = 1;
    public static final int CANVAS_COORDS_SIZE = 4;
    public static final int CANVAS_DATA_SIZE = 2;
    public static final int USE_CAMERA_SIZE = 1;
    public static final int VERTEX_SIZE_FLOATS = POSITION_SIZE + ROTATION_SIZE + ROTATION_ORIGIN_SIZE + COLOR_SIZE + UV_SIZE + TEX_ID_SIZE + CANVAS_COORDS_SIZE + CANVAS_DATA_SIZE + USE_CAMERA_SIZE;
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
    public static final int CANVAS_COORDS_OFFSET = TEX_ID_OFFSET + TEX_ID_SIZE;
    public static final int CANVAS_COORDS_OFFSET_BYTES = CANVAS_COORDS_OFFSET * Float.BYTES;
    public static final int CANVAS_DATA_OFFSET = CANVAS_COORDS_OFFSET + CANVAS_COORDS_SIZE;
    public static final int CANVAS_DATA_OFFSET_BYTES = CANVAS_DATA_OFFSET * Float.BYTES;
    public static final int USE_CAMERA_OFFSET = CANVAS_DATA_OFFSET + CANVAS_DATA_OFFSET_BYTES;
    public static final int USE_CAMERA_OFFSET_BYTES = USE_CAMERA_OFFSET * Float.BYTES;
    // p p p r ro ro c c c c uv uv ti uc cc cc cc cc
    protected int maxSize;
    protected float[] data;
    protected int[] indices;
    protected Texture[] textures;
    protected Window win;
    protected Shader shader;
    protected FloatBuffer vbo;
    protected int vbo_id;
    protected IntBuffer ibo;
    protected int ibo_id;
    protected int[] tex_ids;
    /**
     * The var vertCount is the offset pointer for the incoming data,
     * therefor no data gets overridden.
     * For clearing this var, use clearBatch().
     */

    protected int vertCount = 0;
    protected int objCount = 0;
    protected int nextFreeTexSlot = 0;
    protected boolean isFull = false;
    protected boolean isFullTex = false;

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
        vbo_id = win.getRender2D().genBuffers();

        ibo = BufferUtils.createIntBuffer(maxSize * 6);
        ibo_id = win.getRender2D().genBuffers();

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
        nextFreeTexSlot = 0;

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
        nextFreeTexSlot = 0;

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

    public void addVertices(VertexGroup vertData, boolean useCamera) {
        if (isFull(vertData.length())) return;

        genIndices(vertData.length());

        for (int i = 0; i < vertData.length(); i++) {
            addVertex(vertData.get(i).add(useCamera ? 1 : 0));
            if (vertCount > maxSize) {
                isFull = true;
                return;
            }
        }
        if (vertData.length() < 4) {
            addVertex(vertData.get(0).add(useCamera ? 1 : 0));
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
                return i + 1;
            }
        }

        textures[nextFreeTexSlot] = tex;
        tex_ids[nextFreeTexSlot] = nextFreeTexSlot + 1;
        nextFreeTexSlot++;

        if (nextFreeTexSlot >= textures.length) isFullTex = true;

        return nextFreeTexSlot;
    }

    public void finish() {
        vbo.put(data);
        ibo.put(indices);
        vbo.flip();
        ibo.flip();
    }

    public void render() {
        win.getRender2D().retrieveVertexData(textures, tex_ids, indices, data, vbo_id, ibo_id, shader, win.getAdapter().adaptRenderMode(getRenderMode()));

        forceClearBatch();
    }

    public Shader getShader() {
        return shader;
    }

    public void setShader(Shader shader) {
        this.shader = shader;
    }

    public abstract int getRenderMode();

    protected abstract void genIndices(int vertAmount);

    public abstract boolean isStrip();

}
