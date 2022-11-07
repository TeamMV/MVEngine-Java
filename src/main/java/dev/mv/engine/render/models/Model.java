package dev.mv.engine.render.models;

import dev.mv.engine.render.drawables.Texture;

public interface Model {
    int getId();
    int vertexCount();
    Material getMaterial();
    void setMaterial(Material material);
    Texture getTexture();
    void setTexture(Texture texture);
    void setTexture(Texture texture, float reflectance);
}
