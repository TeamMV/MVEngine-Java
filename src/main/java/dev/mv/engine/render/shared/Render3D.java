package dev.mv.engine.render.shared;

import dev.mv.engine.render.shared.models.Entity;
import dev.mv.engine.render.shared.shader.Shader;

public interface Render3D {
    void entity(Entity entity);

    void render();

    void retrieveVertexData(int[] indices, float[] data, int vboId, int iboId, Shader shader, int renderMode, int amount);
}
