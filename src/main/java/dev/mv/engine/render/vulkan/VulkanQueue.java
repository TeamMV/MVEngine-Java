package dev.mv.engine.render.vulkan;

import dev.mv.utils.logger.Logger;
import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.KHRSurface;
import org.lwjgl.vulkan.VkQueue;
import org.lwjgl.vulkan.VkQueueFamilyProperties;
import org.lwjgl.vulkan.VkSubmitInfo;

import java.nio.IntBuffer;
import java.nio.LongBuffer;

import static dev.mv.engine.render.vulkan.VulkanUtils.vkCheck;
import static org.lwjgl.vulkan.VK13.*;


public class VulkanQueue {
    private final VkQueue vkQueue;

    public VulkanQueue(VulkanDevice device, int queueFamilyIndex, int queueIndex) {
        Logger.debug("Creating queue");

        try (MemoryStack stack = MemoryStack.stackPush()) {
            PointerBuffer pQueue = stack.mallocPointer(1);
            vkGetDeviceQueue(device.getVkDevice(), queueFamilyIndex, queueIndex, pQueue);
            long queue = pQueue.get(0);
            vkQueue = new VkQueue(queue, device.getVkDevice());
        }
    }

    public VkQueue getVkQueue() {
        return vkQueue;
    }

    public void waitIdle() {
        vkQueueWaitIdle(vkQueue);
    }

    public void submit(PointerBuffer commandBuffers, LongBuffer waitSemaphores, IntBuffer dstStageMasks,
                       LongBuffer signalSemaphores, VulkanFence fence) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            VkSubmitInfo submitInfo = VkSubmitInfo.calloc(stack)
                .sType(VK_STRUCTURE_TYPE_SUBMIT_INFO)
                .pCommandBuffers(commandBuffers)
                .pSignalSemaphores(signalSemaphores);
            if (waitSemaphores != null) {
                submitInfo.waitSemaphoreCount(waitSemaphores.capacity())
                    .pWaitSemaphores(waitSemaphores)
                    .pWaitDstStageMask(dstStageMasks);
            } else {
                submitInfo.waitSemaphoreCount(0);
            }
            long fenceHandle = fence != null ? fence.getVkFence() : VK_NULL_HANDLE;

            vkCheck(vkQueueSubmit(vkQueue, submitInfo, fenceHandle),
                "Failed to submit command to queue");
        }
    }

    public static class GraphicsQueue extends VulkanQueue {
        private static int index = -1;

        public GraphicsQueue(VulkanDevice device, int queueIndex) {
            super(device, getGraphicsQueueFamilyIndex(device), queueIndex);
        }

        private static int getGraphicsQueueFamilyIndex(VulkanDevice device) {
            VulkanPhysicalDevice physicalDevice = device.getPhysicalDevice();
            VkQueueFamilyProperties.Buffer queuePropsBuff = physicalDevice.getVkQueueFamilyProps();
            int numQueuesFamilies = queuePropsBuff.capacity();
            for (int i = 0; i < numQueuesFamilies; i++) {
                VkQueueFamilyProperties props = queuePropsBuff.get(i);
                boolean graphicsQueue = (props.queueFlags() & VK_QUEUE_GRAPHICS_BIT) != 0;
                if (graphicsQueue) {
                    index = i;
                    break;
                }
            }

            if (index < 0) {
                throw new RuntimeException("Failed to get graphics Queue family index");
            }
            return index;
        }

        public static int getQueueFamilyIndex() {
            return index;
        }
    }

    public static class PresentQueue extends VulkanQueue {

        public PresentQueue(VulkanDevice device, VulkanSurface surface, int queueIndex) {
            super(device, getPresentQueueFamilyIndex(device, surface), queueIndex);
        }

        private static int getPresentQueueFamilyIndex(VulkanDevice device, VulkanSurface surface) {
            int index = -1;
            try (MemoryStack stack = MemoryStack.stackPush()) {
                VulkanPhysicalDevice physicalDevice = device.getPhysicalDevice();
                VkQueueFamilyProperties.Buffer queuePropsBuff = physicalDevice.getVkQueueFamilyProps();
                int numQueuesFamilies = queuePropsBuff.capacity();
                IntBuffer intBuff = stack.mallocInt(1);
                for (int i = 0; i < numQueuesFamilies; i++) {
                    KHRSurface.vkGetPhysicalDeviceSurfaceSupportKHR(physicalDevice.getVkPhysicalDevice(),
                        i, surface.getVkSurface(), intBuff);
                    boolean supportsPresentation = intBuff.get(0) == VK_TRUE;
                    if (supportsPresentation) {
                        index = i;
                        break;
                    }
                }
            }

            if (index < 0) {
                throw new RuntimeException("Failed to get Presentation Queue family index");
            }
            return index;
        }
    }
}
