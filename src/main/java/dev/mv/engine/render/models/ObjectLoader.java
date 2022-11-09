package dev.mv.engine.render.models;

import java.io.IOException;

public interface ObjectLoader {

    Model loadModel(float[] vertices, float[] texCoords, float[] normals, int[] indices);

    Model loadExternalModel(String filepath) throws IOException;
}
