package dev.mv.engine.render.opengl._3d.object;

import dev.mv.engine.render.models.Model;
import dev.mv.engine.render.models.ObjectLoader;
import dev.mv.engine.render.opengl.glutils.OpenGLUtils;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import java.io.IOException;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class OpenGLObjectLoader implements ObjectLoader {
    private static OpenGLObjectLoader instance = new OpenGLObjectLoader();

    private List<Integer> vaos = new ArrayList<>();
    private List<Integer> vbos = new ArrayList<>();

    private OpenGLObjectLoader() {

    }

    @Override
    public Model loadModel(float[] vertices) {
        int vaoId = createVAO();
        storeDataInAttributeList(0, 3, vertices);
        unbindVAO();
        return new OpenGLModel(vaoId, vertices.length / 3);
    }

    @Override
    public Model loadExternalModel(String filepath) throws IOException {
        return null;
    }

    private int createVAO() {
        int vao = GL30.glGenVertexArrays();
        vaos.add(vao);
        GL30.glBindVertexArray(vao);
        return vao;
    }

    private void storeDataInAttributeList(int attribNumber, int attribSize, float[] data) {
        int vbo = GL15.glGenBuffers();
        vbos.add(vbo);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo);
        FloatBuffer buffer = OpenGLUtils.store(data);
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
        GL20.glVertexAttribPointer(attribNumber, attribSize, GL11.GL_FLOAT, false, 0, 0);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
    }

    private void unbindVAO() {
        GL30.glBindVertexArray(0);
    }

    public void cleanup() {
        for(int vao : vaos) {
            GL30.glDeleteVertexArrays(vao);
        }
        for(int vbo : vbos) {
            GL30.glDeleteBuffers(vbo);
        }
    }

    public static OpenGLObjectLoader instance() {
        return instance;
    }
}
