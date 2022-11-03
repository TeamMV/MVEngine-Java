package dev.mv.engine.render.opengl._2d.vertex;

import dev.mv.engine.render.opengl._2d.batch.OpenGLBatch2D;

public class OpenGLVertex2D {
    private float[] data = new float[OpenGLBatch2D.VERTEX_SIZE_FLOATS];

    public OpenGLVertex2D put(float... data) {
        this.data = data;
        return this;
    }

    public float get(int index) {
        return data[index];
    }
}
