package dev.mv.engine.render.vulkan;

import dev.mv.engine.render.utils.RenderUtils;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.*;

import java.io.IOException;

import static org.lwjgl.vulkan.VK10.*;
import static org.lwjgl.vulkan.VK10.vkDestroyShaderModule;

public class VulkanPipeline {
    VulkanPipeline(VulkanContext context, VulkanShader shader, Vulkan.RenderMode renderMode) throws IOException {
        try(MemoryStack stack = MemoryStack.stackPush()) {
            VkPipelineShaderStageCreateInfo vertShaderStageInfo = VkPipelineShaderStageCreateInfo.calloc(stack);
            vertShaderStageInfo.sType(VK_STRUCTURE_TYPE_PIPELINE_SHADER_STAGE_CREATE_INFO);
            vertShaderStageInfo.stage(VK_SHADER_STAGE_VERTEX_BIT);
            vertShaderStageInfo.module(shader.getVertexModule());
            vertShaderStageInfo.pName(RenderUtils.store("main"));

            VkPipelineShaderStageCreateInfo fragShaderStageInfo = VkPipelineShaderStageCreateInfo.calloc(stack);
            vertShaderStageInfo.sType(VK_STRUCTURE_TYPE_PIPELINE_SHADER_STAGE_CREATE_INFO);
            vertShaderStageInfo.stage(VK_SHADER_STAGE_FRAGMENT_BIT);
            vertShaderStageInfo.module(shader.getFragmentModule());
            vertShaderStageInfo.pName(RenderUtils.store("main"));

            VkPipelineShaderStageCreateInfo[] shaderStages = {vertShaderStageInfo, fragShaderStageInfo};

            VkPipelineVertexInputStateCreateInfo vertexInputInfo = VkPipelineVertexInputStateCreateInfo.calloc(stack);
            vertexInputInfo.sType(VK_STRUCTURE_TYPE_PIPELINE_VERTEX_INPUT_STATE_CREATE_INFO);
            vertexInputInfo.vertexBindingDescriptionCount();
            vertexInputInfo.pVertexBindingDescriptions(null);
            vertexInputInfo.vertexAttributeDescriptionCount();
            vertexInputInfo.pVertexAttributeDescriptions(null);

            VkPipelineInputAssemblyStateCreateInfo inputAssembly = VkPipelineInputAssemblyStateCreateInfo.calloc(stack);
            inputAssembly.sType(VK_STRUCTURE_TYPE_PIPELINE_INPUT_ASSEMBLY_STATE_CREATE_INFO);
            inputAssembly.topology(renderMode.asVulkanMode());
            inputAssembly.primitiveRestartEnable(false);

            VkViewport viewport = VkViewport.calloc(stack);
            viewport.x(0.0f);
            viewport.y(0.0f);
            viewport.width((float) context.swapChain.extent.width());
            viewport.height((float) context.swapChain.extent.height());
            viewport.minDepth(0.0f);
            viewport.maxDepth(1.0f);

            VkRect2D scissor = VkRect2D.calloc(stack);
            scissor.offset();
            scissor.extent(context.swapChain.extent);

            int[] dynamicStates = {
                VK_DYNAMIC_STATE_VIEWPORT,
                VK_DYNAMIC_STATE_SCISSOR
            };
            VkPipelineDynamicStateCreateInfo dynamicState = VkPipelineDynamicStateCreateInfo.calloc(stack);
            dynamicState.sType(VK_STRUCTURE_TYPE_PIPELINE_DYNAMIC_STATE_CREATE_INFO);
            dynamicState.dynamicStateCount();
            dynamicState.pDynamicStates(RenderUtils.store(dynamicStates));

            VkPipelineViewportStateCreateInfo viewportState = VkPipelineViewportStateCreateInfo.calloc(stack);
            viewportState.sType(VK_STRUCTURE_TYPE_PIPELINE_VIEWPORT_STATE_CREATE_INFO);
            viewportState.viewportCount(1);
            viewportState.scissorCount(1);

            VkPipelineRasterizationStateCreateInfo rasterizer = VkPipelineRasterizationStateCreateInfo.calloc(stack);
            rasterizer.sType(VK_STRUCTURE_TYPE_PIPELINE_RASTERIZATION_STATE_CREATE_INFO);
            rasterizer.depthClampEnable(false);
            rasterizer.rasterizerDiscardEnable(false);
            rasterizer.polygonMode(VK_POLYGON_MODE_FILL);
            rasterizer.lineWidth(1f);
            rasterizer.cullMode(VK_CULL_MODE_BACK_BIT);
            rasterizer.frontFace(VK_FRONT_FACE_CLOCKWISE);
            rasterizer.depthBiasEnable(false);
            rasterizer.depthBiasConstantFactor(0f);
            rasterizer.depthBiasClamp(0f);
            rasterizer.depthBiasSlopeFactor(0f);

            VkPipelineMultisampleStateCreateInfo multisampling = VkPipelineMultisampleStateCreateInfo.calloc(stack);
            multisampling.sType(VK_STRUCTURE_TYPE_PIPELINE_MULTISAMPLE_STATE_CREATE_INFO);
            multisampling.sampleShadingEnable(false);
            multisampling.rasterizationSamples(VK_SAMPLE_COUNT_1_BIT);
            multisampling.minSampleShading(1.0f);
            multisampling.pSampleMask(null);
            multisampling.alphaToCoverageEnable(false);
            multisampling.alphaToOneEnable(false);



            vkDestroyShaderModule(context.logicalGPU, shader.getVertexModule(), null);
            vkDestroyShaderModule(context.logicalGPU, shader.getFragmentModule(), null);
        }
    }
}
