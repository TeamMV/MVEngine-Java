package dev.mv.engine.render.shared.deferred;

import dev.mv.engine.render.shared.models.Entity;
import dev.mv.engine.render.shared.models.Model;

import java.util.List;
import java.util.Map;

public interface GeometryPass {
    void render(Map<Model, List<Entity>> entities);
}
