package dev.mv.engine.render.vulkan;

import dev.mv.engine.render.shared.Window;
import dev.mv.engine.render.shared.shader.Shader;
import org.joml.*;

import java.io.IOException;

public class VulkanShader implements Shader {
    private int id;
    private String vertexCode, fragmentCode;
    private long vertexModule, fragmentModule;
    private VulkanContext context;
    private VulkanShaderCreateInfo createInfo;

    public VulkanShader(VulkanShaderCreateInfo shaderCreateInfo, VulkanContext context) throws IOException {
        this.context = context;
        this.createInfo = shaderCreateInfo;
        vertexModule = new SPIRV(createInfo.vertexPath, SPIRV.ShaderType.VERTEX, context).getShaderModule();
        fragmentModule = new SPIRV(createInfo.fragmentPath, SPIRV.ShaderType.FRAGMENT, context).getShaderModule();
    }

    @Override
    public void make(Window window) {
        id = VulkanProgram.genShader(this);
    }

    @Override
    public void bind() {

    }

    @Override
    public void use() {

    }

    @Override
    public void uniform(String name, float value) {

    }

    @Override
    public void uniform(String name, int value) {

    }

    @Override
    public void uniform(String name, boolean value) {

    }

    @Override
    public void uniform(String name, float[] value) {

    }

    @Override
    public void uniform(String name, int[] value) {

    }

    @Override
    public void uniform(String name, Vector2f value) {

    }

    @Override
    public void uniform(String name, Vector3f value) {

    }

    @Override
    public void uniform(String name, Vector4f value) {

    }

    @Override
    public void uniform(String name, Matrix2f value) {

    }

    @Override
    public void uniform(String name, Matrix3f value) {

    }

    @Override
    public void uniform(String name, Matrix4f value) {

    }

    public int getId() {
        return id;
    }

    public long getVertexModule() {
        return vertexModule;
    }

    public long getFragmentModule() {
        return fragmentModule;
    }
}
