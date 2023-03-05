package dev.mv.engine.render.shared;

import dev.mv.engine.render.shared.batch.Vertex;
import dev.mv.engine.render.shared.batch.VertexGroup;
import dev.mv.engine.render.shared.models.Entity;

public class DrawContext3D {
    protected Window window;
    private Render3D ctx3D = null;
    private NormalizedColor color;
    private VertexGroup verts = new VertexGroup();
    private Vertex v1 = new Vertex(), v2 = new Vertex(), v3 = new Vertex(), v4 = new Vertex();
    private boolean useCamera = true;

    public DrawContext3D(Window window) {
        this.window = window;
        ctx3D = window.getRender3D();
        color = new NormalizedColor(0, 0, 0, 0);
    }

    public void entity(Entity entity) {
        if (ctx3D == null) {
            return;
        } else {
            ctx3D.entity(entity);
        }
    }

    public void color(float r, float g, float b, float a) {
        color.set(r / 255f, g / 255f, b / 255f, a / 255f);
    }

    public void color(Color color) {
        color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
    }

    public void point(int x, int y, int z) {
        window.getBatchController3D().pushVertex(
            v1.put(x, y, z, color.r, color.g, color.b, color.a)
        );
    }

    public void end() {
        window.getBatchController3D().end();
    }
}