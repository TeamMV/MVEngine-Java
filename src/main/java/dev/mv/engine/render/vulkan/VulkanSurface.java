package dev.mv.engine.render.vulkan;

import dev.mv.utils.logger.Logger;
import org.lwjgl.glfw.GLFWVulkan;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.KHRSurface;

import java.nio.LongBuffer;

public class VulkanSurface {
    private final VulkanPhysicalDevice physicalDevice;
    private final long vkSurface;

    public VulkanSurface(VulkanPhysicalDevice physicalDevice, long windowHandle) {
        Logger.debug("Creating Vulkan surface");
        this.physicalDevice = physicalDevice;
        try (MemoryStack stack = MemoryStack.stackPush()) {
            LongBuffer pSurface = stack.mallocLong(1);
            GLFWVulkan.glfwCreateWindowSurface(this.physicalDevice.getVkPhysicalDevice().getInstance(), windowHandle, null, pSurface);
            vkSurface = pSurface.get(0);
        }
    }

    public void cleanup() {
        Logger.debug("Destroying Vulkan surface");
        KHRSurface.vkDestroySurfaceKHR(physicalDevice.getVkPhysicalDevice().getInstance(), vkSurface, null);
    }

    public long getVkSurface() {
        return vkSurface;
    }
}
