package dev.mv.engine.gui.components;

import dev.mv.engine.gui.components.animations.TextAnimation;
import dev.mv.engine.gui.components.animations.TextAnimator;
import dev.mv.engine.gui.components.assets.GuiAssets;
import dev.mv.engine.gui.components.extras.Text;
import dev.mv.engine.gui.event.ClickListener;
import dev.mv.engine.gui.event.EventListener;
import dev.mv.engine.gui.theme.Theme;
import dev.mv.engine.gui.utils.GuiUtils;
import dev.mv.engine.gui.utils.VariablePosition;
import dev.mv.engine.input.Input;
import dev.mv.engine.render.shared.DrawContext2D;
import dev.mv.engine.render.shared.Window;
import dev.mv.engine.render.shared.font.BitmapFont;
import dev.mv.engine.render.shared.texture.Texture;
import dev.mv.engine.render.shared.texture.TextureRegion;

public class Checkbox extends ImageButton implements Text {
    private boolean checked = false;
    private TextureRegion tick;
    private BitmapFont font;
    private String text = "";
    private boolean chroma;

    public Checkbox(Window window, Element parent, int width, int height) {
        super(window, parent, width, height);
        setTexture(GuiAssets.TICK);
    }

    public Checkbox(Window window, int x, int y, Element parent, int width, int height) {
        super(window, x, y, parent, width, height);
        setTexture(GuiAssets.TICK);
    }

    public Checkbox(Window window, int x, int y, int width, int height) {
        super(window, x, y, width, height);
        setTexture(GuiAssets.TICK);
    }

