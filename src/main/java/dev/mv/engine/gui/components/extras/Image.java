package dev.mv.engine.gui.components.extras;

import dev.mv.engine.render.shared.texture.Texture;
import dev.mv.engine.render.shared.texture.TextureRegion;

public interface Image {
    void setTexture(Texture texture);
    void setTexture(TextureRegion textureRegion);
    TextureRegion getTexture();
}
