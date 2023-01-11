package dev.mv.engine.render.vulkan;

import dev.mv.utils.logger.Logger;
import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.VkCommandBuffer;
import org.lwjgl.vulkan.VkCommandBufferAllocateInfo;
import org.lwjgl.vulkan.VkCommandBufferBeginInfo;
import org.lwjgl.vulkan.VkDevice;

import static dev.mv.engine.render.vulkan.VulkanUtils.vkCheck;
import static org.lwjgl.vulkan.VK13.*;

public class VulkanCommandBuffer {
    private final VulkanCommandPool commandPool;
    private final boolean oneTimeSubmit;
    private final VkCommandBuffer vkCommandBuffer;

    public VulkanCommandBuffer(VulkanCommandPool commandPool, boolean primary, boolean oneTimeSubmit) {
        Logger.debug("Creating command buffer");
        this.commandPool = commandPool;
        this.oneTimeSubmit = oneTimeSubmit;
        VkDevice vkDevice = commandPool.getDevice().getVkDevice();

        try (MemoryStack stack = MemoryStack.stackPush()) {
            VkCommandBufferAllocateInfo cmdBufAllocateInfo = VkCommandBufferAllocateInfo.calloc(stack)
                .sType(VK_STRUCTURE_TYPE_COMMAND_BUFFER_ALLOCATE_INFO)
                .commandPool(commandPool.getVkCommandPool())
                .level(primary ? VK_COMMAND_BUFFER_LEVEL_PRIMARY : VK_COMMAND_BUFFER_LEVEL_SECONDARY)
                .commandBufferCount(1);
            PointerBuffer pb = stack.mallocPointer(1);
            vkCheck(vkAllocateCommandBuffers(vkDevice, cmdBufAllocateInfo, pb),
                "Failed to allocate render command buffer");

            vkCommandBuffer = new VkCommandBuffer(pb.get(0), vkDevice);
        }
    }

    public void beginRecording() {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            VkCommandBufferBeginInfo cmdBufInfo = VkCommandBufferBeginInfo.calloc(stack)
                .sType(VK_STRUCTURE_TYPE_COMMAND_BUFFER_BEGIN_INFO);
            if (oneTimeSubmit) {
                cmdBufInfo.flags(VK_COMMAND_BUFFER_USAGE_ONE_TIME_SUBMIT_BIT);
            }
            vkCheck(vkBeginCommandBuffer(vkCommandBuffer, cmdBufInfo), "Failed to begin command buffer");
        }
    }

    public void cleanup() {
        Logger.debug("Destroying command buffer");
        vkFreeCommandBuffers(commandPool.getDevice().getVkDevice(), commandPool.getVkCommandPool(),
            vkCommandBuffer);
    }

    public void endRecording() {
        vkCheck(vkEndCommandBuffer(vkCommandBuffer), "Failed to end command buffer");
    }

    public VkCommandBuffer getVkCommandBuffer() {
        return vkCommandBuffer;
    }

    public void reset() {
        vkResetCommandBuffer(vkCommandBuffer, VK_COMMAND_BUFFER_RESET_RELEASE_RESOURCES_BIT);
    }
}
