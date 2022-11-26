package dev.mv.engine.render.shared;

import dev.mv.engine.render.opengl.OpenGLRender3D;
import dev.mv.engine.render.shared.models.Entity;

public class DrawContext3D {
    private static Render3D ctx3D = null;

    public DrawContext3D(Window window) {
        ctx3D = window.getRender3D();
    }

    public void object(Entity entity)  {
        if(ctx3D == null) {
            return;
        } else {
            ctx3D.entity(entity);
        }
    }
}
