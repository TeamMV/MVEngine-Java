package dev.mv.engine.gui.components;

import dev.mv.engine.gui.components.assets.GuiAssets;
import dev.mv.engine.gui.theme.Theme;
import dev.mv.engine.gui.utils.GuiUtils;
import dev.mv.engine.input.Input;
import dev.mv.engine.render.shared.DrawContext2D;
import dev.mv.engine.render.shared.Window;
import dev.mv.engine.render.shared.texture.Texture;
import dev.mv.engine.render.shared.texture.TextureRegion;

public class Checkbox extends ImageButton{
    private boolean checked = false;
    private TextureRegion tick;

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

    @Override
    public void draw(DrawContext2D draw) {
        super.draw(draw);
    }

    @Override
    public void setWidth(int width) {
        initialState.width = width;
        initialState.originX = initialState.posX + width / 2;
        initialState.height = width;
        initialState.originY = initialState.posY + width / 2;
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
        if(btn == Input.BUTTON_LEFT)
        if(!enabled) return;
        if(GuiUtils.mouseNotInside(initialState.posX, initialState.posY, initialState.width, initialState.height)) return;
        animator.animate(theme.getAnimationInTime(), theme.getAnimationFrames());
        if(clickListener != null) {
            clickListener.onCLick(this, btn);
        }
    }
    @Override
    public void clickRelease(int x, int y, int btn) {
        if(!enabled) return;
        animator.animateBack(theme.getAnimationOutTime(), theme.getAnimationFrames());
        if(GuiUtils.mouseNotInside(initialState.posX, initialState.posY, initialState.width, initialState.height)) return;
        invertChecked();
        if(clickListener != null) {
            clickListener.onRelease(this, btn);
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
        if(checked) {
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
    }
}
