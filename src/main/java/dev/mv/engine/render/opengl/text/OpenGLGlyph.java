package dev.mv.engine.render.opengl.text;

import dev.mv.engine.render.drawables.text.Glyph;
import org.joml.Vector2f;

public class OpenGLGlyph implements Glyph {
    private int x, y, width, height, xOff, yOff, xAdv;
    private Vector2f[] texCoords = new Vector2f[2];
    private int maxHeight = 0;

    public OpenGLGlyph(int x, int y, int width, int height, int xOff, int yOff, int xAdv) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.xOff = xOff;
        this.yOff = yOff;
        this.xAdv = xAdv;
    }

    @Override
    public int getX() {
        return x;
    }

    @Override
    public int getY() {
        return y;
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public int getHeight() {
        return height;
    }

    @Override
    public int getXOffset() {
        return xOff;
    }

    @Override
    public int getYOffset() {
        return yOff;
    }

    @Override
    public int getXAdvance() {
        return xAdv;
    }

    @Override
    public int getWidth(int height) {
        return (int) (getWidth() * multiplier(height));
    }

    @Override
    public int getHeight(int height) {
        return (int) (getHeight() * multiplier(height));
    }

    @Override
    public int getXOffset(int height) {
        return (int) (getXOffset() * multiplier(height));
    }

    @Override
    public int getYOffset(int height) {
        return (int) (getYOffset() * multiplier(height));
    }

    @Override
    public int getXAdvance(int height) {
        return (int) (getXAdvance() * multiplier(height));
    }

    @Override
    public Vector2f[] getCoordinates() {
        return texCoords;
    }

    @Override
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
