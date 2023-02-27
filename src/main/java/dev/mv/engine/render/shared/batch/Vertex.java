package dev.mv.engine.render.shared.batch;

public class Vertex {
    private float[] data = new float[Batch.VERTEX_SIZE_FLOATS];
    private int length = 0;

    public Vertex put(float... data) {
        System.arraycopy(data, 0, this.data, 0, data.length);
        length = data.length;
        return this;
    }

    public float get(int index) {
        return data[index];
    }

    public Vertex add(float data) {
        if (length >= Batch.VERTEX_SIZE_FLOATS) return this;
        this.data[length++] = data;
        return this;
    }
}
