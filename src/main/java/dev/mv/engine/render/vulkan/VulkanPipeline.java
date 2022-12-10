package dev.mv.engine.render.vulkan;

import dev.mv.engine.render.utils.RenderUtils;
import org.lwjgl.system.Configuration;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.*;

import java.nio.LongBuffer;

import static org.lwjgl.vulkan.VK10.*;

public class VulkanPipeline {
    private VulkanContext context;
    private long pipelineLayout;
    private long graphicsPipeline;

    VulkanPipeline(VulkanContext context, VulkanShader shader, Vulkan.RenderMode renderMode, VulkanRenderPass renderPass) throws Exception {
        this.context = context;
        try(MemoryStack stack = MemoryStack.stackPush()) {
            VkPipelineShaderStageCreateInfo vertShaderStageInfo = VkPipelineShaderStageCreateInfo.calloc(stack);
            vertShaderStageInfo.sType(VK_STRUCTURE_TYPE_PIPELINE_SHADER_STAGE_CREATE_INFO);
            vertShaderStageInfo.stage(VK_SHADER_STAGE_VERTEX_BIT);
            vertShaderStageInfo.module(shader.getVertexModule());
            vertShaderStageInfo.pName(RenderUtils.store("main"));

            VkPipelineShaderStageCreateInfo fragShaderStageInfo = VkPipelineShaderStageCreateInfo.calloc(stack);
            fragShaderStageInfo.sType(VK_STRUCTURE_TYPE_PIPELINE_SHADER_STAGE_CREATE_INFO);
            fragShaderStageInfo.stage(VK_SHADER_STAGE_FRAGMENT_BIT);
            fragShaderStageInfo.module(shader.getFragmentModule());
            fragShaderStageInfo.pName(RenderUtils.store("main"));

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

            VkPipelineColorBlendAttachmentState colorBlendAttachment = VkPipelineColorBlendAttachmentState.calloc(stack);
            colorBlendAttachment.colorWriteMask(VK_COLOR_COMPONENT_R_BIT | VK_COLOR_COMPONENT_G_BIT | VK_COLOR_COMPONENT_B_BIT | VK_COLOR_COMPONENT_A_BIT);
            colorBlendAttachment.blendEnable(true);
            colorBlendAttachment.srcColorBlendFactor(VK_BLEND_FACTOR_SRC_ALPHA);
            colorBlendAttachment.dstColorBlendFactor(VK_BLEND_FACTOR_ONE_MINUS_SRC_ALPHA);
            colorBlendAttachment.colorBlendOp(VK_BLEND_OP_ADD);
            colorBlendAttachment.srcAlphaBlendFactor(VK_BLEND_FACTOR_ONE);
            colorBlendAttachment.dstAlphaBlendFactor(VK_BLEND_FACTOR_ZERO);
            colorBlendAttachment.alphaBlendOp(VK_BLEND_OP_ADD);

            VkPipelineColorBlendAttachmentState.Buffer pColorBlendAttachment = VkPipelineColorBlendAttachmentState.calloc(1, stack);
            pColorBlendAttachment.put(0, colorBlendAttachment);
            VkPipelineColorBlendStateCreateInfo colorBlending = VkPipelineColorBlendStateCreateInfo.calloc(stack);
            colorBlending.sType(VK_STRUCTURE_TYPE_PIPELINE_COLOR_BLEND_STATE_CREATE_INFO);
            colorBlending.logicOpEnable(false);
            colorBlending.logicOp(VK_LOGIC_OP_COPY);
            colorBlending.pAttachments(pColorBlendAttachment);
            colorBlending.blendConstants(0, 0.0f);
            colorBlending.blendConstants(1, 0.0f);
            colorBlending.blendConstants(2, 0.0f);
            colorBlending.blendConstants(3, 0.0f);

            VkPipelineLayoutCreateInfo pipelineLayoutInfo = VkPipelineLayoutCreateInfo.calloc(stack);
            pipelineLayoutInfo.sType(VK_STRUCTURE_TYPE_PIPELINE_LAYOUT_CREATE_INFO);
            pipelineLayoutInfo.pSetLayouts(null);
            pipelineLayoutInfo.pPushConstantRanges(null);

            LongBuffer pPipelineLayout = stack.callocLong(1);
            if (vkCreatePipelineLayout(context.logicalGPU, pipelineLayoutInfo, null, pPipelineLayout) != VK_SUCCESS) {
                throw new Exception();
            }
            pipelineLayout = pPipelineLayout.get(0);

            vkDestroyShaderModule(context.logicalGPU, shader.getVertexModule(), null);
            vkDestroyShaderModule(context.logicalGPU, shader.getFragmentModule(), null);

            VkPipelineShaderStageCreateInfo.Buffer pShaderStages = VkPipelineShaderStageCreateInfo.calloc(shaderStages.length, stack);
            int i = 0;
            for(VkPipelineShaderStageCreateInfo shaderStageCreateInfo : shaderStages) {
                pShaderStages.put(i++, shaderStageCreateInfo);
            }
            VkGraphicsPipelineCreateInfo pipelineInfo = VkGraphicsPipelineCreateInfo.calloc(stack);
            pipelineInfo.sType(VK_STRUCTURE_TYPE_GRAPHICS_PIPELINE_CREATE_INFO);
            pipelineInfo.pStages(pShaderStages);
            pipelineInfo.pVertexInputState(vertexInputInfo);
            pipelineInfo.pInputAssemblyState(inputAssembly);
            pipelineInfo.pViewportState(viewportState);
            pipelineInfo.pRasterizationState(rasterizer);
            pipelineInfo.pMultisampleState(multisampling);
            pipelineInfo.pDepthStencilState(null);
            pipelineInfo.pColorBlendState(colorBlending);
            pipelineInfo.pDynamicState(dynamicState);
            pipelineInfo.layout(pipelineLayout);
            pipelineInfo.renderPass(renderPass.getRenderPass());
            pipelineInfo.subpass(0);
            pipelineInfo.basePipelineHandle(VK_NULL_HANDLE);
            pipelineInfo.basePipelineIndex(-1);

            VkGraphicsPipelineCreateInfo.Buffer pPipelineInfo = VkGraphicsPipelineCreateInfo.calloc(1, stack);
            pPipelineInfo.put(0, pipelineInfo);
            long[] graphicsPipelineArray = new long[1];
            if (vkCreateGraphicsPipelines(context.logicalGPU, VK_NULL_HANDLE, pPipelineInfo, null, graphicsPipelineArray) != VK_SUCCESS) {
                throw new Exception();
            }

            graphicsPipeline = graphicsPipelineArray[0];
        }
    }

    public long getPipelineLayout() {
        return pipelineLayout;
    }

    public long getGraphicsPipeline() {
        return graphicsPipeline;
    }

    public void cleanup() {
        vkDestroyPipeline(context.logicalGPU, graphicsPipeline, null);
        vkDestroyPipelineLayout(context.logicalGPU, pipelineLayout, null);
    }
}
