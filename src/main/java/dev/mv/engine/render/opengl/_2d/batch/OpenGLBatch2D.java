package dev.mv.engine.render.opengl._2d.batch;

import dev.mv.engine.render.drawables.Texture;
import dev.mv.engine.render.opengl.OpenGLWindow;
import dev.mv.engine.render.opengl._2d.vertex.OpenGLVertex2D;
import dev.mv.engine.render.opengl._2d.vertex.OpenGLVertexGroup2D;
import dev.mv.engine.render.opengl.shader.OpenGLShader;
import dev.mv.utils.Utils;
import lombok.Getter;
import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.Arrays;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;

public class OpenGLBatch2D {
    private static final int POSITION_SIZE = 3;
    private static final int ROTATION_SIZE = 1;
    private static final int COLOR_SIZE = 4;
    private static final int UV_SIZE = 2;
    private static final int TEX_ID_SIZE = 1;
    private static final int CAMERA_MODE_SIZE = 1;
    private static final int ROTATION_ORIGIN_SIZE = 2;
    public final static int VERTEX_SIZE_FLOATS = POSITION_SIZE + ROTATION_SIZE + COLOR_SIZE + UV_SIZE + TEX_ID_SIZE + CAMERA_MODE_SIZE + ROTATION_ORIGIN_SIZE;
    private final int POSITION_OFFSET = 0;
    private final int POSITION_OFFSET_BYTES = POSITION_OFFSET * Float.BYTES;
    private final int ROTATION_OFFSET = POSITION_SIZE;
    private final int ROTATION_OFFSET_BYTES = ROTATION_OFFSET * Float.BYTES;
    private final int COLOR_OFFSET = POSITION_SIZE + ROTATION_SIZE;
    private final int COLOR_OFFSET_BYTES = COLOR_OFFSET * Float.BYTES;
    private final int UV_OFFSET = POSITION_SIZE + ROTATION_SIZE + COLOR_SIZE;
    private final int UV_OFFSET_BYTES = UV_OFFSET * Float.BYTES;
    private final int TEX_ID_OFFSET = POSITION_SIZE + ROTATION_SIZE + COLOR_SIZE + UV_SIZE;
    private final int TEX_ID_OFFSET_BYTES = TEX_ID_OFFSET * Float.BYTES;
    //f, f, f (pos), f (rot), f, f, f, f (col), f, f (uv), f (texID), f (camera mode)
    private final int CAMERA_MODE_OFFSET = POSITION_SIZE + ROTATION_SIZE + COLOR_SIZE + UV_SIZE + TEX_ID_SIZE;
    private final int CAMERA_MODE_OFFSET_BYTES = CAMERA_MODE_OFFSET * Float.BYTES;
    private final int ROTATION_ORIGIN_OFFSET = POSITION_SIZE + ROTATION_SIZE + COLOR_SIZE + UV_SIZE + TEX_ID_SIZE + CAMERA_MODE_SIZE;
    private final int ROTATION_ORIGIN_OFFSET_BYTES = ROTATION_ORIGIN_OFFSET * Float.BYTES;
    private final int VERTEX_SIZE_BYTES = VERTEX_SIZE_FLOATS * Float.BYTES;
    private int maxSize;
    private float[] data;
    private int[] indices;
    private Texture[] textures;
    private OpenGLWindow win;
    @Getter
    private OpenGLShader shader;
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

    public OpenGLBatch2D(int maxSize, OpenGLWindow win) {
        this.maxSize = maxSize;
        this.win = win;
        initBatch();
    }

    private void initBatch() {
        data = new float[VERTEX_SIZE_FLOATS * maxSize];
        indices = new int[maxSize * 6];

        shader = new OpenGLShader(Utils.getPath("src", "main", "java", "dev", "mv", "engine", "render", "opengl", "_2d", "shaderfiles", "vertex", "default.vert"), Utils.getPath("src", "main", "java", "dev", "mv", "engine", "render", "opengl", "_2d", "shaderfiles", "fragment", "default.frag"));

        shader.make();
        shader.use();

        vbo = BufferUtils.createFloatBuffer(VERTEX_SIZE_BYTES * maxSize);
        vbo_id = glGenBuffers();

        ibo = BufferUtils.createIntBuffer(maxSize * 6);
        ibo_id = glGenBuffers();

        //IntBuffer maxTex = BufferUtils.createIntBuffer(1);
        //glGetIntegerv(GL_MAX_TEXTURE_IMAGE_UNITS, maxTex);
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

    private void addVertex(OpenGLVertex2D vertex) {
        for (int i = 0; i < VERTEX_SIZE_FLOATS; i++) {
            data[i + (vertCount * VERTEX_SIZE_FLOATS)] = vertex.get(i);
        }
        vertCount++;
    }

    public void addVertices(OpenGLVertexGroup2D vertData) {

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

        for (Texture texture : textures) {
            if (texture == null) continue;
            texture.bind();
        }

        glBindBuffer(GL_ARRAY_BUFFER, vbo_id);
        glBufferData(GL_ARRAY_BUFFER, data, GL_DYNAMIC_DRAW);

        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ibo_id);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices, GL_DYNAMIC_DRAW);

        shader.setUniform1iv("TEX_SAMPLER", tex_ids);
        shader.setUniform1f("uResX", (float) win.getWidth());
        shader.setUniform1f("uResY", (float) win.getHeight());

        shader.setMatrix4f("uProjection", win.getProjectionMatrix2D());
        //shader.setMatrix4f("uView", win.camera.getViewMatrix());

        glVertexAttribPointer(0, POSITION_SIZE, GL_FLOAT, false, VERTEX_SIZE_BYTES, POSITION_OFFSET_BYTES);
        glEnableVertexAttribArray(0);
        glVertexAttribPointer(1, ROTATION_SIZE, GL_FLOAT, false, VERTEX_SIZE_BYTES, ROTATION_OFFSET_BYTES);
        glEnableVertexAttribArray(1);
        glVertexAttribPointer(2, COLOR_SIZE, GL_FLOAT, false, VERTEX_SIZE_BYTES, COLOR_OFFSET_BYTES);
        glEnableVertexAttribArray(2);
        glVertexAttribPointer(3, UV_SIZE, GL_FLOAT, false, VERTEX_SIZE_BYTES, UV_OFFSET_BYTES);
        glEnableVertexAttribArray(3);
        glVertexAttribPointer(4, TEX_ID_SIZE, GL_FLOAT, false, VERTEX_SIZE_BYTES, TEX_ID_OFFSET_BYTES);
        glEnableVertexAttribArray(4);
        glVertexAttribPointer(5, CAMERA_MODE_SIZE, GL_FLOAT, false, VERTEX_SIZE_BYTES, CAMERA_MODE_OFFSET_BYTES);
        glEnableVertexAttribArray(5);
        glVertexAttribPointer(6, ROTATION_ORIGIN_SIZE, GL_FLOAT, false, VERTEX_SIZE_BYTES, ROTATION_ORIGIN_OFFSET_BYTES);
        glEnableVertexAttribArray(6);

        glDrawElements(GL_TRIANGLES, indices.length, GL_UNSIGNED_INT, 0);

        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);

        for (Texture texture : textures) {
            if (texture == null) continue;
            texture.unbind();
        }

        forceClearBatch();
    }
}
