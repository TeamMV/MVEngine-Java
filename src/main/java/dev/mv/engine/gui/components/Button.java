package dev.mv.engine.gui.components;

import dev.mv.engine.gui.components.animations.TextAnimation;
import dev.mv.engine.gui.components.animations.TextAnimator;
import dev.mv.engine.gui.components.extras.Text;
import dev.mv.engine.gui.components.extras.Toggle;
import dev.mv.engine.gui.event.ClickListener;
import dev.mv.engine.gui.event.EventListener;
import dev.mv.engine.gui.input.Clickable;
import dev.mv.engine.gui.theme.Theme;
import dev.mv.engine.gui.utils.GuiUtils;
import dev.mv.engine.gui.utils.VariablePosition;
import dev.mv.engine.render.shared.DrawContext2D;
import dev.mv.engine.render.shared.Window;
import dev.mv.engine.render.shared.font.BitmapFont;

public class Button extends AbstractClickable implements Text, Toggle, Clickable {
    private String text;
    private BitmapFont font;
    private boolean enabled = true;
    private boolean chroma;

    public Button(Window window, Element parent, int width, int height) {
        super(window, -1, -1, width, height, parent);
    }

    public Button(Window window, int x, int y, Element parent, int width, int height) {
        super(window, x, y, width, height, parent);
    }

    public Button(Window window, int x, int y, int width, int height) {
        super(window, x, y, width, height, null);
    }

    public Button(Window window, VariablePosition position, Element parent) {
        super(window, position, parent);
    }

    @Override
    public BitmapFont getFont() {
        return null;
    }

    @Override
    public void setFont(BitmapFont font) {
        this.font = font;
        if (font == null) return;
        if (initialState.width < font.getWidth(text, initialState.height - textDistance()) + textDistance()) {
            setWidth(font.getWidth(text, initialState.height - textDistance()) + textDistance());
        }
    }

    @Override
    public String getText() {
        return text;
    }

    @Override
    public void setText(String text) {
        this.text = text;
        if (font == null) return;
        if (initialState.width < font.getWidth(text, initialState.height - textDistance()) + textDistance()) {
            setWidth(font.getWidth(text, initialState.height - textDistance()) + textDistance());
        }
    }

    @Override
    public void setUseChroma(boolean chroma) {
        this.chroma = chroma;
    }

