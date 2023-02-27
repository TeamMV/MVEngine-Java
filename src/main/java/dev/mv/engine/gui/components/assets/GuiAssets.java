package dev.mv.engine.gui.components.assets;

import dev.mv.engine.gui.theme.Theme;
import dev.mv.engine.render.shared.create.RenderBuilder;
import dev.mv.engine.render.shared.texture.Texture;
import dev.mv.engine.render.shared.texture.TextureRegion;

import java.io.IOException;

public class GuiAssets {
    public static TextureRegion TICK;
    public static TextureRegion DOT;
    public static TextureRegion ARROW_UP;
    public static TextureRegion ARROW_DOWN;
    public static TextureRegion EYE_OPEN;
    public static TextureRegion EYE_CLOSED;

    public static void init(Theme theme) throws IOException {
        Texture guiAssetSheet = RenderBuilder.newTexture(theme.getGuiAssetPath());
        TICK = guiAssetSheet.cutRegion(theme.getGuiAssetsIconWidth(), 0, theme.getGuiAssetsIconWidth(), theme.getGuiAssetsIconHeight());
        DOT = guiAssetSheet.cutRegion(0, theme.getGuiAssetsIconHeight(), theme.getGuiAssetsIconWidth(), theme.getGuiAssetsIconHeight());
        ARROW_UP = guiAssetSheet.cutRegion(0, theme.getGuiAssetsIconHeight() * 3, theme.getGuiAssetsIconWidth(), theme.getGuiAssetsIconHeight());
        ARROW_DOWN = guiAssetSheet.cutRegion(theme.getGuiAssetsIconWidth(), theme.getGuiAssetsIconHeight() * 3, theme.getGuiAssetsIconWidth(), theme.getGuiAssetsIconHeight());
        EYE_OPEN = guiAssetSheet.cutRegion(0, theme.getGuiAssetsIconHeight() * 2, theme.getGuiAssetsIconWidth(), theme.getGuiAssetsIconHeight());
        EYE_CLOSED = guiAssetSheet.cutRegion(theme.getGuiAssetsIconWidth(), theme.getGuiAssetsIconHeight() * 2, theme.getGuiAssetsIconWidth(), theme.getGuiAssetsIconHeight());
    }
}
