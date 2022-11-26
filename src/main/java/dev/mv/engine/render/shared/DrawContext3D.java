package dev.mv.engine.render.shared;

import dev.mv.engine.render.opengl.OpenGL3DRenderingContext;
import dev.mv.engine.render.shared.models.Entity;

public class DrawContext3D {
    private static OpenGL3DRenderingContext ctx3D = null;

    public DrawContext3D(Window window) {
        ctx3D = new OpenGL3DRenderingContext(window);
    }

    public static void object(Entity entity)  {
        if(ctx3D == null) {
            return;
        } else {
            ctx3D.processEntity(entity);
        }
    }
}
