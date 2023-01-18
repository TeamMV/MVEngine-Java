package dev.mv.engine.gui.components.extras;

import dev.mv.engine.gui.components.animations.TextAnimation;
import dev.mv.engine.gui.components.animations.TextAnimator;
import dev.mv.engine.render.shared.font.BitmapFont;

public interface Text {
    void setFont(BitmapFont font);
    BitmapFont getFont();
    void setText(String text);
    String getText();
    void applyAnimation(TextAnimation animation);
    TextAnimator getTextAnimator();
    void setUseChroma(boolean chroma);
}
