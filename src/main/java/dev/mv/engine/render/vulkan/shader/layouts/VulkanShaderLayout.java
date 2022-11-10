package dev.mv.engine.render.vulkan.shader.layouts;

import org.lwjgl.vulkan.VkVertexInputAttributeDescription;

import static org.lwjgl.vulkan.VK10.*;

public class VulkanShaderLayout {
    private ShaderDataType dataType;
    private int location, binding, offset;

    VulkanShaderLayout(int location, int binding, int offset, ShaderDataType dataType) {
        this.location = location;
        this.binding = binding;
        this.offset = offset;
        this.dataType = dataType;
    }

    public VkVertexInputAttributeDescription getDescription() {
        VkVertexInputAttributeDescription description = VkVertexInputAttributeDescription.calloc();
        description.binding(binding);
        description.location(location);
        description.offset(offset);
        description.format(dataType.getVkFormat());
        return description;
    }

    public enum ShaderDataType {
        FLOAT("float", VK_FORMAT_R32_SFLOAT, Float.BYTES),
        DOUBLE("double", VK_FORMAT_R64_SFLOAT, Double.BYTES),
        INT("int", VK_FORMAT_R32_SINT, Integer.BYTES),
        UINT("uint", VK_FORMAT_R32_UINT, Integer.BYTES),
        BOOL("bool", VK_FORMAT_R8_SINT, Byte.BYTES),

        VEC2("vec2", VK_FORMAT_R32G32_SFLOAT, 2 * Float.BYTES),
        VEC3("vec3", VK_FORMAT_R32G32B32_SFLOAT, 3 * Float.BYTES),
        VEC4("vec4", VK_FORMAT_R32G32B32A32_SFLOAT, 4 * Float.BYTES),
        DVEC2("dvec2", VK_FORMAT_R64G64_SFLOAT, 2 * Double.BYTES),
        DVEC3("dvec3", VK_FORMAT_R64G64B64_SFLOAT, 3 * Double.BYTES),
        DVEC4("dvec4", VK_FORMAT_R64G64B64A64_SFLOAT, 4 * Double.BYTES),
        BVEC2("bvec2", VK_FORMAT_R8G8_SINT, 2 * Byte.BYTES),
        BVEC3("bvec3", VK_FORMAT_R8G8B8_SINT, 3 * Byte.BYTES),
        BVEC4("bvec4", VK_FORMAT_R8G8B8A8_SINT, 4 * Byte.BYTES),
        IVEC2("ivec2", VK_FORMAT_R32G32_SINT, 2 * Integer.BYTES),
        IVEC3("ivec3", VK_FORMAT_R32G32B32_SINT, 3 * Integer.BYTES),
        IVEC4("ivec4", VK_FORMAT_R32G32B32A32_SINT, 4 * Integer.BYTES),
        UVEC2("uvec2", VK_FORMAT_R32G32_UINT, 2 * Integer.BYTES),
        UVEC3("uvec3", VK_FORMAT_R32G32B32_UINT, 3 * Integer.BYTES),
        UVEC4("uvec4", VK_FORMAT_R32G32B32A32_UINT, 4 * Integer.BYTES),

        UNDEF("undef", VK_FORMAT_UNDEFINED, Float.BYTES);

        //MAT2("mat2", VK_FORMAT_R32G32_SFLOAT),
        //MAT3("mat3", VK_FORMAT_R32G32B32_SFLOAT),
        //MAT4("mat4", VK_FORMAT_R32G32B32A32_SFLOAT),
        //DMAT2("dmat2",VK_FORMAT_R64G64_SFLOAT),
        //DMAT3("dmat3", VK_FORMAT_R64G64B64_SFLOAT),
        //DMAT4("dmat4", VK_FORMAT_R64G64B64A64_SFLOAT);

        private String type;
        private int vkFormat;
        private int size;
        ShaderDataType(String type, int vkFormat, int size) {
            this.type = type;
            this.vkFormat = vkFormat;
            this.size = size;
        }

        public String getTypeString() {
            return type;
        }

        public int getVkFormat() {
            return vkFormat;
        }

        public static ShaderDataType get(String type) {
            for (ShaderDataType shaderDataType : values()) {
                if (shaderDataType.type.equals(type)) {
                    return shaderDataType;
                }
            }
            return null;
        }
    }

    public int getSize() {
        return dataType.size;
    }
}
