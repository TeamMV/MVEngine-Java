package dev.mv.engine.gui.components;

import dev.mv.engine.gui.components.assets.GuiAssets;
import dev.mv.engine.gui.components.extras.Text;
import dev.mv.engine.gui.components.layouts.ChoiceGroup;
import dev.mv.engine.gui.theme.Theme;
import dev.mv.engine.gui.utils.GuiUtils;
import dev.mv.engine.input.Input;
import dev.mv.engine.render.shared.DrawContext2D;
import dev.mv.engine.render.shared.Window;
import dev.mv.engine.render.shared.font.BitmapFont;
import dev.mv.engine.render.shared.texture.Texture;
import dev.mv.engine.render.shared.texture.TextureRegion;

public class Choice extends Checkbox {
    private boolean checked = false;
    private TextureRegion dot;

    public Choice(Window window, Element parent, int width, int height) {
        super(window, parent, width, height);
        setTexture(GuiAssets.DOT);
    }

    public Choice(Window window, int x, int y, Element parent, int width, int height) {
        super(window, x, y, parent, width, height);
        setTexture(GuiAssets.DOT);
    }

    public Choice(Window window, int x, int y, int width, int height) {
        super(window, x, y, width, height);
        setTexture(GuiAssets.DOT);
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
        if(!clickListeners.isEmpty()) {
            clickListeners.forEach(l -> l.onCLick(this, btn));
        }
    }
    @Override
    public void clickRelease(int x, int y, int btn) {
        if(!enabled) return;
        animator.animateBack(theme.getAnimationOutTime(), theme.getAnimationFrames());
        if(GuiUtils.mouseNotInside(initialState.posX, initialState.posY, initialState.width, initialState.height)) return;
        check();
        if(!clickListeners.isEmpty()) {
            clickListeners.forEach(l -> l.onRelease(this, btn));
        }
    }

    @Override
    public void setTexture(Texture texture) {
        dot = texture.convertToRegion();
    }

    @Override
    public void setTexture(TextureRegion texture) {
        dot = texture;
    }

    public void check() {
        if(!checked) {
            checked = true;
            ((ChoiceGroup) parent).setCurrentChoice(this);
        }
        checked = true;
        texture = dot;
    }

    public void uncheck() {
        checked = false;
        texture = null;
    }

    public void invertChecked() {
        if(checked) {
            uncheck();
        } else {
            check();
        }
    }

    public boolean isChecked() {
        return checked;
    }

    @Override
    public void setTheme(Theme theme) {
        super.setTheme(theme);
        dot = GuiAssets.DOT;
        useTextColor = theme.isShouldChoiceUseTextColor();
    }
}
