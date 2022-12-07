package dev.mv.engine.render.vulkan;

import dev.mv.engine.render.utils.RenderUtils;
import dev.mv.engine.render.vulkan.VulkanContext;
import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.VkShaderModuleCreateInfo;

import java.io.*;
import java.nio.ByteBuffer;

import static org.lwjgl.util.shaderc.Shaderc.*;
import static org.lwjgl.vulkan.VK10.*;

public class SPIRV {
    private String bytecode;
    private long module;
    private VulkanContext context;

    public SPIRV(String shaderfile, ShaderType type, VulkanContext context) throws IOException {
        this.context = context;
        BufferedReader reader = new BufferedReader(new FileReader(shaderfile));
        String line = "";
        String code = "";
        while((line = reader.readLine()) != null) {
            code += line;
        }
        reader.close();

        bytecode = compileShader(code, type, shaderfile.substring(shaderfile.lastIndexOf(File.separator)));
        module = createShaderModule(bytecode);
    }

    private String compileShader(String source, ShaderType type, String filename) {
        long compiler = shaderc_compile_options_initialize();
        long result = shaderc_compile_into_spv(compiler, source, type.getTypeAsShadercInt(), filename, source.substring(0, 5), -1L);
        ByteBuffer buffer = shaderc_result_get_bytes(result);
        return new String(buffer.array());
    }

    private long createShaderModule(String bytecode) {
        try(MemoryStack stack = MemoryStack.stackPush()) {
            VkShaderModuleCreateInfo createInfo = VkShaderModuleCreateInfo.calloc(stack);
            createInfo.sType(VK_STRUCTURE_TYPE_SHADER_MODULE_CREATE_INFO);
            createInfo.pCode(RenderUtils.store(bytecode));

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
