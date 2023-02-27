package dev.mv.engine.render.vulkan;

import dev.mv.utils.Utils;
import dev.mv.utils.logger.Logger;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.vulkan.*;

import java.nio.IntBuffer;
import java.nio.LongBuffer;
import java.util.Arrays;

import static dev.mv.engine.render.vulkan.VulkanUtils.vkCheck;
import static org.lwjgl.vulkan.VK13.*;

public class VulkanSwapChain {

    private final VulkanDevice device;
    private final SurfaceFormat surfaceFormat;
    private final long vkSwapChain;
    private final SyncSemaphores[] syncSemaphoresList;
    private ImageView[] imageViews;
    private int currentFrame;
    private VkExtent2D swapChainExtent;

    public VulkanSwapChain(VulkanDevice device, VulkanSurface surface, VulkanWindow window, int requestedImages, boolean vsync) {
        Logger.debug("Creating Vulkan SwapChain");
        this.device = device;
        try (MemoryStack stack = MemoryStack.stackPush()) {

            VulkanPhysicalDevice physicalDevice = device.getPhysicalDevice();

            // Get surface capabilities
            VkSurfaceCapabilitiesKHR surfCapabilities = VkSurfaceCapabilitiesKHR.calloc(stack);
            vkCheck(KHRSurface.vkGetPhysicalDeviceSurfaceCapabilitiesKHR(device.getPhysicalDevice().getVkPhysicalDevice(),
                surface.getVkSurface(), surfCapabilities), "Failed to get surface capabilities");

            int numImages = calcNumImages(surfCapabilities, requestedImages);

            surfaceFormat = calcSurfaceFormat(physicalDevice, surface);

            swapChainExtent = calcSwapChainExtent(stack, window, surfCapabilities);

            VkSwapchainCreateInfoKHR vkSwapchainCreateInfo = VkSwapchainCreateInfoKHR.calloc(stack)
                .sType(KHRSwapchain.VK_STRUCTURE_TYPE_SWAPCHAIN_CREATE_INFO_KHR)
                .surface(surface.getVkSurface())
                .minImageCount(numImages)
                .imageFormat(surfaceFormat.imageFormat())
                .imageColorSpace(surfaceFormat.colorSpace())
                .imageExtent(swapChainExtent)
                .imageArrayLayers(1)
                .imageUsage(VK_IMAGE_USAGE_COLOR_ATTACHMENT_BIT)
                .imageSharingMode(VK_SHARING_MODE_EXCLUSIVE)
                .preTransform(surfCapabilities.currentTransform())
                .compositeAlpha(KHRSurface.VK_COMPOSITE_ALPHA_OPAQUE_BIT_KHR)
                .clipped(true);
            if (vsync) {
                vkSwapchainCreateInfo.presentMode(KHRSurface.VK_PRESENT_MODE_FIFO_KHR);
            } else {
                vkSwapchainCreateInfo.presentMode(KHRSurface.VK_PRESENT_MODE_IMMEDIATE_KHR);
            }
            LongBuffer lp = stack.mallocLong(1);
            vkCheck(KHRSwapchain.vkCreateSwapchainKHR(device.getVkDevice(), vkSwapchainCreateInfo, null, lp),
                "Failed to create swap chain");
            vkSwapChain = lp.get(0);

            imageViews = createImageViews(stack, device, vkSwapChain, surfaceFormat.imageFormat);

            syncSemaphoresList = new SyncSemaphores[numImages];
            for (int i = 0; i < numImages; i++) {
                syncSemaphoresList[i] = new SyncSemaphores(device);
            }
            currentFrame = 0;
        }
    }

    private int calcNumImages(VkSurfaceCapabilitiesKHR surfCapabilities, int requestedImages) {
        int maxImages = surfCapabilities.maxImageCount();
        int minImages = surfCapabilities.minImageCount();
        int result = minImages;
        if (maxImages != 0) {
            result = Math.min(requestedImages, maxImages);
        }
        result = Math.max(result, minImages);
        Logger.debug("Requested [" + requestedImages + "] images, got [" + result + "] images. Surface capabilities, maxImages: [" + maxImages + "], minImages [" + minImages + "]");

        return result;
    }

