package dev.mv.engine.render.vulkan;

import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.VkFenceCreateInfo;

import java.nio.LongBuffer;

import static dev.mv.engine.render.vulkan.VulkanUtils.vkCheck;
import static org.lwjgl.vulkan.VK13.*;

public class VulkanFence {
    private final VulkanDevice device;
    private final long vkFence;

    public VulkanFence(VulkanDevice device, boolean signaled) {
        this.device = device;
        try (MemoryStack stack = MemoryStack.stackPush()) {
            VkFenceCreateInfo fenceCreateInfo = VkFenceCreateInfo.calloc(stack)
                .sType(VK_STRUCTURE_TYPE_FENCE_CREATE_INFO)
                .flags(signaled ? VK_FENCE_CREATE_SIGNALED_BIT : 0);

            LongBuffer lp = stack.mallocLong(1);
            vkCheck(vkCreateFence(device.getVkDevice(), fenceCreateInfo, null, lp),
                "Failed to create semaphore");
            vkFence = lp.get(0);
        }
    }

    public void cleanup() {
        vkDestroyFence(device.getVkDevice(), vkFence, null);
    }

    public void fenceWait() {
        vkWaitForFences(device.getVkDevice(), vkFence, true, Long.MAX_VALUE);
    }

    public long getVkFence() {
        return vkFence;
    }

    public void reset() {
        vkResetFences(device.getVkDevice(), vkFence);
    }
}
