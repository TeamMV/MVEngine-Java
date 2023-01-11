package dev.mv.engine.render.vulkan;

import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.VkClearValue;
import org.lwjgl.vulkan.VkExtent2D;
import org.lwjgl.vulkan.VkRenderPassBeginInfo;

import java.nio.LongBuffer;
import java.util.Arrays;

import static org.lwjgl.vulkan.VK13.*;

public class ForwardRenderActivity {
    private VulkanSwapChain swapChain;
    private VulkanSwapChainRenderPass renderPass;
    private VulkanFrameBuffer[] frameBuffers;
    private VulkanCommandBuffer[] commandBuffers;
    private VulkanFence[] fences;

    public ForwardRenderActivity(VulkanSwapChain swapChain, VulkanCommandPool commandPool) {
        this.swapChain = swapChain;
        try (MemoryStack stack = MemoryStack.stackPush()) {
            VulkanDevice device = swapChain.getDevice();
            VkExtent2D swapChainExtent = swapChain.getSwapChainExtent();
            System.err.println(swapChainExtent.width());
            VulkanSwapChain.ImageView[] imageViews = swapChain.getImageViews();
            int numImages = imageViews.length;

            renderPass = new VulkanSwapChainRenderPass(swapChain);

            LongBuffer pAttachments = stack.mallocLong(1);
            frameBuffers = new VulkanFrameBuffer[numImages];
            for (int i = 0; i < numImages; i++) {
                pAttachments.put(0, imageViews[i].getVkImageView());
                frameBuffers[i] = new VulkanFrameBuffer(device, swapChainExtent.width(), swapChainExtent.height(),
                    pAttachments, renderPass.getVkRenderPass());
            }

            commandBuffers = new VulkanCommandBuffer[numImages];
            fences = new VulkanFence[numImages];
            for (int i = 0; i < numImages; i++) {
                commandBuffers[i] = new VulkanCommandBuffer(commandPool, true, false);
                fences[i] = new VulkanFence(device, true);
                recordCommandBuffer(commandBuffers[i], frameBuffers[i], swapChainExtent.width(), swapChainExtent.height());
            }
        }
    }

    private void recordCommandBuffer(VulkanCommandBuffer commandBuffer, VulkanFrameBuffer frameBuffer, int width, int height) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            VkClearValue.Buffer clearValues = VkClearValue.calloc(1, stack);
            clearValues.apply(0, v -> v.color().float32(0, 0.5f).float32(1, 0.7f).float32(2, 0.9f).float32(3, 1));
            VkRenderPassBeginInfo renderPassBeginInfo = VkRenderPassBeginInfo.calloc(stack)
                .sType(VK_STRUCTURE_TYPE_RENDER_PASS_BEGIN_INFO)
                .renderPass(renderPass.getVkRenderPass())
                .pClearValues(clearValues)
                .renderArea(a -> a.extent().set(width, height))
                .framebuffer(frameBuffer.getVkFrameBuffer());

            commandBuffer.beginRecording();
            vkCmdBeginRenderPass(commandBuffer.getVkCommandBuffer(), renderPassBeginInfo, VK_SUBPASS_CONTENTS_INLINE);
            vkCmdEndRenderPass(commandBuffer.getVkCommandBuffer());
            commandBuffer.endRecording();
        }
    }

    public void submit(VulkanQueue queue) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            int idx = swapChain.getCurrentFrame();
            VulkanCommandBuffer commandBuffer = commandBuffers[idx];
            VulkanFence currentFence = fences[idx];
            currentFence.fenceWait();
            currentFence.reset();
            VulkanSwapChain.SyncSemaphores syncSemaphores = swapChain.getSyncSemaphoresList()[idx];
            queue.submit(stack.pointers(commandBuffer.getVkCommandBuffer()),
                stack.longs(syncSemaphores.imgAcquisitionSemaphore().getVkSemaphore()),
                stack.ints(VK_PIPELINE_STAGE_COLOR_ATTACHMENT_OUTPUT_BIT),
                stack.longs(syncSemaphores.renderCompleteSemaphore().getVkSemaphore()), currentFence);

        }
    }

    public void cleanup() {
        Arrays.stream(frameBuffers).forEach(VulkanFrameBuffer::cleanup);
        renderPass.cleanup();
        Arrays.stream(commandBuffers).forEach(VulkanCommandBuffer::cleanup);
        Arrays.stream(fences).forEach(VulkanFence::cleanup);
    }
}
