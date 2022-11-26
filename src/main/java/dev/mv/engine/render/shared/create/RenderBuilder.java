package dev.mv.engine.render.shared.create;

import dev.mv.engine.ApplicationConfig;
import dev.mv.engine.MVEngine;
import dev.mv.engine.render.shared.shader.Shader;
import dev.mv.engine.render.shared.texture.Texture;
import dev.mv.engine.render.shared.Window;
import dev.mv.engine.render.WindowCreateInfo;
import dev.mv.engine.render.opengl.OpenGLShader;
import dev.mv.engine.render.opengl.OpenGLTexture;
import dev.mv.engine.render.opengl.OpenGLWindow;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class RenderBuilder {
    public static Shader newShader(String vertexPath, String fragmentPath) {
        if(MVEngine.getRenderingApi() == ApplicationConfig.RenderingAPI.OPENGL) {
            return new OpenGLShader(vertexPath, fragmentPath);
        } else {
            return null;
        }
    }

    public static Window newWindow(WindowCreateInfo info) {
        if(MVEngine.getRenderingApi() == ApplicationConfig.RenderingAPI.OPENGL) {
            return new OpenGLWindow(info);
        } else {
            return null;
        }
    }

    public static Texture newTexture(InputStream stream) throws IOException {
        return newTexture(ImageIO.read(stream));
    }

    public static Texture newTexture(String path) throws IOException {
        return newTexture(ImageIO.read(RenderBuilder.class.getResourceAsStream(path)));
    }

    public static Texture newTexture(BufferedImage image) throws IOException {
        if(MVEngine.getRenderingApi() == ApplicationConfig.RenderingAPI.OPENGL) {
            return new OpenGLTexture(image);
        } else {
            return null;
        }
    }
}
