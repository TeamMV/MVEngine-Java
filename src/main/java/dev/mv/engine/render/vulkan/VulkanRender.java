package dev.mv.engine.render.vulkan;

import java.net.UnknownServiceException;

public class VulkanRender {
    private final VulkanInstance instance;
    private final VulkanDevice device;
    private final VulkanQueue.GraphicsQueue graphQueue;
    private final VulkanPhysicalDevice physicalDevice;
    private final VulkanSurface surface;
    private final VulkanCommandPool commandPool;
    private final VulkanQueue.PresentQueue presentQueue;
    private final ForwardRenderActivity fwdRenderActivity;
    private VulkanSwapChain swapChain;

    public VulkanRender(VulkanWindow window, VulkanRenderInfo info) throws UnknownServiceException {
        instance = new VulkanInstance(info.shouldValidate);
        physicalDevice = VulkanPhysicalDevice.createPhysicalDevice(instance);
        device = new VulkanDevice(physicalDevice);
        surface = new VulkanSurface(physicalDevice, window.getGlfwId());
        graphQueue = new VulkanQueue.GraphicsQueue(device, 0);
        swapChain = new VulkanSwapChain(device, surface, window, info.requestedImages, info.vsync);
        presentQueue = new VulkanQueue.PresentQueue(device, surface, 0);
        commandPool = new VulkanCommandPool(device, graphQueue.getQueueFamilyIndex());
        fwdRenderActivity = new ForwardRenderActivity(swapChain, commandPool);
        swapChain.acquireNextImage();
        fwdRenderActivity.submit(presentQueue);
        swapChain.presentImage(graphQueue);
    }

    public void cleanup() {
        presentQueue.waitIdle();
        surface.cleanup();
        device.cleanup();
        physicalDevice.cleanup();
        swapChain.cleanup();
        instance.cleanup();
        fwdRenderActivity.cleanup();
        commandPool.cleanup();
    }

    public static class VulkanRenderInfo {
        public String physicalDeviceName;
        public boolean shouldValidate;
        public boolean vsync;
        public int requestedImages;
    }
}
