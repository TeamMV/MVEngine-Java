package dev.mv.engine.render.vulkan;

import org.lwjgl.vulkan.VkExtent2D;
import org.lwjgl.vulkan.VkSurfaceCapabilitiesKHR;
import org.lwjgl.vulkan.VkSurfaceFormatKHR;

import java.nio.IntBuffer;
import java.util.List;

public class VulkanSwapChain {
    long id;
    List<Long> images;
    int imageFormat;
    VkExtent2D extent;
    List<Long> imageViews;
    List<Long> framebuffers;
    SwapChainSupportDetails supportDetails;

    public static class SwapChainSupportDetails {
        VkSurfaceCapabilitiesKHR capabilities;
        VkSurfaceFormatKHR.Buffer formats;
        IntBuffer presentModes;

        public VkSurfaceCapabilitiesKHR getCapabilities() {
            return capabilities;
        }

    }
}
