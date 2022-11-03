package dev.mv.engine.render.opengl._2d.vertex;

public class OpenGLVertexGroup2D {
    private OpenGLVertex2D[] data = new OpenGLVertex2D[4];

    private int length;

    public OpenGLVertexGroup2D set(OpenGLVertex2D v1, OpenGLVertex2D v2, OpenGLVertex2D v3, OpenGLVertex2D v4) {
        this.data[0] = v1;
        this.data[1] = v2;
        this.data[2] = v3;
        this.data[3] = v4;

        length = 4;

        return this;
    }

    public OpenGLVertexGroup2D set(OpenGLVertex2D v1, OpenGLVertex2D v2, OpenGLVertex2D v3) {
        this.data[0] = v1;
        this.data[1] = v2;
        this.data[2] = v3;

        length = 3;

        return this;
    }

    public OpenGLVertex2D get(int index) {
        return data[index];
    }

    public int length() {
        return length;
    }
}
