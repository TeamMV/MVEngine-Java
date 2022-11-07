package dev.mv.engine.render.opengl._3d.object;

import dev.mv.engine.render.drawables.Texture;
import dev.mv.engine.render.models.Model;
import dev.mv.engine.render.models.ObjectLoader;
import dev.mv.engine.render.utils.RenderUtils;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector3i;
import org.lwjgl.PointerBuffer;
import org.lwjgl.assimp.AIMesh;
import org.lwjgl.assimp.AIScene;
import org.lwjgl.assimp.Assimp;
import org.lwjgl.opengl.GL30;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.*;

public class OpenGLObjectLoader implements ObjectLoader {
    private static OpenGLObjectLoader instance = new OpenGLObjectLoader();
    private List<Integer> vaos = new ArrayList<>();
    private List<Integer> vbos = new ArrayList<>();
    private List<Integer> textures = new ArrayList<>();

    private OpenGLObjectLoader() {

    }

    public static OpenGLObjectLoader instance() {
        return instance;
    }

    private static void processVertex(int pos, int texCoord, int normal, List<Vector2f> texCoordList, List<Vector3f> normalList, List<Integer> indicesList, float[] texCoordArr, float[] normalArr) {
        indicesList.add(pos);

        if (texCoord >= 0) {
            Vector2f vTexCoord = texCoordList.get(texCoord);
            texCoordArr[pos * 2] = vTexCoord.x;
            texCoordArr[pos * 2 + 1] = 1 - vTexCoord.y;
        }

        if (normal >= 0) {
            Vector3f vNormal = normalList.get(normal);
            normalArr[pos * 3] = vNormal.x;
            normalArr[pos * 3 + 1] = vNormal.y;
            normalArr[pos * 3 + 2] = vNormal.z;
        }
    }

    private static void processFaces(String token, List<Vector3i> faces) {
        String[] lineToken = token.split("/");
        int length = lineToken.length;
        int pos = -1, coords = -1, normal = -1;
        pos = Integer.parseInt(lineToken[0]) - 1;
        if (length > 1) {
            String texCoord = lineToken[1];
            coords = texCoord.length() > 0 ? Integer.parseInt(texCoord) - 1 : -1;

            if (length > 2) {
                normal = Integer.parseInt(lineToken[2]) - 1;
            }
        }

        Vector3i vFaces = new Vector3i(pos, coords, normal);
        faces.add(vFaces);
    }

    @Override
    public Model loadModel(float[] vertices, float[] texCoords, float[] normals, int[] indices) {
        int id = createVAO();
        storeIndicesBuffer(indices);
        storeDataInAttribList(0, 3, vertices);
        storeDataInAttribList(1, 2, texCoords);
        storeDataInAttribList(2, 3, normals);
        unbind();
        return new OpenGLModel(id, indices.length);
    }

    public void loadModelAssimp(String path) {
        AIScene scene = Assimp.aiImportFile(path, Assimp.aiProcess_Triangulate);
        PointerBuffer buffer = scene.mMeshes();

        for (int i = 0; i < buffer.limit(); i++) {
            AIMesh mesh = AIMesh.create(buffer.get(i));
        }
    }

    @Override
    public Model loadExternalModel(String path) throws IOException {
        List<String> lines = readAllLines(path);
        List<Vector3f> vertices = new ArrayList<>();
        List<Vector3f> normals = new ArrayList<>();
        List<Vector2f> textures = new ArrayList<>();
        List<Vector3i> faces = new ArrayList<>();

        for (String line : lines) {
            String[] tokens = line.split("\\s+");
            switch (tokens[0]) {
                case "v":
                    Vector3f vVertices = new Vector3f(
                        Float.parseFloat(tokens[1]),
                        Float.parseFloat(tokens[2]),
                        Float.parseFloat(tokens[3])
                    );
                    vertices.add(vVertices);
                    break;
                case "vt":
                    Vector2f vTextures = new Vector2f(
                        Float.parseFloat(tokens[1]),
                        Float.parseFloat(tokens[2])
                    );
                    textures.add(vTextures);
                    break;
                case "vn":
                    Vector3f vNormals = new Vector3f(
                        Float.parseFloat(tokens[1]),
                        Float.parseFloat(tokens[2]),
                        Float.parseFloat(tokens[3])
                    );
                    normals.add(vNormals);
                    break;
                case "f":
                    processFaces(tokens[1], faces);
                    processFaces(tokens[2], faces);
                    processFaces(tokens[3], faces);
                    break;
                default:
                    break;
            }
        }

        List<Integer> indices = new ArrayList<>();
        float[] verticesArr = new float[vertices.size() * 3];
        int i = 0;
        for (Vector3f pos : vertices) {
            verticesArr[i * 3] = pos.x;
            verticesArr[i * 3 + 1] = pos.y;
            verticesArr[i * 3 + 2] = pos.z;
            i++;
        }

        float[] texCoordArr = new float[vertices.size() * 2];
        float[] normalArr = new float[vertices.size() * 3];

        for (Vector3i face : faces) {
            processVertex(face.x, face.y, face.z, textures, normals, indices, texCoordArr, normalArr);
        }

        int[] indicesArr = indices.stream().mapToInt(v -> v).toArray();

        return loadModel(verticesArr, texCoordArr, normalArr, indicesArr);
    }

    private List<String> readAllLines(String file) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(file));
        List<String> outputList = new ArrayList<>();
        String line;
        while ((line = reader.readLine()) != null) {
            outputList.add(line);
        }
        return outputList;
    }

    public int registerTexture(Texture texture) {
        textures.add(texture.getId());
        return texture.getId();
    }

    private int createVAO() {
        int id = glGenVertexArrays();
        vaos.add(id);
        glBindVertexArray(id);
        return id;
    }

    private void storeIndicesBuffer(int[] indices) {
        int vbo = glGenBuffers();
        vbos.add(vbo);
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, vbo);
        IntBuffer buffer = RenderUtils.store(indices);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, buffer, GL_STATIC_DRAW);
    }

    private void storeDataInAttribList(int attribNumber, int vertexCount, float[] data) {
        int vbo = glGenBuffers();
        vbos.add(vbo);
        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        FloatBuffer buffer = RenderUtils.store(data);
        glBufferData(GL_ARRAY_BUFFER, buffer, GL_STATIC_DRAW);
        glVertexAttribPointer(attribNumber, vertexCount, GL_FLOAT, false, 0, 0);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
    }

    private void unbind() {
        GL30.glBindVertexArray(0);
    }

    private void cleanup() {
        for (int vao : vaos) {
            glDeleteVertexArrays(vao);
        }
        for (int vbo : vbos) {
            GL30.glDeleteBuffers(vbo);
        }
        for (int texture : textures) {
            GL30.glDeleteTextures(texture);
        }
    }
}
