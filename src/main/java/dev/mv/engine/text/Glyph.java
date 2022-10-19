package dev.mv.engine.text;

import org.joml.Vector2f;

public class Glyph {
    private int x, y, width, height, xOff, yOff, xAdv;
    private Vector2f[] texCoords = new Vector2f[2];

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

    public int getxOff() {
        return xOff;
    }

    public int getyOff() {
        return yOff;
    }

    public int getxAdv() {
        return xAdv;
    }

    public Vector2f[] getTexCoords() {
        return texCoords;
    }

    public Glyph makeTexCoords(int mw, int mh) {         //0,0   1,0
        float x0 = (float) x / (float) mw;                //
        float x1 = (float) (x + width) / (float) mw;      //
        float y0 = (float) (y + height) / (float) mh;     //0,1   1,1
        float y1 = (float) (y) / (float) mh;

        texCoords[0] = new Vector2f(x0, y0);
        texCoords[1] = new Vector2f(x1, y1);

        return this;
    }
}