    private SurfaceFormat calcSurfaceFormat(VulkanPhysicalDevice physicalDevice, VulkanSurface surface) {
        int imageFormat;
        int colorSpace;
        try (MemoryStack stack = MemoryStack.stackPush()) {

            IntBuffer ip = stack.mallocInt(1);
            vkCheck(KHRSurface.vkGetPhysicalDeviceSurfaceFormatsKHR(physicalDevice.getVkPhysicalDevice(),
                surface.getVkSurface(), ip, null), "Failed to get the number surface formats");
            int numFormats = ip.get(0);
            if (numFormats <= 0) {
                throw new RuntimeException("No surface formats retrieved");
            }

            VkSurfaceFormatKHR.Buffer surfaceFormats = VkSurfaceFormatKHR.calloc(numFormats, stack);
            vkCheck(KHRSurface.vkGetPhysicalDeviceSurfaceFormatsKHR(physicalDevice.getVkPhysicalDevice(),
                surface.getVkSurface(), ip, surfaceFormats), "Failed to get surface formats");

            imageFormat = VK_FORMAT_B8G8R8A8_SRGB;
            colorSpace = surfaceFormats.get(0).colorSpace();
            for (int i = 0; i < numFormats; i++) {
                VkSurfaceFormatKHR surfaceFormatKHR = surfaceFormats.get(i);
                if (surfaceFormatKHR.format() == VK_FORMAT_B8G8R8A8_SRGB &&
                    surfaceFormatKHR.colorSpace() == KHRSurface.VK_COLOR_SPACE_SRGB_NONLINEAR_KHR) {
                    imageFormat = surfaceFormatKHR.format();
                    colorSpace = surfaceFormatKHR.colorSpace();
                    break;
                }
            }
        }
        return new SurfaceFormat(imageFormat, colorSpace);
    }

    public VkExtent2D calcSwapChainExtent(MemoryStack stack, VulkanWindow window, VkSurfaceCapabilitiesKHR surfCapabilities) {
        VkExtent2D result = VkExtent2D.calloc(stack);
        if (surfCapabilities.currentExtent().width() == 0xFFFFFFFF) {
            // Surface size undefined. Set to the window size if within bounds
            int width = Utils.clamp(window.getWidth(), surfCapabilities.minImageExtent().width(), surfCapabilities.maxImageExtent().width());
            int height = Utils.clamp(window.getHeight(), surfCapabilities.minImageExtent().height(), surfCapabilities.maxImageExtent().height());

            result.width(width);
            result.height(height);
        } else {
            // Surface already defined, just use that for the swap chain
            result.set(surfCapabilities.currentExtent());
        }
        return result;
    }

    private ImageView[] createImageViews(MemoryStack stack, VulkanDevice device, long swapChain, int format) {
        ImageView[] result;

        IntBuffer ip = stack.mallocInt(1);
        vkCheck(KHRSwapchain.vkGetSwapchainImagesKHR(device.getVkDevice(), swapChain, ip, null),
            "Failed to get number of surface images");
        int numImages = ip.get(0);

        LongBuffer swapChainImages = stack.mallocLong(numImages);
        vkCheck(KHRSwapchain.vkGetSwapchainImagesKHR(device.getVkDevice(), swapChain, ip, swapChainImages),
            "Failed to get surface images");

        result = new ImageView[numImages];
        ImageView.ImageViewData imageViewData = new ImageView.ImageViewData().format(format).aspectMask(VK_IMAGE_ASPECT_COLOR_BIT);
        for (int i = 0; i < numImages; i++) {
            result[i] = new ImageView(device, swapChainImages.get(i), imageViewData);
        }

        return result;
    }

    public boolean acquireNextImage() {
        boolean resize = false;
        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer ip = stack.mallocInt(1);
            int err = KHRSwapchain.vkAcquireNextImageKHR(device.getVkDevice(), vkSwapChain, ~0L,
                syncSemaphoresList[currentFrame].imgAcquisitionSemaphore().getVkSemaphore(), MemoryUtil.NULL, ip);
            if (err == KHRSwapchain.VK_ERROR_OUT_OF_DATE_KHR) {
                resize = true;
            } else if (err == KHRSwapchain.VK_SUBOPTIMAL_KHR) {
                // Not optimal but swapchain can still be used
            } else if (err != VK_SUCCESS) {
                throw new RuntimeException("Failed to acquire image: " + err);
            }
            currentFrame = ip.get(0);
        }

