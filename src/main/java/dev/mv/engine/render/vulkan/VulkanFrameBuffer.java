package dev.mv.engine.render.vulkan;

import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.VkFramebufferCreateInfo;

import java.nio.LongBuffer;

import static dev.mv.engine.render.vulkan.VulkanUtils.vkCheck;
import static org.lwjgl.vulkan.VK13.*;

import java.nio.LongBuffer;

public class VulkanFrameBuffer {
    private final VulkanDevice device;
    private final long vkFrameBuffer;

    public VulkanFrameBuffer(VulkanDevice device, int width, int height, LongBuffer pAttachments, long renderPass) {
        this.device = device;
        try (MemoryStack stack = MemoryStack.stackPush()) {
            VkFramebufferCreateInfo fci = VkFramebufferCreateInfo.calloc(stack)
                .sType(VK_STRUCTURE_TYPE_FRAMEBUFFER_CREATE_INFO)
                .pAttachments(pAttachments)
                .width(width)
                .height(height)
                .layers(1)
                .renderPass(renderPass);

            LongBuffer lp = stack.mallocLong(1);
            vkCheck(vkCreateFramebuffer(device.getVkDevice(), fci, null, lp),
                "Failed to create FrameBuffer");
            vkFrameBuffer = lp.get(0);
        }
    }

    public void cleanup() {
        vkDestroyFramebuffer(device.getVkDevice(), vkFrameBuffer, null);
    }

    public long getVkFrameBuffer() {
        return vkFrameBuffer;
    }
}
