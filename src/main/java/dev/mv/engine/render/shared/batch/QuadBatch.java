package dev.mv.engine.render.shared.batch;

import dev.mv.engine.render.shared.Window;
import dev.mv.engine.render.shared.shader.Shader;

import static org.lwjgl.opengl.GL46.GL_TRIANGLES;

public class QuadBatch extends Batch {

    public QuadBatch(int maxSize, Window win, Shader shader) {
        super(maxSize, win, shader);
    }

    @Override
    public int getRenderMode() {
        return GL_TRIANGLES;
    }

    @Override
    public int getVertexCount() {
        return 4;
    }

    @Override
    protected void genIndices() {
        indices[objCount * 6 + 0] = 0 + objCount * 4;
        indices[objCount * 6 + 1] = 1 + objCount * 4;
        indices[objCount * 6 + 2] = 2 + objCount * 4;
        indices[objCount * 6 + 3] = 0 + objCount * 4;
        indices[objCount * 6 + 4] = 2 + objCount * 4;
        indices[objCount * 6 + 5] = 3 + objCount * 4;
    }
}
