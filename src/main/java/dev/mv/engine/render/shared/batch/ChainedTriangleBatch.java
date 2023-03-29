package dev.mv.engine.render.shared.batch;

import dev.mv.engine.render.shared.Window;
import dev.mv.engine.render.shared.shader.Shader;

import static org.lwjgl.opengl.GL46.GL_TRIANGLE_STRIP;

public class ChainedTriangleBatch extends TriangleBatch {

    public ChainedTriangleBatch(int maxSize, Window win, Shader shader) {
        super(maxSize, win, shader);
    }

    @Override
    public int getRenderMode() {
        return GL_TRIANGLE_STRIP;
    }

    @Override
    protected void genIndices() {
        indices[objCount * 3 + 0] = 0 + objCount * 3;
        indices[objCount * 3 + 1] = 1 + objCount * 3;
        indices[objCount * 3 + 2] = 2 + objCount * 3;
    }
}
