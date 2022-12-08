package dev.mv.engine.render.vulkan;

import org.lwjgl.vulkan.VkDevice;
import org.lwjgl.vulkan.VkInstance;
import org.lwjgl.vulkan.VkPhysicalDevice;

public class VulkanContext {
    VulkanWindow window;
    Vulkan vulkan;
    VkInstance instance;
    VkPhysicalDevice GPU;
    VkDevice logicalGPU;
    VulkanSwapChain swapChain;
    VulkanProgram[] programs;
    long surface;

    VulkanContext(VulkanWindow window) {
        this.window = window;
        vulkan = new Vulkan(this);
    }
}