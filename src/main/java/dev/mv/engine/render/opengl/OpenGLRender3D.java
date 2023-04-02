package dev.mv.engine.render.opengl;

import dev.mv.engine.render.opengl.deferred.OpenGLGeometryPass;
import dev.mv.engine.render.shared.Render3D;
import dev.mv.engine.render.shared.Transformations3D;
import dev.mv.engine.render.shared.Window;
import dev.mv.engine.render.shared.batch.Batch3D;
import dev.mv.engine.render.shared.models.Entity;
import dev.mv.engine.render.shared.models.Model;
import dev.mv.engine.render.shared.shader.Shader;
import dev.mv.engine.render.shared.shader.light.DirectionalLight;
import dev.mv.engine.render.shared.shader.light.PointLight;
import dev.mv.engine.render.shared.shader.light.SpotLight;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.lwjgl.opengl.GL46.*;

public class OpenGLRender3D implements Render3D {
    private Window win;

    private Map<Model, List<Entity>> modelUsages = new HashMap<>();

    private List<PointLight> pointLights = new ArrayList<>();
    private List<SpotLight> spotLights = new ArrayList<>();

    private DirectionalLight directionalLight;
    private OpenGLGeometryPass geometryPass;

    public OpenGLRender3D(Window window) {
        this.win = window;
        geometryPass = new OpenGLGeometryPass(window);
    }

    @Override
    public void render() {
        geometryPass.render(modelUsages);

        //renderLights(pointLights, spotLights);


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
