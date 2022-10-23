package dev.mv.engine.oldRender.texture;

public class TextureRegion {
    Texture tex;
    private int id;
    private int width;
    private int height;
    private float[] uv;

    public TextureRegion(Texture tex, int x, int y, int width, int height) {
        this.width = tex.getWidth();
        this.height = tex.getHeight();
        this.id = tex.getID();
        this.tex = tex;

        this.uv = createUV(x, y, width, height);
    }

    public float[] getUV() {
        return this.uv;
    }

    private float[] createUV(int x, int y, int width, int height) {
        return new float[]{
            (float) x / (float) this.width,
            (float) (x + width) / (float) this.width,
            (float) (y + height) / (float) this.height,
            (float) (y) / (float) this.height
        };
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }

    public Texture getTexture() {
        return this.tex;
    }

}
