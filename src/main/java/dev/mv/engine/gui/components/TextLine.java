package dev.mv.engine.gui.components;

import dev.mv.engine.gui.components.extras.Text;
import dev.mv.engine.gui.event.ClickListener;
import dev.mv.engine.gui.event.EventListener;
import dev.mv.engine.gui.event.TextChangeListener;
import dev.mv.engine.gui.input.Clickable;
import dev.mv.engine.gui.theme.Theme;
import dev.mv.engine.render.shared.DrawContext2D;
import dev.mv.engine.render.shared.Window;
import dev.mv.engine.render.shared.font.BitmapFont;

public class TextLine extends Element implements Text {
    private String text;
    private BitmapFont font;

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
        font = theme.getFont();

        if(theme.getText_base() != null) {
            draw.color(theme.getText_base());
        } else if(theme.getText_gradient() != null) {
            draw.color(theme.getText_gradient());
        }
        draw.font(theme.getFont());
        draw.text(initialState.posX, initialState.posY, initialState.height, text);
    }

    @Override
    public void attachListener(EventListener listener) {
        if(listener instanceof TextChangeListener textChangeListener) {
            this.textChangeListeners.add(textChangeListener);
        }
    }

    @Override
    public void setText(String text) {
        if(!textChangeListeners.isEmpty()) textChangeListeners.forEach(l -> l.onChange(this, this.text, text));
        this.text = text;
        if(font == null) return;
        initialState.width = font.getWidth(text, getHeight());
    }

    @Override
    public String getText() {
        return null;
    }

    public void setFont(BitmapFont font) {
        this.font = font;
        if(font == null) return;
        initialState.width = font.getWidth(text, getHeight());
    }

    @Override
    public BitmapFont getFont(BitmapFont font) {
        return font;
    }
}
