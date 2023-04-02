package dev.mv.engine.render.opengl;

import dev.mv.engine.render.shared.Render2D;
import dev.mv.engine.render.shared.Transformations3D;
import dev.mv.engine.render.shared.Window;
import dev.mv.engine.render.shared.batch.Batch;
import dev.mv.engine.render.shared.shader.Shader;
import dev.mv.engine.render.shared.texture.Texture;

import static org.lwjgl.opengl.GL46.*;

public class OpenGLRender2D implements Render2D {
    private Window window;

    public OpenGLRender2D(Window window) {
        this.window = window;
    }

    @Override
    public void retrieveVertexData(Texture[] textures, int[] texIds, int[] indices, float[] vertices, int vboId, int iboId, Shader shader, int renderMode) {
        if (window == null) {
            throw new IllegalStateException("Window is not set!");
        }

        if (textures != null) {
            int i = 1;
            for (Texture texture : textures) {
                if (texture == null) continue;
                texture.bind(i++);
            }
        }

        glBindBuffer(GL_ARRAY_BUFFER, vboId);
        glBufferData(GL_ARRAY_BUFFER, vertices, GL_DYNAMIC_DRAW);

        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, iboId);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices, GL_DYNAMIC_DRAW);

        if (textures != null) {
            shader.uniform("TEX_SAMPLER", texIds);
        }
        shader.uniform("uResX", (float) window.getWidth());
        shader.uniform("uResY", (float) window.getHeight());

        shader.uniform("uProjection", window.getProjectionMatrix2D());
        shader.uniform("uView", Transformations3D.getViewMatrix2D(window.getCamera()));
        //shader.uniform("uCanvas", );

        //System.out.println(Arrays.toString(vertices));

        glVertexAttribPointer(0, Batch.POSITION_SIZE, GL_FLOAT, false, Batch.VERTEX_SIZE_BYTES, Batch.POSITION_OFFSET_BYTES);
        glEnableVertexAttribArray(0);
        glVertexAttribPointer(1, Batch.ROTATION_SIZE, GL_FLOAT, false, Batch.VERTEX_SIZE_BYTES, Batch.ROTATION_OFFSET_BYTES);
        glEnableVertexAttribArray(1);
        glVertexAttribPointer(2, Batch.ROTATION_ORIGIN_SIZE, GL_FLOAT, false, Batch.VERTEX_SIZE_BYTES, Batch.ROTATION_ORIGIN_OFFSET_BYTES);
        glEnableVertexAttribArray(2);
        glVertexAttribPointer(3, Batch.COLOR_SIZE, GL_FLOAT, false, Batch.VERTEX_SIZE_BYTES, Batch.COLOR_OFFSET_BYTES);
        glEnableVertexAttribArray(3);
        glVertexAttribPointer(4, Batch.UV_SIZE, GL_FLOAT, false, Batch.VERTEX_SIZE_BYTES, Batch.UV_OFFSET_BYTES);
        glEnableVertexAttribArray(4);
        glVertexAttribPointer(5, Batch.TEX_ID_SIZE, GL_FLOAT, false, Batch.VERTEX_SIZE_BYTES, Batch.TEX_ID_OFFSET_BYTES);
        glEnableVertexAttribArray(5);
        glVertexAttribPointer(6, Batch.CANVAS_COORDS_SIZE, GL_FLOAT, false, Batch.VERTEX_SIZE_BYTES, Batch.CANVAS_COORDS_OFFSET_BYTES);
        glEnableVertexAttribArray(6);
        glVertexAttribPointer(7, Batch.CANVAS_DATA_SIZE, GL_FLOAT, false, Batch.VERTEX_SIZE_BYTES, Batch.CANVAS_DATA_OFFSET_BYTES);
        glEnableVertexAttribArray(7);
        glVertexAttribPointer(8, Batch.USE_CAMERA_SIZE, GL_FLOAT, false, Batch.VERTEX_SIZE_BYTES, Batch.USE_CAMERA_OFFSET_BYTES);
        glEnableVertexAttribArray(8);

        glDrawElements(renderMode, indices.length, GL_UNSIGNED_INT, 0);

        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);

        if (textures != null) {
            for (Texture texture : textures) {
                if (texture == null) continue;
                texture.unbind();
            }
        }
    }

    @Override
    public int genBuffers() {
        return glGenBuffers();
    }
}
