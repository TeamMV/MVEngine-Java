package dev.mv.engine.render.vulkan.shader;

import dev.mv.engine.render.Shader;
import dev.mv.engine.render.utils.RenderUtils;
import org.joml.*;

import java.io.IOException;
import java.nio.ByteBuffer;

public class VulkanShader implements Shader {
    private SPIRV compiledVert, compiledFrag;

    public VulkanShader(String vertShaderPath, String fragShaderPath) throws IOException {
        compiledVert = SPIRV.compileShaderFile(vertShaderPath, SPIRV.ShaderKind.VERTEX_SHADER);
        compiledFrag = SPIRV.compileShaderFile(fragShaderPath, SPIRV.ShaderKind.FRAGMENT_SHADER);
    }

    @Override
    public void make() {

    }

    @Override
    public void use() {

    }

    @Override
    public void uploadUniform1f(String name, float value) {

    }

    @Override
    public void uploadUniform1i(String name, int value) {

    }

    @Override
    public void uploadUniform1b(String name, boolean value) {
        uploadUniform1i(name, value ? 1 : 0);
    }

    @Override
    public void uploadUniform1fv2(String name, Vector2f value) {

    }

    @Override
    public void uploadUniform1fv3(String name, Vector3f value) {

    }

    @Override
    public void uploadUniform1fv4(String name, Vector4f value) {

    }

    @Override
    public void uploadUniform2fv2(String name, Matrix2f value) {

    }

    @Override
    public void uploadUniform3fv3(String name, Matrix3f value) {

    }

    @Override
    public void uploadUniform4fv4(String name, Matrix4f value) {

    }
}
