package dev.mv.engine.render.opengl.texture;

import dev.mv.engine.render.drawables.Texture;
import dev.mv.engine.render.drawables.TextureRegion;

public class OpenGLTextureRegion implements TextureRegion {
    OpenGLTexture tex;
    private int id;
    private int width;
    private int height;
    private float[] uv;

    public OpenGLTextureRegion(OpenGLTexture tex, int x, int y, int width, int height) {
        this.width = tex.getWidth();
        this.height = tex.getHeight();
        this.id = tex.getId();
        this.tex = tex;

        this.uv = createUV(x, y, width, height);
    }

    private float[] createUV(int x, int y, int width, int height) {
        return new float[] {
            (float) x / (float) this.width,
            (float) (x + width) / (float) this.width,
            (float) (y + height) / (float) this.height,
            (float) (y) / (float) this.height
        };
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
    public float[] getUVCoordinates() {
        return uv;
    }

    @Override
    public Texture getParentTexture() {
        return tex;
    }
}
