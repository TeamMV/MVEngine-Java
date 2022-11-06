package dev.mv.engine.render.drawables;

public interface Texture {
    void bind();

    void unbind();

    int getWidth();

    int getHeight();

    int getId();

    TextureRegion cutRegion(int x, int y, int width, int height);

    TextureRegion convertToRegion();
}
