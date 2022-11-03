package dev.mv.engine;

import dev.mv.engine.render.Window;
import dev.mv.engine.render.drawables.Texture;
import dev.mv.engine.render.drawables.text.BitmapFont;
import dev.mv.engine.render.models.ObjectLoader;
import dev.mv.engine.render.opengl.OpenGLWindow;
import dev.mv.engine.render.opengl._3d.object.OpenGLObjectLoader;
import dev.mv.engine.render.opengl.text.OpenGLBitmapFont;
import dev.mv.engine.render.opengl.texture.OpenGLTexture;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

public class MVEngine {
    private static boolean usesVulkan = false;
    public static boolean usesVulkan() {
        return usesVulkan;
    }

    public static void init(boolean vulkan) {
        usesVulkan = vulkan;
    }

    public static Window createWindow(int width, int height, String title, boolean resizeable) {
        if(usesVulkan()) {
            return null;
        } else {
            return new OpenGLWindow(width, height, title, resizeable);
        }
    }

    public static Texture createTexture(String path) throws IOException {
        if(usesVulkan()) {
            return null;
        } else {
            return new OpenGLTexture(path);
        }
    }

    public static Texture createTexture(InputStream inputStream) throws IOException {
        if(usesVulkan()) {
            return null;
        } else {
            return new OpenGLTexture(inputStream);
        }
    }

    public static Texture createTexture(BufferedImage image) {
        if(usesVulkan()) {
            return null;
        } else {
            return new OpenGLTexture(image);
        }
    }

    public static BitmapFont createFont(String pngPath, String fntPath) {
        if(usesVulkan()) {
            return null;
        } else {
            return new OpenGLBitmapFont(pngPath, fntPath);
        }
    }

    public static ObjectLoader getObjectLoader() {
        if(usesVulkan()) {
            return null;
        } else {
            return OpenGLObjectLoader.instance();
        }
    }

    public static class Exceptions {
        public static void Throw(Throwable throwable) {
            throw new RuntimeException(throwable);
        }
    }
}
