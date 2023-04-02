package dev.mv.engine.render.shared.batch;

import dev.mv.engine.render.shared.Window;
import dev.mv.engine.render.shared.shader.Shader;

import static org.lwjgl.opengl.GL46.GL_TRIANGLE_STRIP;

public class ChainedBatch extends RegularBatch {

    public ChainedBatch(int maxSize, Window win, Shader shader) {
        super(maxSize, win, shader);
    }

    @Override
    public int getRenderMode() {
        return GL_TRIANGLE_STRIP;
    }

    @Override
    protected void genIndices(int vertAmount) {
        if (vertAmount == 4) {
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
    }

    @Override
    public boolean isStrip() {
        return true;
    }
}
