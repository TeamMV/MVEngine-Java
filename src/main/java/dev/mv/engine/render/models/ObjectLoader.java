package dev.mv.engine.render.models;

import java.io.IOException;

public interface ObjectLoader {
    Model loadModel(float[] vertices);
    Model loadExternalModel(String filepath) throws IOException;
}
