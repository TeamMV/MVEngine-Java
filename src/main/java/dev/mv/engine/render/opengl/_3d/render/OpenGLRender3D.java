package dev.mv.engine.render.opengl._3d.render;

import dev.mv.engine.render.Window;
import dev.mv.engine.render.drawables.Texture;
import dev.mv.engine.render.models.Entity;
import dev.mv.engine.render.models.Model;
import dev.mv.engine.render.opengl.OpenGLWindow;
import dev.mv.engine.render.opengl._3d.camera.OpenGLTransformation3D;
import dev.mv.engine.render.opengl.shader.OpenGLShader;
import lombok.Getter;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.lwjgl.opengl.GL11.*;

public class OpenGLRender3D {
    private Window win;

    private Map<Model, List<Entity>> modelUsages = new HashMap<>();
    @Getter
    private OpenGLShader shader;

    public OpenGLRender3D(Window win) {
        this.win = win;
        this.shader = new OpenGLShader("src/main/java/dev/mv/engine/render/opengl/_3d/shaderfiles/vertex/default.vert",
            "src/main/java/dev/mv/engine/render/opengl/_3d/shaderfiles/fragment/default.frag");
//
        shader.make();
        shader.use();
    }


    private void bind(Model model) {
        GL30.glBindVertexArray(model.getId());
        GL20.glEnableVertexAttribArray(0);
        GL20.glEnableVertexAttribArray(1);
        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL11.glBindTexture(GL_TEXTURE_2D, model.getTexture().getId());
    }

    private void unbind() {
        GL30.glBindVertexArray(0);
        GL20.glDisableVertexAttribArray(0);
        GL20.glDisableVertexAttribArray(1);
        GL11.glBindTexture(GL_TEXTURE_2D, 0);
    }

    private void prepare(Entity entity) {
        shader.setUniform1i("TEX_SAMPLER", 0);
        shader.setMatrix4f("uTransform", OpenGLTransformation3D.getTransformationMatrix(entity));
        shader.setMatrix4f("uProjection", win.getProjectionMatrix3D());
        shader.setMatrix4f("uView", OpenGLTransformation3D.getViewMatrix(win.getDrawContext3D().getCamera()));
    }

    public void render() {

        for(Model model : modelUsages.keySet()) {
            bind(model);
            List<Entity> entities = modelUsages.get(model);
            for(Entity entity : entities) {
                prepare(entity);
                glDrawElements(GL_TRIANGLES, model.vertexCount(), GL_UNSIGNED_INT, 0);
            }
            unbind();
        }
        modelUsages.clear();
    }

    public void processEntity(Entity entity) {
        try {
            List<Entity> entityList = modelUsages.get(entity.getModel());
            entityList.add(entity);
        } catch (NullPointerException e) {
            List<Entity> entityList = new ArrayList<>();
            entityList.add(entity);
            modelUsages.put(entity.getModel(), entityList);
        }
    }
}
