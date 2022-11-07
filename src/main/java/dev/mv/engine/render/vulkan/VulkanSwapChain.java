package dev.mv.engine.render.vulkan;

import lombok.Getter;
import org.lwjgl.vulkan.VkExtent2D;
import org.lwjgl.vulkan.VkSurfaceCapabilitiesKHR;
import org.lwjgl.vulkan.VkSurfaceFormatKHR;

import java.nio.IntBuffer;
import java.util.List;

public class VulkanSwapChain {

    long swapChain;
    List<Long> swapChainImages;
    int swapChainImageFormat;
    VkExtent2D swapChainExtent;

    VulkanSwapChain() {}

    VulkanSwapChain(long swapChain, List<Long> swapChainImages, int swapChainImageFormat, VkExtent2D swapChainExtent) {
        this.swapChain = swapChain;
        this.swapChainImages = swapChainImages;
        this.swapChainImageFormat = swapChainImageFormat;
        this.swapChainExtent = swapChainExtent;
    }


    public void resize(int width, int height) {
        swapChainExtent = VkExtent2D.create().set(width, height);
    }

    static class SwapChainSupportDetails {

        VkSurfaceCapabilitiesKHR capabilities;
        VkSurfaceFormatKHR.Buffer formats;
        IntBuffer presentModes;

    }
}
