package dev.mv.engine.render.shared;

import dev.mv.engine.render.shared.models.Entity;

public interface Render3D {
    void entity(Entity entity);

    void render();
}
