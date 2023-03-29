package dev.mv.engine.render.shared.batch;

import dev.mv.engine.render.shared.Window;
import dev.mv.engine.render.shared.shader.Shader;

@FunctionalInterface
public interface BatchSupplier<T extends Batch> {

    T create(int maxBatchSize, Window window, Shader shader);

}
