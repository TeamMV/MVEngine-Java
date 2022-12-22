package dev.mv.engine.gui.components;

import dev.mv.engine.gui.components.extras.Text;
import dev.mv.engine.gui.event.ClickListener;
import dev.mv.engine.gui.event.EventListener;
import dev.mv.engine.gui.event.TextChangeListener;
import dev.mv.engine.gui.input.Clickable;
import dev.mv.engine.gui.theme.Theme;
import dev.mv.engine.gui.utils.GuiUtils;
import dev.mv.engine.render.shared.DrawContext2D;
import dev.mv.engine.render.shared.font.BitmapFont;

public class Button extends Element implements Text, Clickable {
    private String text;
    private BitmapFont font;

    public Button(Element parent, int width, int height) {
        super(parent);
        this.width = width;
        this.height = height;
    }

    public Button(int x, int y, int width, int height) {
        super(x, y);
        this.width = width;
        this.height = height;
    }

    public Button(int x, int y, Element parent, int width, int height) {
        super(x, y, parent);
        this.width = width;
        this.height = height;
    }

    public Button(int x, int y, int width, int height, int width1, int height1) {
        super(x, y, width, height);
        this.width = width1;
        this.height = height1;
    }

    public Button(int x, int y, int width, int height, Element parent, int width1, int height1) {
        super(x, y, width, height, parent);
        this.width = width1;
        this.height = height1;
    }

    @Override
    public void setFont(BitmapFont font) {
        this.font = font;
    }

    @Override
    public BitmapFont getFont(BitmapFont font) {
        return null;
    }

    @Override
    public void setText(String text) {
        this.text = text;
        if(width < font.getWidth(text) + 40) {
            width = font.getWidth(text) + 40;
        }
    }

    @Override
    public String getText() {
        return null;
    }

    @Override
    public void draw(DrawContext2D draw, Theme theme) {
        font = theme.getFont();

        if(theme.getEdgeStyle() == Theme.EdgeStyle.ROUND) {
            if(theme.normal.getBase() != null) {
                draw.color(theme.normal.getBase());
            } else if(theme.normal.getBaseGradient() != null) {
                draw.color(theme.normal.getBaseGradient());
            }

            draw.roundedRectangle(x, y, width, height, theme.getEdgeRadius(), theme.getEdgeRadius());

            if(theme.hasOutline()) {
                int thickness = theme.getOutlineThickness();
                if(theme.normal.getOutline() != null) {
                    draw.color(theme.normal.getOutline());
                } else if(theme.normal.getOutlineGradient() != null) {
                    draw.color(theme.normal.getOutlineGradient());
                }

                draw.voidRoundedRectangle(x - thickness, y - thickness, width + 2 * thickness, height + 2 * thickness, thickness, theme.getEdgeRadius(), theme.getEdgeRadius());
            }

            draw.font(font);

            if(theme.normal.getText_base() != null) {
                draw.color(theme.normal.getText_base());
            } else if(theme.normal.getText_gradient() != null) {
                draw.color(theme.normal.getText_gradient());
            }

            draw.text(x + 20, y + 20, height - 40, text);
        }
    }

    @Override
    public void attachListener(EventListener listener) {
        if(listener instanceof ClickListener clickListener) {
            this.clickListener = clickListener;
        }
    }

    @Override
    public void click(int x, int y, int btn) {
        if(GuiUtils.mouseNotInside(this.x, this.y, width, height)) return;
        if(clickListener != null) {
            clickListener.onCLick(this, btn);
        }
    }

    @Override
    public void clickRelease(int x, int y, int btn) {
        if(GuiUtils.mouseNotInside(this.x, this.y, width, height)) return;
        if(clickListener != null) {
            clickListener.onRelease(this, btn);
        }
    }
}