    @Override
    public void draw(DrawContext2D draw) {
        checkAnimations();

        if (theme.getEdgeStyle() == Theme.EdgeStyle.ROUND) {
            if (theme.hasOutline()) {
                int thickness = theme.getOutlineThickness();
                draw.color(animationState.outlineColor);
                if (!enabled) {
                    draw.color(theme.getDisabledOutlineColor());
                }
                draw.voidRoundedRectangle(animationState.posX, animationState.posY, animationState.width, animationState.height, thickness, theme.getEdgeRadius(), theme.getEdgeRadius(), animationState.rotation, animationState.originX, animationState.originY);
                draw.color(animationState.baseColor);
                if (!enabled) {
                    draw.color(theme.getDisabledBaseColor());
                }
                draw.roundedRectangle(animationState.posX + thickness, animationState.posY + thickness, animationState.width - 2 * thickness, animationState.height - 2 * thickness, theme.getEdgeRadius(), theme.getEdgeRadius(), animationState.rotation, animationState.originX, animationState.originY);
            } else {
                draw.color(animationState.baseColor);
                draw.roundedRectangle(animationState.posX, animationState.posY, animationState.width, animationState.height, theme.getEdgeRadius(), theme.getEdgeRadius(), animationState.rotation, animationState.originX, animationState.originY);
            }
        } else if (theme.getEdgeStyle() == Theme.EdgeStyle.TRIANGLE) {
            if (theme.hasOutline()) {
                int thickness = theme.getOutlineThickness();
                draw.color(animationState.outlineColor);
                if (!enabled) {
                    draw.color(theme.getDisabledOutlineColor());
                }
                draw.voidTriangularRectangle(animationState.posX, animationState.posY, animationState.width, animationState.height, thickness, theme.getEdgeRadius(), animationState.rotation, animationState.originX, animationState.originY);
                draw.color(animationState.baseColor);
                if (!enabled) {
                    draw.color(theme.getDisabledBaseColor());
                }
                draw.triangularRectangle(animationState.posX + thickness, animationState.posY + thickness, animationState.width - 2 * thickness, animationState.height - 2 * thickness, theme.getEdgeRadius(), animationState.rotation, animationState.originX, animationState.originY);
            } else {
                draw.color(animationState.baseColor);
                draw.triangularRectangle(animationState.posX, animationState.posY, animationState.width, animationState.height, theme.getEdgeRadius(), animationState.rotation, animationState.originX, animationState.originY);
            }
        } else if (theme.getEdgeStyle() == Theme.EdgeStyle.SQUARE) {
            if (theme.hasOutline()) {
                int thickness = theme.getOutlineThickness();
                draw.color(animationState.outlineColor);
                if (!enabled) {
                    draw.color(theme.getDisabledOutlineColor());
                }
                draw.voidRectangle(animationState.posX, animationState.posY, animationState.width, animationState.height, thickness, animationState.rotation, animationState.originX, animationState.originY);
                draw.color(animationState.baseColor);
                if (!enabled) {
                    draw.color(theme.getDisabledBaseColor());
                }
                draw.rectangle(animationState.posX + thickness, animationState.posY + thickness, animationState.width - 2 * thickness, animationState.height - 2 * thickness, animationState.rotation, animationState.originX, animationState.originY);
            } else {
                draw.color(animationState.baseColor);
                draw.rectangle(animationState.posX, animationState.posY, animationState.width, animationState.height, animationState.rotation, animationState.originX, animationState.originY);
            }
        }

        if (theme.getText_base() != null) {
            draw.color(theme.getText_base());
        } else if (theme.getText_gradient() != null) {
            draw.color(theme.getText_gradient());
        }
        if (!enabled) {
            draw.color(theme.getDisabledTextColor());
        } else {
            draw.text(chroma, animationState.posX + animationState.width / 2 - font.getWidth(text, animationState.height - textDistance() * 2) / 2, animationState.posY + textDistance(), animationState.height - textDistance() * 2, text, font, animationState.rotation, animationState.originX, animationState.originY);
        }
    }

    private int textDistance() {
        return getHeight() / 5;
    }

    @Override
    public void attachListener(EventListener listener) {
        if (listener instanceof ClickListener clickListener) {
            clickListeners.add(clickListener);
        }
    }

    @Override
    public void click(int x, int y, int btn) {
        if (!enabled) return;
        if (GuiUtils.mouseNotInside(initialState.posX, initialState.posY, initialState.width, initialState.height))
            return;
        animator.animate(theme.getAnimationInTime(), theme.getAnimationFrames());
        if (!clickListeners.isEmpty()) {
            clickListeners.forEach(l -> l.onCLick(this, btn));
        }
    }

    @Override
    public void clickRelease(int x, int y, int btn) {
        if (!enabled) return;
        animator.animateBack(theme.getAnimationOutTime(), theme.getAnimationFrames());
        if (GuiUtils.mouseNotInside(initialState.posX, initialState.posY, initialState.width, initialState.height))
            return;
        if (!clickListeners.isEmpty()) {
            clickListeners.forEach(l -> l.onRelease(this, btn));
        }
    }

    @Override
    public void disable() {
        enabled = false;
    }

    @Override
    public void enable() {
        enabled = true;
    }

    @Override
    public void toggle() {
        enabled = !enabled;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public void setHeight(int height) {
        super.setHeight(height);
        if (font == null) return;
        if (initialState.width < font.getWidth(text, initialState.height - textDistance()) + textDistance()) {
            setWidth(font.getWidth(text, initialState.height - textDistance()) + textDistance());
        }
    }
}
