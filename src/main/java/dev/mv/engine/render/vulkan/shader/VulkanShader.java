package dev.mv.engine.render.vulkan.shader;

import dev.mv.engine.render.Shader;
import dev.mv.engine.render.Window;
import dev.mv.engine.render.utils.RenderUtils;
import dev.mv.engine.render.vulkan.VulkanWindow;
import dev.mv.engine.render.vulkan.shader.layouts.VulkanShaderLayoutInformation;
import org.joml.*;
import org.lwjgl.vulkan.VkVertexInputAttributeDescription;
import org.lwjgl.vulkan.VkVertexInputBindingDescription;

import java.io.IOException;
import java.nio.ByteBuffer;

public class VulkanShader implements Shader {
    private SPIRV compiledVert, compiledFrag;
    private VulkanShaderLayoutInformation shaderLayout;
    private String vertShaderPath, fragShaderPath;
    private int pipelineId = -1;
    private VulkanWindow window;

    public VulkanShader(String vertShaderPath, String fragShaderPath) throws IOException {
        this.vertShaderPath = vertShaderPath;
        this.fragShaderPath = fragShaderPath;
    }

    public void compile() {
        compiledVert = SPIRV.compileShaderFile(vertShaderPath, SPIRV.ShaderKind.VERTEX_SHADER);
        compiledFrag = SPIRV.compileShaderFile(fragShaderPath, SPIRV.ShaderKind.FRAGMENT_SHADER);
        shaderLayout = VulkanShaderLayoutInformation.retrieveInformation(vertShaderPath);
    }

    public VkVertexInputAttributeDescription.Buffer getAttributeDescription() {
        return shaderLayout.getAttributeDescription();
    }

    public VkVertexInputBindingDescription.Buffer getBindingDescription() {
        return shaderLayout.getBindingDescription();
    }

    public ByteBuffer getVertexBytecode() {
        return compiledVert.bytecode();
    }

    public ByteBuffer getFragmentBytecode() {
        return compiledFrag.bytecode();
    }

    public void free() {
        shaderLayout = null;
        compiledVert.free();
        compiledFrag.free();
        compiledVert = null;
        compiledFrag = null;
    }

    @Override
    public void make(Window window) {
        if (!(window instanceof VulkanWindow)) {
            throw new RuntimeException("Cannot bind vulkan shader to non vulkan window.");
        }
        this.window = (VulkanWindow) window;
        pipelineId = this.window.addShader(this);
    }

    @Override
    public void use() {
        if (window == null) {
            throw new RuntimeException("Cannot use shader that isn't bound to window.");
        }
        window.setShader(this);
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

    public int getId() {
        return pipelineId;
    }
}
