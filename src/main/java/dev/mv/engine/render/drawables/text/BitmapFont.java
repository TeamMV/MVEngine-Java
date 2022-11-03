package dev.mv.engine.render.drawables.text;

import dev.mv.engine.render.drawables.Texture;

public interface BitmapFont {
    int getSpacing();
    int getMaxHeight();
    int getMaxHeight(int height);
    int getHeight(char c);
    int getWidth(char c);
    int getWidth(String s);
    int getMaxXOffset();
    int getMaxXOffset(int height);
    int getMaxYOffset();
    int getMaxYOffset(int height);

    Glyph getGlyph(char c);
    Glyph[] getGlyphs(String s);
    Texture getBitmap();
}
