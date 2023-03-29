package dev.mv.engine.render.opengl;

import dev.mv.engine.render.shared.texture.Texture;
import dev.mv.engine.render.shared.texture.TextureRegion;
import org.lwjgl.BufferUtils;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL46.*;

public class OpenGLTexture implements Texture {
    protected int[] pixels;
    protected int id;
    protected int width;
    protected int height;

    public OpenGLTexture(String filename) throws IOException {
        ByteBuffer buffer;
        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer w = stack.mallocInt(1);
            IntBuffer h = stack.mallocInt(1);
            IntBuffer c = stack.mallocInt(1);

            buffer = STBImage.stbi_load(filename, w, h, c, 4);
            if (buffer == null) {
                throw new IOException("Could not load image " + filename);
            }
            width = w.get();
            height = h.get();

        }

        create(buffer);
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
        create(pixelBuffer);
    }

    protected void create(ByteBuffer pixelBuffer) {
        this.id = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, this.id);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, this.width, this.height, 0, GL_RGBA, GL_UNSIGNED_BYTE, pixelBuffer);
        glGenerateMipmap(GL_TEXTURE_2D);
    }

    @Override
    public void bind(int index) {
        glActiveTexture(GL_TEXTURE0 + index);
        glBindTexture(GL_TEXTURE_2D, id);
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
        return new TextureRegion(this, x, y, width, height);
    }

    @Override
    public TextureRegion convertToRegion() {
        return new TextureRegion(this, 0, 0, width, height);
    }
}
