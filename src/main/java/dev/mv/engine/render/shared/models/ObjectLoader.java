package dev.mv.engine.render.shared.models;

import java.io.IOException;

public interface ObjectLoader {

    Model loadModel(float[] vertices, float[] texCoords, float[] normals, int[] indices);

    Model loadExternalModel(String filepath) throws IOException;
    Model loadExternalModelAssimp(String path) throws IOException;
}
