package dev.mv.engine.render.opengl.deferred;

import dev.mv.engine.render.shared.Transformations3D;
import dev.mv.engine.render.shared.Window;
import dev.mv.engine.render.shared.create.RenderBuilder;
import dev.mv.engine.render.shared.deferred.GeometryPass;
import dev.mv.engine.render.shared.models.Entity;
import dev.mv.engine.render.shared.models.Model;
import dev.mv.engine.render.shared.shader.Shader;
import dev.mv.utils.Utils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import java.util.List;
import java.util.Map;

import static org.lwjgl.opengl.GL30.*;

public class OpenGLGeometryPass implements GeometryPass {
    private GBuffer gBuffer;
    private Shader shader;
    private Window window;
    private OpenGLLightingPass lightingPass;

    public OpenGLGeometryPass(Window window) {
        this.window = window;
        shader = RenderBuilder.newShader("/shaders/3d/geometryPass.vert", "/shaders/3d/geometryPass.frag");
        shader.make(window);
        shader.bind();
        lightingPass = new OpenGLLightingPass(window);
        gBuffer = new GBuffer(window.getWidth(), window.getHeight());
    }

    private void bind(Model model) {
        GL30.glBindVertexArray(model.getId());
        GL20.glEnableVertexAttribArray(0);
        GL20.glEnableVertexAttribArray(1);
        GL20.glEnableVertexAttribArray(2);
        GL20.glEnableVertexAttribArray(3);
        Utils.ifNotNull(model.getTexture()).then(t -> t.bind(0));
    }

    private void unbind() {
        GL30.glBindVertexArray(0);
        GL20.glDisableVertexAttribArray(0);
        GL20.glDisableVertexAttribArray(1);
        GL20.glDisableVertexAttribArray(2);
        GL20.glDisableVertexAttribArray(3);
        GL11.glBindTexture(GL_TEXTURE_2D, 0);
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
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
        gBuffer.bind();
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

        gBuffer.unbind();

        lightingPass.render(gBuffer.getPosition(), gBuffer.getNormal(), gBuffer.getAlbedoSpec());
    }
}
