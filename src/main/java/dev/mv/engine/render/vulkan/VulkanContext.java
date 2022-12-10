package dev.mv.engine.render.vulkan;

import org.lwjgl.vulkan.*;

import java.util.ArrayList;
import java.util.List;

public class VulkanContext {
    VulkanWindow window;
    Vulkan vulkan;
    VkInstance instance;
    VkPhysicalDevice GPU;
    VkDevice logicalGPU;
    VkQueue graphicsQueue;
    VkQueue presentQueue;
    VulkanSwapChain swapChain = new VulkanSwapChain();
    VulkanProgram[] programs;
    VulkanProgram currentProgram;
    long[] swapChainFramebuffers;
    long commandPool;
    VkCommandBuffer commandBuffer;
    long imageAvailableSemaphore;
    long renderFinishedSemaphore;
    long inFlightFence;

    VulkanContext(VulkanWindow window) {
        this.window = window;
        vulkan = new Vulkan(this);
    }
}