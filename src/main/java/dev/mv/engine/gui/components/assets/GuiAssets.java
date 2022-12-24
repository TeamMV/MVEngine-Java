package dev.mv.engine.gui.components.assets;

import dev.mv.engine.MVEngine;
import dev.mv.engine.gui.theme.Theme;
import dev.mv.engine.render.shared.create.RenderBuilder;
import dev.mv.engine.render.shared.texture.Texture;
import dev.mv.engine.render.shared.texture.TextureRegion;

import java.io.IOException;

public class GuiAssets {
    public static TextureRegion TICK;

    public static void init(Theme theme) throws IOException {
        Texture guiAssetSheet = RenderBuilder.newTexture(theme.getGuiAssetPath());
        TICK = guiAssetSheet.cutRegion(theme.getGuiAssetsIconWidth(), 0, theme.getGuiAssetsIconWidth(), theme.getGuiAssetsIconHeight());
    }
}
