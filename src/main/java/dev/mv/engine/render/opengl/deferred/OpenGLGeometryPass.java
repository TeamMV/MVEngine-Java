package dev.mv.engine.render.opengl.deferred;

import dev.mv.engine.render.shared.Transformations3D;
import dev.mv.engine.render.shared.Window;
import dev.mv.engine.render.shared.create.RenderBuilder;
import dev.mv.engine.render.shared.deferred.GeometryPass;
import dev.mv.engine.render.shared.models.Entity;
import dev.mv.engine.render.shared.models.Model;
import dev.mv.engine.render.shared.shader.Shader;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import java.util.List;
import java.util.Map;

import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public class OpenGLGeometryPass implements GeometryPass {
    private int gBuffer;
    private int gPosition, gNormal, gAlbedoSpec;
    private Shader shader;
    private Window window;
    private OpenGLLightingPass lightingPass;

    public OpenGLGeometryPass(Window window) {
        this.window = window;
        this.shader = RenderBuilder.newShader("/shaders/3d/geometryPass.vert", "/shaders/3d/geometryPass.frag");

        lightingPass = new OpenGLLightingPass(window);

        gBuffer = glGenFramebuffers();

        glBindFramebuffer(GL_FRAMEBUFFER, gBuffer);

        gPosition = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, gPosition);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA16F, window.getWidth(), window.getHeight(), 0, GL_RGBA, GL_FLOAT, NULL);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, gPosition, 0);

        gNormal = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, gNormal);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA16F, window.getWidth(), window.getHeight(), 0, GL_RGBA, GL_FLOAT, NULL);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT1, GL_TEXTURE_2D, gNormal, 0);

        gAlbedoSpec = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, gAlbedoSpec);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, window.getWidth(), window.getHeight(), 0, GL_RGBA, GL_UNSIGNED_BYTE, NULL);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT2, GL_TEXTURE_2D, gAlbedoSpec, 0);

        int[] attachments = new int[]{GL_COLOR_ATTACHMENT0, GL_COLOR_ATTACHMENT1, GL_COLOR_ATTACHMENT2};
        glDrawBuffers(attachments);
    }

    private void bind(Model model) {
        GL30.glBindVertexArray(model.getId());
        GL20.glEnableVertexAttribArray(0);
        GL20.glEnableVertexAttribArray(1);
        GL20.glEnableVertexAttribArray(2);
        GL20.glEnableVertexAttribArray(3);
        //Utils.ifNotNull(model.getTexture()).then(t -> t.bind(0));
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
        shader.uniform("texture_diffuse1", 0);
        shader.uniform("texture_specular1", 1);
        shader.uniform("uTransform", Transformations3D.getTransformationMatrix(entity));
        shader.uniform("uProjection", window.getProjectionMatrix3D());
        shader.uniform("uView", Transformations3D.getViewMatrix(window.getCamera()));
    }

    @Override
    public void render(Map<Model, List<Entity>> entities) {
        glBindFramebuffer(GL_FRAMEBUFFER, gBuffer);
        shader.use();

        for (Model model : entities.keySet()) {
            bind(model);
            List<Entity> entityList = entities.get(model);
            for (Entity entity : entityList) {
                prepare(entity);
                glDrawBuffers(GL_FRAMEBUFFER);
                lightingPass.render(gPosition, gNormal, gAlbedoSpec);
            }
            unbind();
        }

        glBindFramebuffer(GL_FRAMEBUFFER, 0);
    }
}
