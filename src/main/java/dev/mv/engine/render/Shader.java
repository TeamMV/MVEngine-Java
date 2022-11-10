package dev.mv.engine.render;

import org.joml.*;

public interface Shader {
    void make();
    void use();
    void uploadUniform1f(String name, float value);
    void uploadUniform1i(String name, int value);
    void uploadUniform1b(String name, boolean value);
    void uploadUniform1fv2(String name, Vector2f value);
    void uploadUniform1fv3(String name, Vector3f value);
    void uploadUniform1fv4(String name, Vector4f value);
    void uploadUniform2fv2(String name, Matrix2f value);
    void uploadUniform3fv3(String name, Matrix3f value);
    void uploadUniform4fv4(String name, Matrix4f value);
}
