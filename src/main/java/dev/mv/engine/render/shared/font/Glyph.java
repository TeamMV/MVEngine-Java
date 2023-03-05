package dev.mv.engine.render.shared.font;

import org.joml.Vector2f;

public class Glyph {
    private int x, y, width, height, xOff, yOff, xAdv;
    private Vector2f[] texCoords = new Vector2f[2];
    private int maxHeight = 0;

    public Glyph(int x, int y, int width, int height, int xOff, int yOff, int xAdv) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.xOff = xOff;
        this.yOff = yOff;
        this.xAdv = xAdv;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getXOffset() {
        return xOff;
    }

    public int getYOffset() {
        return yOff;
    }

    public int getXAdvance() {
        return xAdv;
    }

    public int getWidth(int height) {
        return (int) (getWidth() * multiplier(height));
    }

    public int getHeight(int height) {
        return (int) (getHeight() * multiplier(height));
    }

    public int getXOffset(int height) {
        return (int) (getXOffset() * multiplier(height));
    }

    public int getYOffset(int height) {
        return (int) (getYOffset() * multiplier(height));
    }

    public int getXAdvance(int height) {
        return (int) (getXAdvance() * multiplier(height));
    }

    public Vector2f[] getCoordinates() {
        return texCoords;
    }

    public void makeCoordinates(int atlasWidth, int atlasHeight, int maxHeight) {
        float x0 = (float) x / (float) atlasWidth;
        float x1 = (float) (x + width) / (float) atlasWidth;
        float y0 = (float) (y + height) / (float) atlasHeight;
        float y1 = (float) (y) / (float) atlasHeight;

        this.maxHeight = maxHeight;

        texCoords[0] = new Vector2f(x0, y0);
        texCoords[1] = new Vector2f(x1, y1);
    }

    private float multiplier(int height) {
        return (float) height / (float) maxHeight;
    }
}
