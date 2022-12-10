package dev.mv.engine.render.vulkan;

import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.*;

import java.nio.LongBuffer;

import static org.lwjgl.vulkan.KHRSwapchain.VK_IMAGE_LAYOUT_PRESENT_SRC_KHR;
import static org.lwjgl.vulkan.VK10.*;
import static org.lwjgl.vulkan.VK10.VK_PIPELINE_BIND_POINT_GRAPHICS;

public class VulkanRenderPass {
    private VulkanContext context;
    private long renderPass;

    public VulkanRenderPass(VulkanContext context) throws Exception {
        this.context = context;
        try(MemoryStack stack = MemoryStack.stackPush()) {
            VkAttachmentDescription colorAttachment = VkAttachmentDescription.calloc(stack);
            colorAttachment.format(context.swapChain.imageFormat);
            colorAttachment.samples(VK_SAMPLE_COUNT_1_BIT);
            colorAttachment.loadOp(VK_ATTACHMENT_LOAD_OP_CLEAR);
            colorAttachment.storeOp(VK_ATTACHMENT_STORE_OP_STORE);
            colorAttachment.stencilLoadOp(VK_ATTACHMENT_LOAD_OP_DONT_CARE);
            colorAttachment.stencilStoreOp(VK_ATTACHMENT_STORE_OP_DONT_CARE);
            colorAttachment.initialLayout(VK_IMAGE_LAYOUT_UNDEFINED);
            colorAttachment.finalLayout(VK_IMAGE_LAYOUT_PRESENT_SRC_KHR);

            VkAttachmentReference colorAttachmentRef = VkAttachmentReference.calloc(stack);
            colorAttachmentRef.attachment(0);
            colorAttachmentRef.layout(VK_IMAGE_LAYOUT_COLOR_ATTACHMENT_OPTIMAL);

            VkAttachmentReference.Buffer pColorAttachmentRef = VkAttachmentReference.calloc(1, stack);
            pColorAttachmentRef.put(0, colorAttachmentRef);
            VkSubpassDescription subpass = VkSubpassDescription.calloc(stack);
            subpass.pipelineBindPoint(VK_PIPELINE_BIND_POINT_GRAPHICS);
            subpass.colorAttachmentCount(1);
            subpass.pColorAttachments(pColorAttachmentRef);

            VkSubpassDependency dependency = VkSubpassDependency.calloc(stack);
            dependency.srcSubpass(VK_SUBPASS_EXTERNAL);
            dependency.dstSubpass(0);
            dependency.srcStageMask(VK_PIPELINE_STAGE_COLOR_ATTACHMENT_OUTPUT_BIT);
            dependency.srcAccessMask(0);
            dependency.dstStageMask(VK_PIPELINE_STAGE_COLOR_ATTACHMENT_OUTPUT_BIT);
            dependency.dstAccessMask(VK_ACCESS_COLOR_ATTACHMENT_WRITE_BIT);

            VkAttachmentDescription.Buffer pColorAttachment = VkAttachmentDescription.calloc(1, stack);
            pColorAttachment.put(0, colorAttachment);
            VkSubpassDescription.Buffer pSubpass = VkSubpassDescription.calloc(1, stack);
            pSubpass.put(0, subpass);
            VkSubpassDependency.Buffer pDependency = VkSubpassDependency.calloc(1, stack);
            pDependency.put(dependency);
            VkRenderPassCreateInfo renderPassInfo = VkRenderPassCreateInfo.calloc(stack);
            renderPassInfo.sType(VK_STRUCTURE_TYPE_RENDER_PASS_CREATE_INFO);
            renderPassInfo.pAttachments(pColorAttachment);
            renderPassInfo.pSubpasses(pSubpass);
            renderPassInfo.pDependencies(pDependency);

            LongBuffer pRenderPass = stack.callocLong(1);
            if (vkCreateRenderPass(context.logicalGPU, renderPassInfo, null, pRenderPass) != VK_SUCCESS) {
                throw new Exception();
            }
            renderPass = pRenderPass.get(0);
        }
    }

    public long getRenderPass() {
        return renderPass;
    }

    public void cleanup() {
        vkDestroyRenderPass(context.logicalGPU, renderPass, null);
    }
}
