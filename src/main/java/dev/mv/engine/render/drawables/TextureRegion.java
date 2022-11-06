package dev.mv.engine.render.drawables;

public interface TextureRegion {
    int getWidth();

    int getHeight();

    float[] getUVCoordinates();

    Texture getParentTexture();

}
