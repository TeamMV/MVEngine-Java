package dev.mv.engine.render.opengl;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

import static org.lwjgl.opengl.GL46.*;

public class OpenGLTextureMap extends OpenGLTexture {
    private int attachmentIdx;
    private Quality quality;

    public OpenGLTextureMap(String filepath, int attachmentIdx, Quality quality) throws IOException {
        super(filepath);
        this.attachmentIdx = attachmentIdx;
        this.quality = quality;
    }

    public OpenGLTextureMap(InputStream inputStream, int attachmentIdx, Quality quality) throws IOException {
        super(inputStream);
        this.attachmentIdx = attachmentIdx;
        this.quality = quality;
    }

    public OpenGLTextureMap(BufferedImage img, int attachmentIdx, Quality quality) throws IOException {
        super(img);
        this.attachmentIdx = attachmentIdx;
        this.quality = quality;
    }

    @Override
    protected void create(ByteBuffer pixelBuffer) {
        this.id = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, this.id);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        glTexImage2D(GL_TEXTURE_2D, 0, this.quality == Quality.HIGH ? GL_RGB16F : GL_RGBA, this.width, this.height, 0, GL_RGBA, GL_UNSIGNED_BYTE, pixelBuffer);
        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0 + this.attachmentIdx, GL_TEXTURE_2D, this.id, 0);
        glGenerateMipmap(GL_TEXTURE_2D);
    }

    public int getAttachmentIdx() {
        return attachmentIdx;
    }

    public Quality getQuality() {
        return quality;
    }

    public enum Quality {
        LOW, HIGH
    }
}
