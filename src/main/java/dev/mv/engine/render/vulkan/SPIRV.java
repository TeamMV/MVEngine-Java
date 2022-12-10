package dev.mv.engine.render.vulkan;

import dev.mv.engine.render.utils.RenderUtils;
import dev.mv.engine.render.vulkan.VulkanContext;
import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.vulkan.VkShaderModuleCreateInfo;

import java.io.*;
import java.nio.ByteBuffer;

import static org.lwjgl.util.shaderc.Shaderc.*;
import static org.lwjgl.vulkan.VK10.*;

public class SPIRV {
    private ByteBuffer bytecode;
    private long module;
    private VulkanContext context;

    public SPIRV(String shaderFile, ShaderType type, VulkanContext context) throws IOException {
        this.context = context;
        String code = new String(getClass().getResourceAsStream(shaderFile).readAllBytes());

        bytecode = compileShader(code, type, shaderFile.substring(shaderFile.lastIndexOf(File.separator) + 1));
        module = createShaderModule(bytecode);
    }

    private ByteBuffer compileShader(String source, ShaderType type, String filename) {
        long compiler = shaderc_compiler_initialize();
        if (compiler == MemoryUtil.NULL) throw new RuntimeException("Failed to compile shader " + filename + "into SPIR-V");
        long result = shaderc_compile_into_spv(compiler, source, type.getTypeAsShadercInt(), filename, "main", 0L);
        if (result == MemoryUtil.NULL) throw new RuntimeException("Failed to compile shader " + filename + "into SPIR-V");
        if(shaderc_result_get_compilation_status(result) != shaderc_compilation_status_success) {
            throw new RuntimeException("Failed to compile shader " + filename + "into SPIR-V:\n " + shaderc_result_get_error_message(result));
        }
        shaderc_compiler_release(compiler);
        return shaderc_result_get_bytes(result);
    }

    private long createShaderModule(ByteBuffer bytecode) {
        try(MemoryStack stack = MemoryStack.stackPush()) {
            VkShaderModuleCreateInfo createInfo = VkShaderModuleCreateInfo.calloc(stack);
            createInfo.sType(VK_STRUCTURE_TYPE_SHADER_MODULE_CREATE_INFO);
            createInfo.pCode(bytecode);

            long[] sModule = new long[1];
            if (vkCreateShaderModule(context.logicalGPU, createInfo, null, sModule) != VK_SUCCESS) {
                throw new RuntimeException("Failed to create Shader module");
            }
            return sModule[0];
        }
    }

    public enum ShaderType{
        VERTEX(1),
        FRAGMENT(2),
        GEOMETRY(3),
        TESSELATION(4);

        private int i;

        ShaderType(int i) {
            this.i = i;
        }

        int getTypeAsShadercInt() {
            return switch(i) {
                case 1: yield shaderc_vertex_shader;
                case 2: yield shaderc_fragment_shader;
                case 3: yield shaderc_geometry_shader;
                case 4: yield shaderc_tess_evaluation_shader;
                default: yield -1;
            };
        }
    }

    public long getShaderModule() {
        return module;
    }
}
