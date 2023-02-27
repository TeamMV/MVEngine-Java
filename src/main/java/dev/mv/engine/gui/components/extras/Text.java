package dev.mv.engine.gui.components.extras;

import dev.mv.engine.gui.components.animations.TextAnimation;
import dev.mv.engine.gui.components.animations.TextAnimator;
import dev.mv.engine.render.shared.font.BitmapFont;

public interface Text {
    BitmapFont getFont();

    void setFont(BitmapFont font);

    String getText();

    void setText(String text);

    void applyAnimation(TextAnimation animation);

    TextAnimator getTextAnimator();

    void setUseChroma(boolean chroma);
}
