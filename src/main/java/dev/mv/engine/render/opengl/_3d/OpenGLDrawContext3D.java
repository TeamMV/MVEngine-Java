package dev.mv.engine.render.opengl._3d;

import dev.mv.engine.render.DrawContext3D;
import dev.mv.engine.render.opengl.OpenGLWindow;
import dev.mv.engine.render.opengl._3d.camera.OpenGLCamera3D;

public class OpenGLDrawContext3D implements DrawContext3D {
    private OpenGLCamera3D camera3D;

    public OpenGLDrawContext3D(OpenGLWindow window) {
        camera3D = new OpenGLCamera3D();
        camera3D.moveTo(0, 0, 0.01f);
    }

    @Override
    public OpenGLCamera3D getCamera() {
        return camera3D;
    }
}
