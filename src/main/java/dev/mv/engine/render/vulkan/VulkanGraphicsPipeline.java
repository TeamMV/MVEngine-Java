package dev.mv.engine.render.vulkan;

import dev.mv.engine.render.vulkan.shader.VulkanShader;
import lombok.Getter;

public class VulkanGraphicsPipeline {

    @Getter
    long renderPass, layout, id;
    @Getter
    VulkanShader shader;

}