        return resize;
    }

    public boolean presentImage(VulkanQueue queue) {
        boolean resize = false;
        try (MemoryStack stack = MemoryStack.stackPush()) {
            VkPresentInfoKHR present = VkPresentInfoKHR.calloc(stack)
                .sType(KHRSwapchain.VK_STRUCTURE_TYPE_PRESENT_INFO_KHR)
                .pWaitSemaphores(stack.longs(
                    syncSemaphoresList[currentFrame].renderCompleteSemaphore().getVkSemaphore()))
                .swapchainCount(1)
                .pSwapchains(stack.longs(vkSwapChain))
                .pImageIndices(stack.ints(currentFrame));

            int err = KHRSwapchain.vkQueuePresentKHR(queue.getVkQueue(), present);
            if (err == KHRSwapchain.VK_ERROR_OUT_OF_DATE_KHR) {
                resize = true;
            } else if (err == KHRSwapchain.VK_SUBOPTIMAL_KHR) {
                // Not optimal but swap chain can still be used
            } else if (err != VK_SUCCESS) {
                throw new RuntimeException("Failed to present KHR: " + err);
            }
        }
        currentFrame = (currentFrame + 1) % imageViews.length;
        return resize;
    }

    public void cleanup() {
        Logger.debug("Destroying Vulkan SwapChain");
        Arrays.stream(imageViews).forEach(ImageView::cleanup);
        Arrays.stream(syncSemaphoresList).forEach(SyncSemaphores::cleanup);
        KHRSwapchain.vkDestroySwapchainKHR(device.getVkDevice(), vkSwapChain, null);
    }

    public SurfaceFormat getSurfaceFormat() {
        return surfaceFormat;
    }

    public long getVkSwapChain() {
        return vkSwapChain;
    }

    public VulkanDevice getDevice() {
        return device;
    }

    public ImageView[] getImageViews() {
        return imageViews;
    }

    public SyncSemaphores[] getSyncSemaphoresList() {
        return syncSemaphoresList;
    }

    public int getCurrentFrame() {
        return currentFrame;
    }

    public VkExtent2D getSwapChainExtent() {
        return swapChainExtent;
    }

    public record SurfaceFormat(int imageFormat, int colorSpace) {
    }

    public record SyncSemaphores(VulkanSemaphore imgAcquisitionSemaphore, VulkanSemaphore renderCompleteSemaphore) {

        public SyncSemaphores(VulkanDevice device) {
            this(new VulkanSemaphore(device), new VulkanSemaphore(device));
        }

        public void cleanup() {
            imgAcquisitionSemaphore.cleanup();
            renderCompleteSemaphore.cleanup();
        }
    }

    public class ImageView {
        private final VulkanDevice device;
        private final long vkImageView;
        private int aspectMask;
        private int mipLevels;

        public ImageView(VulkanDevice device, long vkImage, ImageViewData imageViewData) {
            this.device = device;
            this.aspectMask = imageViewData.aspectMask;
            this.mipLevels = imageViewData.mipLevels;
            try (MemoryStack stack = MemoryStack.stackPush()) {
                LongBuffer lp = stack.mallocLong(1);
                VkImageViewCreateInfo viewCreateInfo = VkImageViewCreateInfo.calloc(stack)
                    .sType(VK_STRUCTURE_TYPE_IMAGE_VIEW_CREATE_INFO)
                    .image(vkImage)
                    .viewType(imageViewData.viewType)
                    .format(imageViewData.format)
                    .subresourceRange(it -> it
                        .aspectMask(aspectMask)
                        .baseMipLevel(0)
                        .levelCount(mipLevels)
                        .baseArrayLayer(imageViewData.baseArrayLayer)
                        .layerCount(imageViewData.layerCount));

                vkCheck(vkCreateImageView(device.getVkDevice(), viewCreateInfo, null, lp),
                    "Failed to create image view");
                vkImageView = lp.get(0);
            }
        }

        public void cleanup() {
            vkDestroyImageView(device.getVkDevice(), vkImageView, null);
        }

        public long getVkImageView() {
            return vkImageView;
        }

        public static class ImageViewData {
            private int aspectMask;
            private int baseArrayLayer;
            private int format;
            private int layerCount;
            private int mipLevels;
            private int viewType;

            public ImageViewData() {
                this.baseArrayLayer = 0;
                this.layerCount = 1;
                this.mipLevels = 1;
                this.viewType = VK_IMAGE_VIEW_TYPE_2D;
            }

            public ImageView.ImageViewData aspectMask(int aspectMask) {
                this.aspectMask = aspectMask;
                return this;
            }

            public ImageView.ImageViewData baseArrayLayer(int baseArrayLayer) {
                this.baseArrayLayer = baseArrayLayer;
                return this;
            }

            public ImageView.ImageViewData format(int format) {
                this.format = format;
                return this;
            }

            public ImageView.ImageViewData layerCount(int layerCount) {
                this.layerCount = layerCount;
                return this;
            }

            public ImageView.ImageViewData mipLevels(int mipLevels) {
                this.mipLevels = mipLevels;
                return this;
            }

            public ImageView.ImageViewData viewType(int viewType) {
                this.viewType = viewType;
                return this;
            }
        }
    }
}
