package dev.mv.engine.render.models;

import dev.mv.engine.render.drawables.Texture;

public interface Model {
    int getId();

    int vertexCount();

    Texture getTexture();

    void setTexture(Texture texture);
}
