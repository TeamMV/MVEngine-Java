package dev.mv.engine.render.drawables.text;

import org.joml.Vector2f;

public interface Glyph {
    int getX();
    int getY();
    int getWidth();
    int getHeight();
    int getXOffset();
    int getYOffset();
    int getXAdvance();

    int getWidth(int height);
    int getHeight(int height);
    int getXOffset(int height);
    int getYOffset(int height);
    int getXAdvance(int height);

    Vector2f[] getCoordinates();
    void makeCoordinates(int atlasWidth, int atlasHeight, int maxHeight);
}
