package dev.mv.engine.render.opengl;

import dev.mv.engine.render.shared.Render3D;
import dev.mv.engine.render.shared.Transformations3D;
import dev.mv.engine.render.shared.Window;
import dev.mv.engine.render.shared.batch.Batch;
import dev.mv.engine.render.shared.batch.Batch3D;
import dev.mv.engine.render.shared.create.RenderBuilder;
import dev.mv.engine.render.shared.models.Entity;
import dev.mv.engine.render.shared.models.Model;
import dev.mv.engine.render.shared.shader.Shader;
import dev.mv.engine.render.shared.shader.Uniforms3D;
import dev.mv.engine.render.shared.shader.light.DirectionalLight;
import dev.mv.engine.render.shared.shader.light.PointLight;
import dev.mv.engine.render.shared.shader.light.SpotLight;
import dev.mv.engine.render.shared.texture.Texture;
import dev.mv.engine.render.utils.RenderConstansts;
import dev.mv.utils.Utils;
import lombok.Getter;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import java.util.*;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL15.GL_ELEMENT_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;

public class OpenGLRender3D implements Render3D {
    private Window win;

    private Map<Model, List<Entity>> modelUsages = new HashMap<>();

    private List<PointLight> pointLights = new ArrayList<>();
    private List<SpotLight> spotLights = new ArrayList<>();

    private DirectionalLight directionalLight;
    @Getter
    private Shader shader;

    public OpenGLRender3D(Window window) {
        this.win = window;
        this.shader = RenderBuilder.newShader("/shaders/3d/default.vert", "/shaders/3d/default.frag");

        shader.make(window);
        shader.bind();
    }


    private void bind(Model model) {
        GL30.glBindVertexArray(model.getId());
        GL20.glEnableVertexAttribArray(0);
        GL20.glEnableVertexAttribArray(1);
        GL20.glEnableVertexAttribArray(2);
        GL20.glEnableVertexAttribArray(3);
        Utils.ifNotNull(model.getTexture()).then(t -> t.bind(0));
        Uniforms3D.material(shader, model.getMaterial(), "uMaterial");
    }

    private void unbind() {
        GL30.glBindVertexArray(0);
        GL20.glDisableVertexAttribArray(0);
        GL20.glDisableVertexAttribArray(1);
        GL20.glDisableVertexAttribArray(2);
        GL20.glDisableVertexAttribArray(3);
        GL11.glBindTexture(GL_TEXTURE_2D, 0);
    }

    private void prepare(Entity entity) {
        shader.uniform("uTexSampler", 0);
        shader.uniform("uTransform", Transformations3D.getTransformationMatrix(entity));
        shader.uniform("uProjection", win.getProjectionMatrix3D());
        shader.uniform("uView", Transformations3D.getViewMatrix(win.getCamera()));
    }

    public void renderLights(List<PointLight> pointLights, List<SpotLight> spotLights) {
        shader.uniform("uAmbient", RenderConstansts.AMBIENT_LIGHT_COLOR);
        shader.uniform("uSpecularPower", RenderConstansts.SPECULAR_POWER);
        //shader.setPointLights("uPointLights", pointLights);
        //shader.setSpotLights("uSpotLights", spotLights);
        //shader.setDirectionalLight("uDirectionalLight", directionalLight);
    }

    @Override
    public void render() {
        shader.use();

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

    @Override
    public void retrieveVertexData(int[] indices, float[] data, int vboId, int iboId, Shader shader, int renderMode, int amount) {
        if (win == null) {
            throw new IllegalStateException("Window is not set!");
        }

        glBindBuffer(GL_ARRAY_BUFFER, vboId);
        glBufferData(GL_ARRAY_BUFFER, data, GL_DYNAMIC_DRAW);

        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, iboId);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices, GL_DYNAMIC_DRAW);

        shader.uniform("uResX", (float) win.getWidth());
        shader.uniform("uResY", (float) win.getHeight());

        shader.uniform("uProjection", win.getProjectionMatrix3D());
        shader.uniform("uTransform", Transformations3D.IDENTITY);
        shader.uniform("uView", Transformations3D.getViewMatrix(win.getCamera()));

        glVertexAttribPointer(0, Batch3D.POSITION_SIZE, GL_FLOAT, false, Batch3D.VERTEX_SIZE_BYTES, Batch3D.POSITION_OFFSET_BYTES);
        glEnableVertexAttribArray(0);
        glVertexAttribPointer(1, Batch3D.COLOR_SIZE, GL_FLOAT, false, Batch3D.VERTEX_SIZE_BYTES, Batch3D.COLOR_OFFSET_BYTES);
        glEnableVertexAttribArray(1);

        glDrawElements(renderMode, amount, GL_UNSIGNED_INT, 0);

        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
    }

    @Override
    public void entity(Entity entity) {
        List<Entity> entityList = modelUsages.get(entity.getModel());
        if (entityList != null) {
            entityList.add(entity);
        } else {
            entityList = new ArrayList<>();
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
