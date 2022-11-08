package dev.mv.engine.render.vulkan;

import lombok.Getter;
import org.lwjgl.vulkan.VkExtent2D;
import org.lwjgl.vulkan.VkSurfaceCapabilitiesKHR;
import org.lwjgl.vulkan.VkSurfaceFormatKHR;

import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

public class VulkanSwapChain {

    @Getter
    long id;
    @Getter
    List<Long> images = new ArrayList<>();
    @Getter
    List<Long> imageViews = new ArrayList<>();
    @Getter
    List<Long> framebuffers = new ArrayList<>();
    @Getter
    int imageFormat;
    @Getter
    VkExtent2D extent;
    VulkanSwapChain() {}

    static class SwapChainSupportDetails {
        VkSurfaceCapabilitiesKHR capabilities;
        VkSurfaceFormatKHR.Buffer formats;
        IntBuffer presentModes;
    }
}