    public Checkbox(Window window, VariablePosition position, Element parent) {
        super(window, position, parent);
        setTexture(GuiAssets.TICK);
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
                draw.voidRoundedRectangle(animationState.posX, animationState.posY, animationState.width, animationState.height, thickness, theme.getEdgeRadius() + thickness, theme.getEdgeRadius(), animationState.rotation, animationState.originX, animationState.originY);
                draw.color(animationState.baseColor);
                if (!enabled) {
                    draw.color(theme.getDisabledBaseColor());
                }
                draw.roundedRectangle(animationState.posX + thickness, animationState.posY + thickness, animationState.width - 2 * thickness, animationState.height - 2 * thickness, theme.getEdgeRadius(), theme.getEdgeRadius(), animationState.rotation, animationState.originX, animationState.originY);
                draw.color(0, 0, 0, 0);
                if (useTextColor) {
                    draw.color(animationState.textColor);
                }
                if (texture != null) {
                    draw.image(animationState.posX + theme.getEdgeRadius(), animationState.posY + theme.getEdgeRadius(), animationState.width - 2 * theme.getEdgeRadius(), animationState.height - 2 * theme.getEdgeRadius(), texture, animationState.rotation, animationState.originX, animationState.originY);
                }
            } else {
                draw.color(animationState.baseColor);
                if (!enabled) {
                    draw.color(theme.getDisabledBaseColor());
                }
                draw.roundedRectangle(animationState.posX, animationState.posY, animationState.width, animationState.height, theme.getEdgeRadius(), theme.getEdgeRadius(), animationState.rotation, animationState.originX, animationState.originY);
                draw.color(0, 0, 0, 0);
                if (useTextColor) {
                    draw.color(animationState.textColor);
                }
                if (texture != null) {
                    draw.image(animationState.posX, animationState.posY, animationState.width, animationState.height, texture, animationState.rotation, animationState.originX, animationState.originY);
                }
            }
        } else if (theme.getEdgeStyle() == Theme.EdgeStyle.TRIANGLE) {
            if (theme.hasOutline()) {
                int thickness = theme.getOutlineThickness();
                draw.color(animationState.outlineColor);
                if (!enabled) {
                    draw.color(theme.getDisabledOutlineColor());
                }
                draw.voidTriangularRectangle(animationState.posX, animationState.posY, animationState.width, animationState.height, thickness, theme.getEdgeRadius() + thickness, animationState.rotation, animationState.originX, animationState.originY);
                draw.color(animationState.baseColor);
                if (!enabled) {
                    draw.color(theme.getDisabledBaseColor());
                }
                draw.triangularRectangle(animationState.posX + thickness, animationState.posY + thickness, animationState.width - 2 * thickness, animationState.height - 2 * thickness, theme.getEdgeRadius(), animationState.rotation, animationState.originX, animationState.originY);
                draw.color(0, 0, 0, 0);
                if (useTextColor) {
                    draw.color(animationState.textColor);
                }
                if (texture != null) {
                    draw.image(animationState.posX + theme.getEdgeRadius(), animationState.posY + theme.getEdgeRadius(), animationState.width - 2 * theme.getEdgeRadius(), animationState.height - 2 * theme.getEdgeRadius(), texture, animationState.rotation, animationState.originX, animationState.originY);
                }
            } else {
                draw.color(animationState.baseColor);
                if (!enabled) {
                    draw.color(theme.getDisabledBaseColor());
                }
                draw.triangularRectangle(animationState.posX, animationState.posY, animationState.width, animationState.height, theme.getEdgeRadius(), animationState.rotation, animationState.originX, animationState.originY);
                draw.color(0, 0, 0, 0);
                if (useTextColor) {
                    draw.color(animationState.textColor);
                }
                if (texture != null) {
                    draw.image(animationState.posX, animationState.posY, animationState.width, animationState.height, texture, animationState.rotation, animationState.originX, animationState.originY);
                }
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
                draw.color(0, 0, 0, 0);
                if (useTextColor) {
                    draw.color(animationState.textColor);
                }
                if (texture != null) {
                    draw.image(animationState.posX + theme.getEdgeRadius(), animationState.posY + theme.getEdgeRadius(), animationState.width - 2 * theme.getEdgeRadius(), animationState.height - 2 * theme.getEdgeRadius(), texture, animationState.rotation, animationState.originX, animationState.originY);
                }
            } else {
                draw.color(animationState.baseColor);
                if (!enabled) {
                    draw.color(theme.getDisabledBaseColor());
                }
                draw.rectangle(animationState.posX, animationState.posY, animationState.width, animationState.height, animationState.rotation, animationState.originX, animationState.originY);
                draw.color(0, 0, 0, 0);
                if (useTextColor) {
                    draw.color(animationState.textColor);
                }
                if (texture != null) {
                    draw.image(animationState.posX, animationState.posY, animationState.width, animationState.height, texture, animationState.rotation, animationState.originX, animationState.originY);
                }
            }
        }

        draw.color(animationState.textColor);
        if (!enabled) {
            draw.color(theme.getDisabledTextColor());
        }

        draw.text(chroma, initialState.posX + initialState.width + 5, initialState.posY + textDistance(), initialState.height - textDistance() * 2, text, font, initialState.rotation, initialState.originX, initialState.originY);
    }

    protected int textDistance() {
        return getHeight() / 5;
    }

    @Override
    public void attachListener(EventListener listener) {
        if (listener instanceof ClickListener clickListener) {
            clickListeners.add(clickListener);
        }
    }

    @Override
    public void setHeight(int height) {
        initialState.width = height;
        initialState.originX = initialState.posX + height / 2;
        initialState.height = height;
        initialState.originY = initialState.posY + height / 2;
    }

    @Override
    public void click(int x, int y, int btn) {
        if (btn == Input.BUTTON_LEFT)
            if (!enabled) return;
        if (GuiUtils.mouseNotInside(initialState.posX, initialState.posY, initialState.width, initialState.height, theme))
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
        if (GuiUtils.mouseNotInside(initialState.posX, initialState.posY, initialState.width, initialState.height, theme))
            return;
        invertChecked();
        if (!clickListeners.isEmpty()) {
            clickListeners.forEach(l -> l.onRelease(this, btn));
        }
    }

    @Override
    public void setTexture(Texture texture) {
        tick = texture.convertToRegion();
    }

    @Override
    public void setTexture(TextureRegion texture) {
        tick = texture;
    }

    public void check() {
        checked = true;
        texture = tick;
    }

    public void uncheck() {
        checked = false;
        texture = null;
    }

    public void invertChecked() {
        checked = !checked;
        if (checked) {
            texture = tick;
        } else {
            texture = null;
        }
    }

    public boolean isChecked() {
        return checked;
    }

    @Override
    public void setTheme(Theme theme) {
        super.setTheme(theme);
        tick = GuiAssets.TICK;
        useTextColor = theme.isShouldCheckboxUseTextColor();
    }

    @Override
    public BitmapFont getFont() {
        return font;
    }

    @Override
    public void setFont(BitmapFont font) {
        this.font = font;
    }

    @Override
    public String getText() {
        return text;
    }

    @Override
    public void setText(String text) {
        this.text = text;
    }

    @Override
    public void setUseChroma(boolean chroma) {
        this.chroma = chroma;
    }

    @Override
    public int getWidth() {
        if (text.isEmpty()) {
            return super.getWidth();
        }

        if (font == null) return super.getWidth();

        return super.getWidth() + 10 + font.getWidth(text, getHeight() - textDistance() * 2);
    }

    @Override
    public void setWidth(int width) {
        initialState.width = width;
        initialState.originX = initialState.posX + width / 2;
        initialState.height = width;
        initialState.originY = initialState.posY + width / 2;
    }
}
