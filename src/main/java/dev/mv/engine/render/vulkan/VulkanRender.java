package dev.mv.engine.render.vulkan;

import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.VkPresentInfoKHR;
import org.lwjgl.vulkan.VkSubmitInfo;

import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.vulkan.KHRSwapchain.*;
import static org.lwjgl.vulkan.VK10.*;

public class VulkanRender {
    public static final int MAX_FRAMES_IN_FLIGHT = 2;

    List<Frame> inFlightFrames = new ArrayList<>(MAX_FRAMES_IN_FLIGHT);;
    Map<Integer, Frame> imagesInFlight = new HashMap<>();
    int currentFrame;

    VulkanRender() {}

    void drawFrame(VulkanSwapChain swapChain, VulkanCommandPool commandPool) {
        try(MemoryStack stack = stackPush()) {

            Frame thisFrame = inFlightFrames.get(currentFrame);

            vkWaitForFences(Vulkan.getLogicalDevice(), thisFrame.pFence(), true, 0xFFFFFFFFFFFFFFFFL);

            IntBuffer pImageIndex = stack.mallocInt(1);

            vkAcquireNextImageKHR(Vulkan.getLogicalDevice(), swapChain.id, 0xFFFFFFFFFFFFFFFFL, thisFrame.imageAvailableSemaphore(), VK_NULL_HANDLE, pImageIndex);
            final int imageIndex = pImageIndex.get(0);

            if(imagesInFlight.containsKey(imageIndex)) {
                vkWaitForFences(Vulkan.getLogicalDevice(), imagesInFlight.get(imageIndex).fence(), true, 0xFFFFFFFFFFFFFFFFL);
            }

            imagesInFlight.put(imageIndex, thisFrame);

            VkSubmitInfo submitInfo = VkSubmitInfo.calloc(stack);
            submitInfo.sType(VK_STRUCTURE_TYPE_SUBMIT_INFO);

            submitInfo.waitSemaphoreCount(1);
            submitInfo.pWaitSemaphores(thisFrame.pImageAvailableSemaphore());
            submitInfo.pWaitDstStageMask(stack.ints(VK_PIPELINE_STAGE_COLOR_ATTACHMENT_OUTPUT_BIT));

            submitInfo.pSignalSemaphores(thisFrame.pRenderFinishedSemaphore());

            submitInfo.pCommandBuffers(stack.pointers(commandPool.buffers.get(imageIndex)));

            vkResetFences(Vulkan.getLogicalDevice(), thisFrame.pFence());

            if (vkQueueSubmit(Vulkan.getGraphicsQueue(), submitInfo, thisFrame.fence()) != VK_SUCCESS) {
                throw new RuntimeException("Failed to submit draw command buffer");
            }

            VkPresentInfoKHR presentInfo = VkPresentInfoKHR.calloc(stack);
            presentInfo.sType(VK_STRUCTURE_TYPE_PRESENT_INFO_KHR);

            presentInfo.pWaitSemaphores(thisFrame.pRenderFinishedSemaphore());

            presentInfo.swapchainCount(1);
            presentInfo.pSwapchains(stack.longs(swapChain.id));

            presentInfo.pImageIndices(pImageIndex);

            vkQueuePresentKHR(Vulkan.getPresentQueue(), presentInfo);

            currentFrame = (currentFrame + 1) % MAX_FRAMES_IN_FLIGHT;
        }
    }
}
