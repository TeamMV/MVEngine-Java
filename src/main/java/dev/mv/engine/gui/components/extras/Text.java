package dev.mv.engine.gui.components.extras;

import dev.mv.engine.render.shared.font.BitmapFont;

public interface Text {
    void setFont(BitmapFont font);
    BitmapFont getFont(BitmapFont font);
    void setText(String text);
    String getText();
}
