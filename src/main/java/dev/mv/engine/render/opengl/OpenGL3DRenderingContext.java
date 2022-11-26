package dev.mv.engine.render.opengl;

import dev.mv.engine.render.shared.Transformations3D;
import dev.mv.engine.render.shared.Window;
import dev.mv.engine.render.shared.create.RenderBuilder;
import dev.mv.engine.render.shared.models.Entity;
import dev.mv.engine.render.shared.models.Model;
import dev.mv.engine.render.shared.shader.Shader;
import dev.mv.engine.render.shared.shader.Uniforms3D;
import dev.mv.engine.render.shared.shader.light.DirectionalLight;
import dev.mv.engine.render.shared.shader.light.PointLight;
import dev.mv.engine.render.shared.shader.light.SpotLight;
import dev.mv.engine.render.utils.RenderConstansts;
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

public class OpenGL3DRenderingContext {
    private Window win;

    private Map<Model, List<Entity>> modelUsages = new HashMap<>();

    private List<PointLight> pointLights = new ArrayList<>();
    private List<SpotLight> spotLights = new ArrayList<>();

    private DirectionalLight directionalLight;
    @Getter
    private Shader shader;

    public OpenGL3DRenderingContext(Window win) {
        this.win = win;
        this.shader = RenderBuilder.newShader("/shaders/3d/default.vert", "/shaders/3d/default.frag");

        shader.make(win);
        shader.use();
    }


    private void bind(Model model) {
        GL30.glBindVertexArray(model.getId());
        GL20.glEnableVertexAttribArray(0);
        GL20.glEnableVertexAttribArray(1);
        GL20.glEnableVertexAttribArray(2);
        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        if (model.getTexture() != null) {
            GL11.glBindTexture(GL_TEXTURE_2D, model.getTexture().getId());
        }
        Uniforms3D.material(shader, model.getMaterial(), "uMaterial");
    }

    private void unbind() {
        GL30.glBindVertexArray(0);
        GL20.glDisableVertexAttribArray(0);
        GL20.glDisableVertexAttribArray(1);
        GL20.glDisableVertexAttribArray(2);
        GL11.glBindTexture(GL_TEXTURE_2D, 0);
    }

    private void prepare(Entity entity) {
        shader.uniform("uTexSampler", 0);
        shader.uniform("uTransform", Transformations3D.getTransformationMatrix(entity));
        shader.uniform("uProjection", win.getProjectionMatrix3D());
        //shader.setMatrix4f("uView", OpenGLTransformation3D.getViewMatrix(win.getDrawContext3D().getCamera()));
    }

    public void renderLights(List<PointLight> pointLights, List<SpotLight> spotLights) {
        shader.uniform("uAmbient", RenderConstansts.AMBIENT_LIGHT_COLOR);
        shader.uniform("uSpecularPower", RenderConstansts.SPECULAR_POWER);
        //shader.setPointLights("uPointLights", pointLights);
        //shader.setSpotLights("uSpotLights", spotLights);
        //shader.setDirectionalLight("uDirectionalLight", directionalLight);
    }

    public void render() {

        //renderLights(pointLights, spotLights);

        for (Model model : modelUsages.keySet()) {
            bind(model);
            List<Entity> entities = modelUsages.get(model);
            for (Entity entity : entities) {
                prepare(entity);
                glDrawElements(GL_TRIANGLES, model.vertexCount(), GL_UNSIGNED_INT, 0);
            }
            unbind();
        }
        modelUsages.clear();
        pointLights.clear();
        spotLights.clear();
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

    public void processPointLight(PointLight light) {
        pointLights.add(light);
    }

    public void processSpotLight(SpotLight light) {
        spotLights.add(light);
    }

    public void processDirectionalLight(DirectionalLight light) {
        directionalLight = light;
    }
}
