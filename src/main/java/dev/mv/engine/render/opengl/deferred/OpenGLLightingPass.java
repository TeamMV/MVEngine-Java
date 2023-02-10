package dev.mv.engine.render.opengl.deferred;

import dev.mv.engine.render.shared.Window;
import dev.mv.engine.render.shared.create.RenderBuilder;
import dev.mv.engine.render.shared.deferred.LightingPass;
import dev.mv.engine.render.shared.shader.Shader;

import java.util.Arrays;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;

public class OpenGLLightingPass implements LightingPass {
    private Shader shader;
    private Window window;
    private int vboId, iboId;

    private int[] indices = new int[] {
        0, 1, 2, 0, 2, 3
    };

    private float[] vertices = new float[] {
        0, 0, 0,
        0, -1, 0,
        -1, -1, 0,
        -1, 0, 0
    };

    public OpenGLLightingPass(Window window) {
        this.window = window;
        this.shader = RenderBuilder.newShader("/shaders/3d/lightingPass.vert", "/shaders/3d/lightingPass.frag");
        vboId = glGenBuffers();
        iboId = glGenBuffers();
    }

    @Override
    public void render(int gPosition, int gNormal, int gAlbedoSpec) {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, gPosition);
        glActiveTexture(GL_TEXTURE1);
        glBindTexture(GL_TEXTURE_2D, gNormal);
        glActiveTexture(GL_TEXTURE2);
        glBindTexture(GL_TEXTURE_2D, gAlbedoSpec);

        shader.use();
        prepareUniforms();
        renderQuad();
    }

    private void prepareUniforms() {
        shader.uniform("viewPos", window.getCamera().getLocation());
        shader.uniform("uProjection", window.getProjectionMatrix2D());
        shader.uniform("gPosition", 0);
        shader.uniform("gNormal", 1);
        shader.uniform("gAlbedoSpec", 2);
    }

    private void renderQuad() {
        vertices[4] = window.getHeight();
        vertices[6] = window.getWidth();
        vertices[7] = window.getHeight();
        vertices[9] = window.getWidth();

        glBindBuffer(GL_ARRAY_BUFFER, vboId);
        glBufferData(GL_ARRAY_BUFFER, vertices, GL_DYNAMIC_DRAW);

        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, iboId);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices, GL_DYNAMIC_DRAW);

        System.out.println(Arrays.toString(vertices));

        glVertexAttribPointer(0, 3, GL_FLOAT, false, 3 * Float.BYTES, 0);
        glEnableVertexAttribArray(0);

        glDrawElements(GL_TRIANGLES, 6, GL_UNSIGNED_INT, 0);

        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
    }
}