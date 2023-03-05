package dev.mv.engine.gui.components;

import dev.mv.engine.gui.components.extras.Image;
import dev.mv.engine.gui.event.EventListener;
import dev.mv.engine.gui.utils.VariablePosition;
import dev.mv.engine.render.shared.DrawContext2D;
import dev.mv.engine.render.shared.Window;
import dev.mv.engine.render.shared.texture.Texture;
import dev.mv.engine.render.shared.texture.TextureRegion;

public class Picture extends Element implements Image {
    private TextureRegion texture;

    public Picture(Window window, int x, int y, int width, int height, Element parent) {
        super(window, x, y, width, height, parent);
    }

    public Picture(Window window, VariablePosition position, Element parent) {
        super(window, position, parent);
    }

    @Override
    public void draw(DrawContext2D draw) {
        draw.color(0, 0, 0, 0);
        draw.image(animationState.posX, animationState.posY, animationState.width, animationState.height, texture, animationState.rotation, animationState.originX, animationState.originY);
    }

    @Override
    public void attachListener(EventListener listener) {

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
}
