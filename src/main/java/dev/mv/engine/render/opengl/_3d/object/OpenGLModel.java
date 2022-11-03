package dev.mv.engine.render.opengl._3d.object;

import dev.mv.engine.render.drawables.Texture;
import dev.mv.engine.render.models.Model;

public class OpenGLModel implements Model {
    private int voaID;
    private int vertexCount;

    public OpenGLModel(int voaID, int vertexCount) {
        this.voaID = voaID;
        this.vertexCount = vertexCount;
    }

    @Override
    public int getId() {
        return voaID;
    }

    @Override
    public int vertexCount() {
        return vertexCount;
    }

    @Override
    public Texture getTexture() {
        return null;
    }

    @Override
    public void setTexture(Texture texture) {

    }
}
