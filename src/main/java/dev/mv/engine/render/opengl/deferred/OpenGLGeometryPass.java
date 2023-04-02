package dev.mv.engine.render.opengl.deferred;

import dev.mv.engine.render.shared.Transformations3D;
import dev.mv.engine.render.shared.Window;
import dev.mv.engine.render.shared.create.RenderBuilder;
import dev.mv.engine.render.shared.deferred.GeometryPass;
import dev.mv.engine.render.shared.models.Entity;
import dev.mv.engine.render.shared.models.Model;
import dev.mv.engine.render.shared.shader.Shader;
import dev.mv.utils.Utils;

import java.util.List;
import java.util.Map;

import static org.lwjgl.opengl.GL46.*;

public class OpenGLGeometryPass implements GeometryPass {
    private GBuffer gBuffer;
    private Shader shader;
    private Window window;
    private OpenGLLightingPass lightingPass;

    public OpenGLGeometryPass(Window window) {
        this.window = window;
        shader = RenderBuilder.newShader("/assets/mvengine/shaders/3d/default.vert", "/assets/mvengine/shaders/3d/default.frag");
        shader.make(window);
        shader.bind();
        lightingPass = new OpenGLLightingPass(window);
        gBuffer = new GBuffer(window.getWidth(), window.getHeight());
    }

    private void bind(Model model) {
        glBindVertexArray(model.getId());
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);
        glEnableVertexAttribArray(2);
        glEnableVertexAttribArray(3);
        Utils.ifNotNull(model.getTexture()).then(t -> t.bind(0));
    }

    private void unbind() {
        glBindVertexArray(0);
        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);
        glDisableVertexAttribArray(2);
        glDisableVertexAttribArray(3);
        glBindTexture(GL_TEXTURE_2D, 0);
    }

    private void prepare(Entity entity) {
        shader.uniform("uDiffuse", 0);
        shader.uniform("uSpecular", 1);
        shader.uniform("uTransform", Transformations3D.getTransformationMatrix(entity));
        shader.uniform("uProjection", window.getProjectionMatrix3D());
        shader.uniform("uView", Transformations3D.getViewMatrix(window.getCamera()));
    }

    @Override
    public void render(Map<Model, List<Entity>> entities) {
        //gBuffer.bind();
        shader.use();

        for (Model model : entities.keySet()) {
            bind(model);
            List<Entity> entityList = entities.get(model);
            for (Entity entity : entityList) {
                prepare(entity);
                glDrawElements(GL_TRIANGLES, model.vertexCount(), GL_UNSIGNED_INT, 0);
            }
            unbind();
        }

        //gBuffer.unbind();

        //lightingPass.render(gBuffer.getPosition(), gBuffer.getNormal(), gBuffer.getAlbedoSpec());
    }
}
