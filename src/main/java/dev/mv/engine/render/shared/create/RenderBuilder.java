package dev.mv.engine.render.shared.create;

import dev.mv.engine.ApplicationConfig;
import dev.mv.engine.MVEngine;
import dev.mv.engine.render.opengl.OpenGLShader;
import dev.mv.engine.render.opengl.OpenGLTexture;
import dev.mv.engine.render.opengl.OpenGLTextureMap;
import dev.mv.engine.render.shared.shader.Shader;
import dev.mv.engine.render.shared.texture.Texture;
import dev.mv.utils.Utils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

public class RenderBuilder {
    public static Shader newShader(String vertexPath, String fragmentPath) {
        if (MVEngine.instance().getRenderingApi() == ApplicationConfig.RenderingAPI.OPENGL) {
            return new OpenGLShader(vertexPath, fragmentPath);
        } else {
            return null;
        }
    }

    public static Texture newTexture(InputStream stream) throws IOException {
        return newTexture(ImageIO.read(stream));
    }

    public static Texture newTexture(String path) throws IOException {
        return newTexture(ImageIO.read(Objects.requireNonNull(RenderBuilder.class.getResourceAsStream(path))));
    }

    public static Texture newTexture(BufferedImage image) throws IOException {
        if (MVEngine.instance().getRenderingApi() == ApplicationConfig.RenderingAPI.OPENGL) {
            return new OpenGLTexture(image);
        } else {
            return null;
        }
    }

    public static Texture newTextureMap(InputStream stream, OpenGLTextureMap.Quality quality) throws IOException {
        return newTextureMap(ImageIO.read(stream), quality);
    }

    public static Texture newTextureMap(String path, OpenGLTextureMap.Quality quality) throws IOException {
        return newTextureMap(ImageIO.read(Objects.requireNonNull(RenderBuilder.class.getResourceAsStream(path))), quality);
    }

    public static Texture newTextureMap(BufferedImage image, OpenGLTextureMap.Quality quality) throws IOException {
        if (MVEngine.instance().getRenderingApi() == ApplicationConfig.RenderingAPI.OPENGL) {
            return new OpenGLTextureMap(image, Utils.nextId("textureMap"), quality);
        } else {
            return null;
        }
    }
}
