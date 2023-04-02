package dev.mv.engine.gui.components;

import dev.mv.engine.Looper;
import dev.mv.engine.MVEngine;
import dev.mv.engine.gui.components.extras.Image;
import dev.mv.engine.gui.components.extras.Toggle;
import dev.mv.engine.gui.event.ClickListener;
import dev.mv.engine.gui.event.EventListener;
import dev.mv.engine.gui.input.Clickable;
import dev.mv.engine.gui.theme.Theme;
import dev.mv.engine.gui.utils.GuiUtils;
import dev.mv.engine.gui.utils.VariablePosition;
import dev.mv.engine.render.shared.DrawContext2D;
import dev.mv.engine.render.shared.Window;
import dev.mv.engine.render.shared.texture.Texture;
import dev.mv.engine.render.shared.texture.TextureRegion;
import dev.mv.engine.resources.R;

public class ImageButton extends Element implements Toggle, Image, Clickable, Looper {
    protected TextureRegion texture;
    protected String textureResource;
    protected boolean enabled = true, useTextColor = false;

    public ImageButton(Window window, Element parent, int width, int height) {
        super(window, -1, -1, width, height, parent);
        MVEngine.instance().registerLooper(this);
    }

    public ImageButton(Window window, int x, int y, Element parent, int width, int height) {
        super(window, x, y, width, height, parent);
        MVEngine.instance().registerLooper(this);
    }

    public ImageButton(Window window, int x, int y, int width, int height) {
        super(window, x, y, width, height, null);
        MVEngine.instance().registerLooper(this);
    }

    public ImageButton(Window window, VariablePosition position, Element parent) {
        super(window, position, parent);
        MVEngine.instance().registerLooper(this);
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
    }

    @Override
    public void attachListener(EventListener listener) {
        if (listener instanceof ClickListener clickListener) {
            this.clickListeners.add(clickListener);
        }
    }

    @Override
    public void click(int x, int y, int btn) {
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

    public boolean usesTextColor() {
        return useTextColor;
    }

    public void setUseTextColor(boolean useTextColor) {
        this.useTextColor = useTextColor;
    }

    @Override
    public TextureRegion getTexture() {
        return texture;
    }

    @Override
    public void setTexture(Texture texture) {
        this.texture = texture.convertToRegion();
    }

    @Override
    public void setTexture(TextureRegion textureRegion) {
        this.texture = textureRegion;
    }

    public void setTexture(String texture) {
        textureResource = texture;
        this.texture = R.textures.get(textureResource);
    }

    @Override
    public void loop() {
        if (textureResource != null) {
            texture = R.textures.get(textureResource);
        }
    }
}
