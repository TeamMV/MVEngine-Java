package dev.mv.engine.render.shared.texture;

public interface Texture {
    void bind(int index);
    void unbind();
    int getWidth();
    int getHeight();
    int getId();
    TextureRegion cutRegion(int x, int y, int width, int height);
    TextureRegion convertToRegion();
}
