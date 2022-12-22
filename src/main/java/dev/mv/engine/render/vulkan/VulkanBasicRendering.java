package dev.mv.engine.render.vulkan;

import dev.mv.engine.render.utils.RenderUtils;
import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.VkCommandBuffer;
import org.lwjgl.vulkan.VkPresentInfoKHR;
import org.lwjgl.vulkan.VkSubmitInfo;

import java.nio.IntBuffer;

import static org.lwjgl.vulkan.KHRSwapchain.*;
import static org.lwjgl.vulkan.VK10.*;

public class VulkanBasicRendering {
    private VulkanContext context;
    private long[] inFlightFences;
    private MemoryStack stack = MemoryStack.stackPush();

    public VulkanBasicRendering(VulkanContext context) {
        this.context = context;
        inFlightFences = new long[] {context.inFlightFence};
    }

    public void drawFrame() {
        vkWaitForFences(context.logicalGPU, inFlightFences, true, Long.MAX_VALUE);
        vkResetFences(context.logicalGPU, inFlightFences);

        IntBuffer pImageIndex = stack.mallocInt(1);
        vkAcquireNextImageKHR(context.logicalGPU, context.swapChain.id, Long.MAX_VALUE, context.imageAvailableSemaphore, context.inFlightFence, pImageIndex);

        vkResetCommandBuffer(context.commandBuffer, 0);
        try {
            context.vulkan.execute_bindGraphicsPipeline(VulkanProgram.findPipeline(context.currentProgram.getVulkanPipeline()));
            context.vulkan.beginCommandBufferRecording(context.commandBuffer, pImageIndex.get(0));
        } catch (Vulkan.BufferRecordException | Vulkan.NoBufferRecordingException e) {
            throw new RuntimeException(e);
        }

        long[] waitSemaphores = {context.imageAvailableSemaphore};
        int[] waitStages = {VK_PIPELINE_STAGE_COLOR_ATTACHMENT_OUTPUT_BIT};
        long[] signalSemaphores = {context.renderFinishedSemaphore};
        PointerBuffer pCommandBuffers = stack.callocPointer(1);
        pCommandBuffers.put(stack.pointers(context.vulkan.getCurrentCommandBuffer()));

        VkSubmitInfo submitInfo = VkSubmitInfo.calloc(stack);
        submitInfo.sType(VK_STRUCTURE_TYPE_SUBMIT_INFO);
        submitInfo.waitSemaphoreCount(1);
        submitInfo.pWaitSemaphores(stack.longs(waitSemaphores));
        submitInfo.pWaitDstStageMask(stack.ints(waitStages));
        submitInfo.pCommandBuffers(pCommandBuffers);
        submitInfo.pSignalSemaphores(stack.longs(signalSemaphores));

        if (vkQueueSubmit(context.graphicsQueue, submitInfo, context.inFlightFence) != VK_SUCCESS) {
            throw new RuntimeException("failed to submit draw command buffer!");
        }

        long[] swapChains = {context.swapChain.id};
        VkPresentInfoKHR presentInfo = VkPresentInfoKHR.calloc(stack);
        presentInfo.sType(VK_STRUCTURE_TYPE_PRESENT_INFO_KHR);
        presentInfo.pWaitSemaphores(RenderUtils.store(signalSemaphores));
        presentInfo.swapchainCount(1);
        presentInfo.pSwapchains(stack.longs(swapChains));
        presentInfo.pImageIndices(pImageIndex);
        presentInfo.pResults(null);

        vkQueuePresentKHR(context.presentQueue, presentInfo);
    }
}
