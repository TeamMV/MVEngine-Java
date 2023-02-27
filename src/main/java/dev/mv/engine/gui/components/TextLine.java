package dev.mv.engine.gui.components;

import dev.mv.engine.gui.components.animations.TextAnimation;
import dev.mv.engine.gui.components.animations.TextAnimator;
import dev.mv.engine.gui.components.extras.Text;
import dev.mv.engine.gui.event.EventListener;
import dev.mv.engine.gui.event.TextChangeListener;
import dev.mv.engine.render.shared.DrawContext2D;
import dev.mv.engine.render.shared.Window;
import dev.mv.engine.render.shared.font.BitmapFont;

public class TextLine extends Element implements Text {
    private String text = "";
    private BitmapFont font;
    private boolean chroma;

    public TextLine(Window window, Element parent, int height) {
        super(window, -1, -1, 0, height, parent);
    }

    public TextLine(Window window, int x, int y, int height) {
        super(window, x, y, 0, height, null);
    }

    public TextLine(Window window, int x, int y, int height, Element parent) {
        super(window, x, y, 0, height, parent);
    }

    public TextLine(Window window, int x, int y, int width, int height) {
        super(window, x, y, width, height, null);
    }

    public TextLine(Window window, int x, int y, int width, int height, Element parent) {
        super(window, x, y, width, height, parent);
    }

    @Override
    public void draw(DrawContext2D draw) {
        draw.color(animationState.textColor);

        draw.text(chroma, initialState.posX, initialState.posY, initialState.height, text, font, initialState.rotation, initialState.originX, initialState.originY);
    }

    @Override
    public void attachListener(EventListener listener) {
        if (listener instanceof TextChangeListener textChangeListener) {
            this.textChangeListeners.add(textChangeListener);
        }
    }

    @Override
    public String getText() {
        return null;
    }

    @Override
    public void setText(String text) {
        if (!textChangeListeners.isEmpty()) textChangeListeners.forEach(l -> l.onChange(this, this.text, text));
        this.text = text;
        if (font == null) return;
        initialState.width = font.getWidth(text, getHeight());
    }

    @Override
    public void applyAnimation(TextAnimation animation) {

    }

    @Override
    public TextAnimator getTextAnimator() {
        return null;
    }

    @Override
    public void setUseChroma(boolean chroma) {
        this.chroma = chroma;
    }

    @Override
    public BitmapFont getFont() {
        return font;
    }

    @Override
    public void setFont(BitmapFont font) {
        this.font = font;
        if (font == null) return;
        initialState.width = font.getWidth(text, getHeight());
    }
}
