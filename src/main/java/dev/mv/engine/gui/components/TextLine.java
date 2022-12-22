package dev.mv.engine.gui.components;

import dev.mv.engine.gui.components.extras.Text;
import dev.mv.engine.gui.event.ClickListener;
import dev.mv.engine.gui.event.EventListener;
import dev.mv.engine.gui.event.TextChangeListener;
import dev.mv.engine.gui.input.Clickable;
import dev.mv.engine.gui.theme.Theme;
import dev.mv.engine.render.shared.DrawContext2D;
import dev.mv.engine.render.shared.font.BitmapFont;

public class TextLine extends Element implements Text {
    private String text;
    private BitmapFont font;

    public TextLine(Element parent) {
        super(parent);
    }

    public TextLine(int x, int y) {
        super(x, y);
    }

    public TextLine(int x, int y, Element parent) {
        super(x, y, parent);
    }

    public TextLine(int x, int y, int width, int height) {
        super(x, y, width, height);
    }

    public TextLine(int x, int y, int width, int height, Element parent) {
        super(x, y, width, height, parent);
    }

    @Override
    public void draw(DrawContext2D draw, Theme theme) {
        font = theme.getFont();

        if(theme.normal.getText_base() != null) {
            draw.color(theme.normal.getText_base());
        } else if(theme.normal.getText_gradient() != null) {
            draw.color(theme.normal.getText_gradient());
        }
        draw.font(theme.getFont());
        draw.text(x, y, height, text);
    }

    @Override
    public void attachListener(EventListener listener) {
        if(listener instanceof TextChangeListener textChangeListener) {
            this.textChangeListener = textChangeListener;
        }
    }

    @Override
    public void setText(String text) {
        if(textChangeListener != null) textChangeListener.onChange(this, this.text, text);
        this.text = text;
        width = font.getWidth(text);
    }

    @Override
    public String getText() {
        return null;
    }

    public void setFont(BitmapFont font) {
        this.font = font;
    }

    @Override
    public BitmapFont getFont(BitmapFont font) {
        return font;
    }
}
