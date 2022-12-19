package dev.mv.editor.launcher;

import dev.mv.engine.render.shared.create.RenderBuilder;
import imgui.ImGui;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class FileTextures {
    private static Map<String, Integer> extensions;

    public static void setupTextures() throws IOException {
        extensions = new HashMap<>();
        extensions.put("txt", RenderBuilder.newTexture("/gui/fileTextures/txt.png").getId());
        extensions.put("png", RenderBuilder.newTexture("/gui/fileTextures/png.png").getId());
        extensions.put("bmp", RenderBuilder.newTexture("/gui/fileTextures/bmp.png").getId());
        extensions.put("html", RenderBuilder.newTexture("/gui/fileTextures/html.png").getId());
        extensions.put("js", RenderBuilder.newTexture("/gui/fileTextures/js.png").getId());
        extensions.put("css", RenderBuilder.newTexture("/gui/fileTextures/css.png").getId());
        extensions.put("java", RenderBuilder.newTexture("/gui/fileTextures/java.png").getId());
        extensions.put("xml", RenderBuilder.newTexture("/gui/fileTextures/xml.png").getId());
        extensions.put("dir", RenderBuilder.newTexture("/gui/fileTextures/dir.png").getId());
    }

    public static int getType(String filename) {
        try {
            return extensions.get(filename.split("\\.")[filename.split("\\.").length - 1]);
        } catch (NullPointerException e) {
            return -1;
        }
    }
}
