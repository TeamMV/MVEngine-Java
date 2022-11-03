package dev.mv.engine.render.opengl.texture;

import dev.mv.engine.MVEngine;
import dev.mv.engine.render.drawables.Texture;
import dev.mv.engine.render.drawables.TextureRegion;
import org.lwjgl.BufferUtils;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_BYTE;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL30.glGenerateMipmap;

public class OpenGLTexture implements Texture {
    int[] pixels;
    private int id;
    private int width;
    private int height;

    public OpenGLTexture(String filename) throws IOException {
        this(ImageIO.read(new File(filename)));
    }

    public OpenGLTexture(InputStream stream) throws IOException {
        this(ImageIO.read(stream));
    }

    public OpenGLTexture(BufferedImage img) {
        this.width = img.getWidth();
        this.height = img.getHeight();
        this.pixels = new int[this.width * this.height];
        this.pixels = img.getRGB(0, 0, this.width, this.height, null, 0, this.width);
        ByteBuffer pixelBuffer = BufferUtils.createByteBuffer(this.width * this.height * 4);
        for (int i = 0; i < this.width * this.height; i++) {
            int pixel = pixels[i];
            pixelBuffer.put((byte) ((pixel >> 16) & 0xFF)); //r
            pixelBuffer.put((byte) ((pixel >> 8) & 0xFF));  //g
            pixelBuffer.put((byte) (pixel & 0xFF));           //b
            pixelBuffer.put((byte) ((pixel >> 24) & 0xFF)); //a
        }
        pixelBuffer.flip();
        this.id = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, this.id);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, this.width, this.height, 0, GL_RGBA, GL_UNSIGNED_BYTE, pixelBuffer);
        glGenerateMipmap(GL_TEXTURE_2D);
    }

    @Override
    public void bind() {
        glActiveTexture(GL_TEXTURE0 + this.id);
        glBindTexture(GL_TEXTURE_2D, this.id);
    }

    @Override
    public void unbind() {
        glBindTexture(GL_TEXTURE_2D, 0);
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
    public int getId() {
        return id;
    }

    @Override
    public TextureRegion cutRegion(int x, int y, int width, int height) {
        return new OpenGLTextureRegion(this, x, y, width, height);
    }

    @Override
    public TextureRegion convertToRegion() {
        return new OpenGLTextureRegion(this, 0, 0, width, height);
    }
}
