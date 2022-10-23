package dev.mv.engine.oldRender.utils;

import dev.mv.engine.oldRender.texture.Texture;
import dev.mv.engine.oldRender.texture.TextureRegion;

import java.io.IOException;

public class DefaultTextures {
    public static Texture BUTTON_SHEET;
    public static TextureRegion BUTTON_CROSS;
    public static TextureRegion BUTTON_TICK;
    public static TextureRegion BUTTON_DOT;
    public static TextureRegion BUTTON_ARROW;

    public static void onStart() {
        try {
            BUTTON_SHEET = new Texture(DefaultTextures.class.getResourceAsStream("/defaultTextures/buttonSheet.png"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
