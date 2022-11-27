package dev.mv.engine.render.shared.shader;

import dev.mv.engine.render.shared.Window;
import org.joml.*;

public interface Shader {
    void make(Window window);

    void bind();

    void use();

    void uniform(String name, float value);

    void uniform(String name, int value);

    void uniform(String name, boolean value);

    void uniform(String name, float[] value);

    void uniform(String name, int[] value);

    void uniform(String name, Vector2f value);

    void uniform(String name, Vector3f value);

    void uniform(String name, Vector4f value);

    void uniform(String name, Matrix2f value);

    void uniform(String name, Matrix3f value);

    void uniform(String name, Matrix4f value);
}
