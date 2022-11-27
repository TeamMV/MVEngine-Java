package dev.mv.engine.render.vulkan;

import dev.mv.engine.MVEngine;
import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.util.vma.VmaAllocatorCreateInfo;
import org.lwjgl.util.vma.VmaVulkanFunctions;
import org.lwjgl.vulkan.*;

import java.nio.IntBuffer;
import java.nio.LongBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import static org.joml.Math.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.GLFWVulkan.*;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.util.vma.Vma.*;
import static org.lwjgl.vulkan.KHRSurface.*;
import static org.lwjgl.vulkan.KHRSwapchain.*;
import static org.lwjgl.vulkan.VK10.*;

public class Vulkan {
    VulkanContext context;
    long allocator;
    long commandPool;

    public Vulkan(VulkanContext context) {
        this.context = context;
    }

    private static boolean hasStencilComponent(int format) {
        return format == VK_FORMAT_D32_SFLOAT_S8_UINT || format == VK_FORMAT_D24_UNORM_S8_UINT;
    }

    boolean init() {
        if (!createInstance()) return false;
        if (!createSurface()) return false;
        if (!pickPhysicalDevice()) return false;
        if (!createLogicalDevice()) return false;
        if (!createAllocator()) return false;
        //window.memoryTypes.createMemoryTypes();
        if (!createCommandPool()) return false;
        allocateImmediateCmdBuffer();
        if (!createSwapChain()) return false;
        if (!createImageViews()) return false;
        if (!createRenderPass()) return false;
        if (!createDepthResources()) return false;
        createFramebuffers();
        createStagingBuffers();
        return true;
    }

    public void terminate() {
        vkDestroyDevice(window.GPUWrapper, null);
        vkDestroySurfaceKHR(window.instance, window.surface, null);
        vkDestroyInstance(window.instance, null);
    }

    boolean createSurface() {
        try (MemoryStack stack = stackPush()) {
            LongBuffer pSurface = stack.longs(VK_NULL_HANDLE);
            if (glfwCreateWindowSurface(window.instance, window.window, null, pSurface) != VK_SUCCESS) {
                return false;
            }
            window.surface = pSurface.get(0);
        }
        return true;
    }

    private boolean createInstance() {
        try (MemoryStack stack = stackPush()) {
            VkApplicationInfo appInfo = VkApplicationInfo.calloc(stack);
            appInfo.sType(VK_STRUCTURE_TYPE_APPLICATION_INFO);
            appInfo.pApplicationName(stack.UTF8Safe(window.info.title));
            appInfo.applicationVersion(MVEngine.getApplicationConfig().getVersion().toVulkanVersion());
            appInfo.pEngineName(stack.UTF8Safe("MVEngine"));
            appInfo.engineVersion(MVEngine.VERSION.toVulkanVersion());
            appInfo.apiVersion(VK_MAKE_API_VERSION(0, 1, 3, 0));

            VkInstanceCreateInfo createInfo = VkInstanceCreateInfo.calloc(stack);
            createInfo.sType(VK_STRUCTURE_TYPE_INSTANCE_CREATE_INFO);
            createInfo.pApplicationInfo(appInfo);

            createInfo.ppEnabledExtensionNames(glfwGetRequiredInstanceExtensions());

            PointerBuffer instancePtr = stack.mallocPointer(1);

            if (vkCreateInstance(createInfo, null, instancePtr) != VK_SUCCESS) {
                return false;
            }

            window.instance = new VkInstance(instancePtr.get(0), createInfo);

            return true;
        }
    }

    private boolean pickPhysicalDevice() {
        int bestScore = 0;
        try (MemoryStack stack = stackPush()) {
            IntBuffer deviceCount = stack.ints(0);
            vkEnumeratePhysicalDevices(window.instance, deviceCount, null);

            if (deviceCount.get(0) == 0) {
                return false;
            }

            PointerBuffer ppPhysicalDevices = stack.mallocPointer(deviceCount.get(0));
            vkEnumeratePhysicalDevices(window.instance, deviceCount, ppPhysicalDevices);

            int score = 0;
            for (int i = 0; i < ppPhysicalDevices.capacity(); i++) {
                VkPhysicalDevice device = new VkPhysicalDevice(ppPhysicalDevices.get(i), window.instance);
                if (isDeviceSuitable(device)) {
                    if ((score = rateDevice(device)) > bestScore) {
                        bestScore = score;
                        window.GPU = device;
                    }
                }
            }
            if (bestScore == 0 || window.GPU == null) {
                return false;
            }
        }
        return true;
    }

    private int rateDevice(VkPhysicalDevice device) {
        try (MemoryStack stack = stackPush()) {
            int score = 0;
            VkPhysicalDeviceFeatures deviceFeatures = VkPhysicalDeviceFeatures.calloc(stack);
            vkGetPhysicalDeviceFeatures(device, deviceFeatures);
            VkPhysicalDeviceProperties deviceProperties = VkPhysicalDeviceProperties.calloc(stack);
            vkGetPhysicalDeviceProperties(device, deviceProperties);

            if (deviceProperties.deviceType() == VK_PHYSICAL_DEVICE_TYPE_DISCRETE_GPU) {
                score += 32000;
            } else if (deviceProperties.deviceType() == VK_PHYSICAL_DEVICE_TYPE_INTEGRATED_GPU) {
                score += 16000;
            } else if (deviceProperties.deviceType() == VK_PHYSICAL_DEVICE_TYPE_VIRTUAL_GPU) {
                score += 8000;
            }

            score += deviceProperties.limits().maxImageDimension2D();
            score += deviceProperties.limits().maxImageDimension3D();

            if (!deviceFeatures.geometryShader()) {
                score = 0;
            }

            //System.out.println(deviceProperties.deviceNameString() + ": " + score);
            return score;
        }
    }

    private boolean isDeviceSuitable(VkPhysicalDevice device) {
        VulkanQueueFamilyIndices indices = findQueueFamilies(device);
        return indices.isComplete();
    }

    VulkanQueueFamilyIndices findQueueFamilies(VkPhysicalDevice device) {
        VulkanQueueFamilyIndices indices = new VulkanQueueFamilyIndices();
        try (MemoryStack stack = stackPush()) {
            IntBuffer queueFamilyCount = stack.ints(0);
            vkGetPhysicalDeviceQueueFamilyProperties(device, queueFamilyCount, null);
            VkQueueFamilyProperties.Buffer queueFamilies = VkQueueFamilyProperties.malloc(queueFamilyCount.get(0), stack);
            vkGetPhysicalDeviceQueueFamilyProperties(device, queueFamilyCount, queueFamilies);
            IntStream.range(0, queueFamilies.capacity())
                .filter(index -> (queueFamilies.get(index).queueFlags() & VK_QUEUE_GRAPHICS_BIT) != 0)
                .findFirst()
                .ifPresent(index -> indices.graphicsFamily = index);
            return indices;
        }
    }

    private boolean createLogicalDevice() {
        try (MemoryStack stack = stackPush()) {
            VulkanQueueFamilyIndices indices = findQueueFamilies(window.GPU);
            VkDeviceQueueCreateInfo.Buffer queueCreateInfos = VkDeviceQueueCreateInfo.calloc(1, stack);

            queueCreateInfos.sType(VK_STRUCTURE_TYPE_DEVICE_QUEUE_CREATE_INFO);
            queueCreateInfos.queueFamilyIndex(indices.graphicsFamily);
            queueCreateInfos.pQueuePriorities(stack.floats(1.0f));

            VkPhysicalDeviceFeatures deviceFeatures = VkPhysicalDeviceFeatures.callocStack(stack);
            VkDeviceCreateInfo createInfo = VkDeviceCreateInfo.calloc(stack);

            createInfo.sType(VK_STRUCTURE_TYPE_DEVICE_CREATE_INFO);
            createInfo.pQueueCreateInfos(queueCreateInfos);
            createInfo.pEnabledFeatures(deviceFeatures);

            PointerBuffer pDevice = stack.pointers(VK_NULL_HANDLE);
            if (vkCreateDevice(window.GPU, createInfo, null, pDevice) != VK_SUCCESS) {
                return false;
            }

            window.GPUWrapper = new VkDevice(pDevice.get(0), window.GPU, createInfo);
            PointerBuffer pGraphicsQueue = stack.pointers(VK_NULL_HANDLE);
            vkGetDeviceQueue(window.GPUWrapper, indices.graphicsFamily, 0, pGraphicsQueue);
            window.graphicsQueue = new VkQueue(pGraphicsQueue.get(0), window.GPUWrapper);
            vkGetDeviceQueue(window.GPUWrapper, indices.presentFamily, 0, pGraphicsQueue);
            window.presentQueue = new VkQueue(pGraphicsQueue.get(0), window.GPUWrapper);

            return true;
        }
    }

    private boolean createAllocator() {
        try (MemoryStack stack = stackPush()) {

            VmaVulkanFunctions vulkanFunctions = VmaVulkanFunctions.calloc(stack);
            vulkanFunctions.set(window.instance, window.GPUWrapper);

            VmaAllocatorCreateInfo allocatorCreateInfo = VmaAllocatorCreateInfo.calloc(stack);
            allocatorCreateInfo.physicalDevice(window.GPU);
            allocatorCreateInfo.device(window.GPUWrapper);
            allocatorCreateInfo.pVulkanFunctions(vulkanFunctions);
            allocatorCreateInfo.instance(window.instance);

            PointerBuffer pAllocator = stack.pointers(VK_NULL_HANDLE);

            if (vmaCreateAllocator(allocatorCreateInfo, pAllocator) != VK_SUCCESS) {
                return false;
            }

            allocator = pAllocator.get(0);
            return true;
        }
    }

    private boolean createCommandPool() {

        try (MemoryStack stack = stackPush()) {

            VulkanQueueFamilyIndices queueFamilyIndices = findQueueFamilies(window.GPU);

            VkCommandPoolCreateInfo poolInfo = VkCommandPoolCreateInfo.calloc(stack);
            poolInfo.sType(VK_STRUCTURE_TYPE_COMMAND_POOL_CREATE_INFO);
            poolInfo.queueFamilyIndex(queueFamilyIndices.graphicsFamily);
            poolInfo.flags(VK_COMMAND_POOL_CREATE_RESET_COMMAND_BUFFER_BIT);

            LongBuffer pCommandPool = stack.mallocLong(1);

            if (vkCreateCommandPool(window.GPUWrapper, poolInfo, null, pCommandPool) != VK_SUCCESS) {
                return false;
            }

            commandPool = pCommandPool.get(0);
            return true;
        }
    }

    private boolean createSwapChain() {
        try (MemoryStack stack = stackPush()) {
            window.swapChain = new VulkanSwapChain(window);
            long oldSwapChain = window.swapChain.id != 0L ? window.swapChain.id : VK_NULL_HANDLE;

            window.swapChain.supportDetails = querySwapChainSupport(window.GPU, null);

            VkSurfaceFormatKHR surfaceFormat = chooseSwapSurfaceFormat(window.swapChain.supportDetails.formats);
            int presentMode = chooseSwapPresentMode(window.swapChain.supportDetails.presentModes);
            VkExtent2D extent = chooseSwapExtent(window.swapChain.supportDetails.capabilities);

            IntBuffer imageCount = stack.ints(window.swapChain.frameQueueSize);

            if (window.swapChain.supportDetails.capabilities.maxImageCount() > 0 && imageCount.get(0) > window.swapChain.supportDetails.capabilities.maxImageCount()) {
                imageCount.put(0, window.swapChain.supportDetails.capabilities.maxImageCount());
            }

            VkSwapchainCreateInfoKHR createInfo = VkSwapchainCreateInfoKHR.calloc(stack);
            createInfo.sType(VK_STRUCTURE_TYPE_SWAPCHAIN_CREATE_INFO_KHR);
            createInfo.surface(window.surface);

            window.swapChain.imageFormat = surfaceFormat.format();
            window.swapChain.extent = VkExtent2D.create().set(extent);

            createInfo.minImageCount(imageCount.get(0));
            createInfo.imageFormat(window.swapChain.imageFormat);
            createInfo.imageColorSpace(surfaceFormat.colorSpace());
            createInfo.imageExtent(extent);
            createInfo.imageArrayLayers(1);
            createInfo.imageUsage(VK_IMAGE_USAGE_COLOR_ATTACHMENT_BIT);

            VulkanQueueFamilyIndices indices = findQueueFamilies(window.GPU);

            if (!indices.graphicsFamily.equals(indices.presentFamily)) {
                createInfo.imageSharingMode(VK_SHARING_MODE_CONCURRENT);
                createInfo.pQueueFamilyIndices(stack.ints(indices.graphicsFamily, indices.presentFamily));
            } else {
                createInfo.imageSharingMode(VK_SHARING_MODE_EXCLUSIVE);
            }

            createInfo.preTransform(window.swapChain.supportDetails.capabilities.currentTransform());
            createInfo.compositeAlpha(VK_COMPOSITE_ALPHA_OPAQUE_BIT_KHR);
            createInfo.presentMode(presentMode);
            createInfo.clipped(true);
            createInfo.oldSwapchain(oldSwapChain);

            LongBuffer pSwapChain = stack.longs(VK_NULL_HANDLE);

            if (vkCreateSwapchainKHR(window.GPUWrapper, createInfo, null, pSwapChain) != VK_SUCCESS) {
                return false;
            }

            if (oldSwapChain != VK_NULL_HANDLE) {
                window.swapChain.imageViews.forEach(imageView -> vkDestroyImageView(window.GPUWrapper, imageView, null));
                vkDestroySwapchainKHR(window.GPUWrapper, window.swapChain.id, null);
            }

            window.swapChain.id = pSwapChain.get(0);
            vkGetSwapchainImagesKHR(window.GPUWrapper, window.swapChain.id, imageCount, null);
            LongBuffer pSwapchainImages = stack.mallocLong(imageCount.get(0));
            vkGetSwapchainImagesKHR(window.GPUWrapper, window.swapChain.id, imageCount, pSwapchainImages);
            window.swapChain.images = new ArrayList<>(imageCount.get(0));

            for (int i = 0; i < pSwapchainImages.capacity(); i++) {
                window.swapChain.images.add(pSwapchainImages.get(i));
            }
            return true;
        }
    }

    public VulkanSwapChain.SwapChainSupportDetails querySwapChainSupport(VkPhysicalDevice device, MemoryStack stack) {
        VulkanSwapChain.SwapChainSupportDetails details = new VulkanSwapChain.SwapChainSupportDetails();
        if (stack != null) {
            details.capabilities = VkSurfaceCapabilitiesKHR.malloc();
            vkGetPhysicalDeviceSurfaceCapabilitiesKHR(device, window.surface, details.capabilities);

            IntBuffer count = stack.ints(0);
            vkGetPhysicalDeviceSurfaceFormatsKHR(device, window.surface, count, null);

            if (count.get(0) != 0) {
                details.formats = VkSurfaceFormatKHR.malloc(count.get(0));
                vkGetPhysicalDeviceSurfaceFormatsKHR(device, window.surface, count, details.formats);
            }

            vkGetPhysicalDeviceSurfacePresentModesKHR(device, window.surface, count, null);

            if (count.get(0) != 0) {
                details.presentModes = stack.mallocInt(count.get(0));
                vkGetPhysicalDeviceSurfacePresentModesKHR(device, window.surface, count, details.presentModes);
            }
        } else {
            details.capabilities = VkSurfaceCapabilitiesKHR.malloc();
            vkGetPhysicalDeviceSurfaceCapabilitiesKHR(device, window.surface, details.capabilities);

            IntBuffer count = MemoryStack.stackInts(0);
            vkGetPhysicalDeviceSurfaceFormatsKHR(device, window.surface, count, null);

            if (count.get(0) != 0) {
                details.formats = VkSurfaceFormatKHR.malloc(count.get(0));
                vkGetPhysicalDeviceSurfaceFormatsKHR(device, window.surface, count, details.formats);
            }

            vkGetPhysicalDeviceSurfacePresentModesKHR(device, window.surface, count, null);

            if (count.get(0) != 0) {
                details.presentModes = MemoryUtil.memAllocInt(count.get(0));
                vkGetPhysicalDeviceSurfacePresentModesKHR(device, window.surface, count, details.presentModes);
            }
        }
        return details;
    }

    private VkSurfaceFormatKHR chooseSwapSurfaceFormat(VkSurfaceFormatKHR.Buffer availableFormats) {
        List<VkSurfaceFormatKHR> list = availableFormats.stream().toList();

        VkSurfaceFormatKHR format = list.get(0);
        boolean flag = true;

        for (VkSurfaceFormatKHR availableFormat : list) {
            if (availableFormat.format() == VK_FORMAT_R8G8B8A8_UNORM && availableFormat.colorSpace() == VK_COLOR_SPACE_SRGB_NONLINEAR_KHR)
                return availableFormat;

            if (availableFormat.format() == VK_FORMAT_B8G8R8A8_UNORM && availableFormat.colorSpace() == VK_COLOR_SPACE_SRGB_NONLINEAR_KHR) {
                format = availableFormat;
                flag = false;
            }
        }

        //TODO: logging here
        //if(flag) System.out.println("Non-optimal surface format.");
        return format;
    }

    private int chooseSwapPresentMode(IntBuffer availablePresentModes) {
        if (!window.info.vsync) {
            for (int i = 0; i < availablePresentModes.capacity(); i++) {
                if (availablePresentModes.get(i) == VK_PRESENT_MODE_MAILBOX_KHR) {
                    return availablePresentModes.get(i);
                }
            }
            for (int i = 0; i < availablePresentModes.capacity(); i++) {
                if (availablePresentModes.get(i) == VK_PRESENT_MODE_IMMEDIATE_KHR) {
                    return availablePresentModes.get(i);
                }
            }
        }
        return VK_PRESENT_MODE_FIFO_KHR;
    }

    private VkExtent2D chooseSwapExtent(VkSurfaceCapabilitiesKHR capabilities) {
        if (capabilities.currentExtent().width() != 0xFFFFFFFF) {
            return capabilities.currentExtent();
        }

        MemoryStack stack = MemoryStack.stackGet();
        IntBuffer width = stack.ints(0);
        IntBuffer height = stack.ints(0);
        stack.close();

        glfwGetFramebufferSize(window.window, width, height);

        VkExtent2D actualExtent = VkExtent2D.malloc().set(width.get(0), height.get(0));

        VkExtent2D minExtent = capabilities.minImageExtent();
        VkExtent2D maxExtent = capabilities.maxImageExtent();

        actualExtent.width(clamp(minExtent.width(), maxExtent.width(), actualExtent.width()));
        actualExtent.height(clamp(minExtent.height(), maxExtent.height(), actualExtent.height()));

        return actualExtent;
    }

    private boolean createImageViews() {

        window.swapChain.imageViews = new ArrayList<>(window.swapChain.images.size());

        for (long swapChainImage : window.swapChain.images) {
            try {
                window.swapChain.imageViews.add(createImageView(swapChainImage, window.swapChain.imageFormat, VK_IMAGE_ASPECT_COLOR_BIT, 1));
            } catch (Exception e) {
                return false;
            }
        }

        return true;
    }

    public long createImageView(long image, int format, int aspectFlags, int mipLevels) throws Exception {

        try (MemoryStack stack = stackPush()) {

            VkImageViewCreateInfo viewInfo = VkImageViewCreateInfo.calloc(stack);
            viewInfo.sType(VK_STRUCTURE_TYPE_IMAGE_VIEW_CREATE_INFO);
            viewInfo.image(image);
            viewInfo.viewType(VK_IMAGE_VIEW_TYPE_2D);
            viewInfo.format(format);
            viewInfo.subresourceRange().aspectMask(aspectFlags);
            viewInfo.subresourceRange().baseMipLevel(0);
            viewInfo.subresourceRange().levelCount(mipLevels);
            viewInfo.subresourceRange().baseArrayLayer(0);
            viewInfo.subresourceRange().layerCount(1);

            LongBuffer pImageView = stack.mallocLong(1);

            if (vkCreateImageView(window.GPUWrapper, viewInfo, null, pImageView) != VK_SUCCESS) {
                throw new Exception();
            }

            return pImageView.get(0);
        }
    }

    private boolean createRenderPass() {
        try (MemoryStack stack = stackPush()) {
            VkAttachmentDescription.Buffer attachments = VkAttachmentDescription.callocStack(2, stack);
            VkAttachmentReference.Buffer attachmentRefs = VkAttachmentReference.callocStack(2, stack);

            VkAttachmentDescription colorAttachment = attachments.get(0);
            colorAttachment.format(window.swapChain.imageFormat);
            colorAttachment.samples(VK_SAMPLE_COUNT_1_BIT);
            colorAttachment.loadOp(VK_ATTACHMENT_LOAD_OP_CLEAR);
            colorAttachment.storeOp(VK_ATTACHMENT_STORE_OP_STORE);
            colorAttachment.stencilLoadOp(VK_ATTACHMENT_LOAD_OP_DONT_CARE);
            colorAttachment.stencilStoreOp(VK_ATTACHMENT_STORE_OP_DONT_CARE);
            colorAttachment.initialLayout(VK_IMAGE_LAYOUT_UNDEFINED);
            colorAttachment.finalLayout(VK_IMAGE_LAYOUT_PRESENT_SRC_KHR);

            int y = attachments.get(0).samples();

            VkAttachmentReference colorAttachmentRef = attachmentRefs.get(0);
            colorAttachmentRef.attachment(0);
            colorAttachmentRef.layout(VK_IMAGE_LAYOUT_COLOR_ATTACHMENT_OPTIMAL);

            VkAttachmentDescription depthAttachment = attachments.get(1);
            try {
                depthAttachment.format(findDepthFormat());
            } catch (Exception e) {
                return false;
            }
            depthAttachment.samples(VK_SAMPLE_COUNT_1_BIT);
            depthAttachment.loadOp(VK_ATTACHMENT_LOAD_OP_CLEAR);
            depthAttachment.storeOp(VK_ATTACHMENT_STORE_OP_DONT_CARE);
            depthAttachment.stencilLoadOp(VK_ATTACHMENT_LOAD_OP_DONT_CARE);
            depthAttachment.stencilStoreOp(VK_ATTACHMENT_STORE_OP_DONT_CARE);
            depthAttachment.initialLayout(VK_IMAGE_LAYOUT_UNDEFINED);
            depthAttachment.finalLayout(VK_IMAGE_LAYOUT_DEPTH_STENCIL_ATTACHMENT_OPTIMAL);

            VkAttachmentReference depthAttachmentRef = attachmentRefs.get(1);
            depthAttachmentRef.attachment(1);
            depthAttachmentRef.layout(VK_IMAGE_LAYOUT_DEPTH_STENCIL_ATTACHMENT_OPTIMAL);

            VkSubpassDescription.Buffer subpass = VkSubpassDescription.callocStack(1, stack);
            subpass.pipelineBindPoint(VK_PIPELINE_BIND_POINT_GRAPHICS);
            subpass.colorAttachmentCount(1);
            subpass.pColorAttachments(VkAttachmentReference.callocStack(1, stack).put(0, colorAttachmentRef));
            subpass.pDepthStencilAttachment(depthAttachmentRef);

            VkSubpassDependency.Buffer dependency = VkSubpassDependency.callocStack(1, stack);
            dependency.srcSubpass(VK_SUBPASS_EXTERNAL);
            dependency.dstSubpass(0);
            dependency.srcStageMask(VK_PIPELINE_STAGE_COLOR_ATTACHMENT_OUTPUT_BIT | VK_PIPELINE_STAGE_EARLY_FRAGMENT_TESTS_BIT);
            dependency.srcAccessMask(0);
            dependency.dstStageMask(VK_PIPELINE_STAGE_COLOR_ATTACHMENT_OUTPUT_BIT | VK_PIPELINE_STAGE_EARLY_FRAGMENT_TESTS_BIT);
            dependency.dstAccessMask(VK_ACCESS_COLOR_ATTACHMENT_READ_BIT | VK_ACCESS_DEPTH_STENCIL_ATTACHMENT_WRITE_BIT);

            VkRenderPassCreateInfo renderPassInfo = VkRenderPassCreateInfo.callocStack(stack);
            renderPassInfo.sType(VK_STRUCTURE_TYPE_RENDER_PASS_CREATE_INFO);
            renderPassInfo.pAttachments(attachments);
            renderPassInfo.pSubpasses(subpass);
            //renderPassInfo.pDependencies(dependency);

            LongBuffer pRenderPass = stack.mallocLong(1);

            if (vkCreateRenderPass(window.GPUWrapper, renderPassInfo, null, pRenderPass) != VK_SUCCESS) {
                return false;
            }

            window.renderPass = pRenderPass.get(0);

            return true;
        }
    }

    private int findDepthFormat() throws Exception {
        return findSupportedFormat(
            MemoryStack.stackGet().ints(VK_FORMAT_D32_SFLOAT, VK_FORMAT_D32_SFLOAT_S8_UINT, VK_FORMAT_D24_UNORM_S8_UINT),
            VK_IMAGE_TILING_OPTIMAL,
            VK_FORMAT_FEATURE_DEPTH_STENCIL_ATTACHMENT_BIT);
    }

    private int findSupportedFormat(IntBuffer formatCandidates, int tiling, int features) throws Exception {
        try (MemoryStack stack = stackPush()) {
            VkFormatProperties props = VkFormatProperties.calloc(stack);
            for (int i = 0; i < formatCandidates.capacity(); i++) {
                int format = formatCandidates.get(i);
                vkGetPhysicalDeviceFormatProperties(window.GPU, format, props);
                if (tiling == VK_IMAGE_TILING_LINEAR && (props.linearTilingFeatures() & features) == features) {
                    return format;
                } else if (tiling == VK_IMAGE_TILING_OPTIMAL && (props.optimalTilingFeatures() & features) == features) {
                    return format;
                }
            }
        }
        throw new Exception();
    }

    private boolean createDepthResources() {
        try (MemoryStack stack = stackPush()) {
            int depthFormat = 0;
            try {
                depthFormat = findDepthFormat();
            } catch (Exception e) {
                return false;
            }
            LongBuffer pDepthImage = stack.mallocLong(1);
            PointerBuffer pDepthImageMemory = stack.mallocPointer(1);
            window.memoryManager.createImage(window.swapChain.extent.width(), window.swapChain.extent.height(), 1, depthFormat, VK_IMAGE_TILING_OPTIMAL, VK_IMAGE_USAGE_DEPTH_STENCIL_ATTACHMENT_BIT, VK_MEMORY_PROPERTY_DEVICE_LOCAL_BIT, pDepthImage, pDepthImageMemory);
            depthImage = pDepthImage.get(0);
            depthImageMemory = pDepthImageMemory.get(0);
            try {
                depthImageView = createImageView(depthImage, depthFormat, VK_IMAGE_ASPECT_DEPTH_BIT, 1);
            } catch (Exception e) {
                return false;
            }
            VkCommandBuffer commandBuffer = beginImmediateCmd();
            transitionImageLayout(commandBuffer, depthImage, depthFormat,
                VK_IMAGE_LAYOUT_UNDEFINED, VK_IMAGE_LAYOUT_DEPTH_STENCIL_ATTACHMENT_OPTIMAL, 1);
            endImmediateCmd();
        }
        return true;
    }

    public VkCommandBuffer beginImmediateCmd() {
        try (MemoryStack stack = stackPush()) {
            VkCommandBufferBeginInfo beginInfo = VkCommandBufferBeginInfo.calloc(stack);
            beginInfo.sType(VK_STRUCTURE_TYPE_COMMAND_BUFFER_BEGIN_INFO);

            vkBeginCommandBuffer(window.immediateCmdBuffer, beginInfo);
        }
        return window.immediateCmdBuffer;
    }

    public void endImmediateCmd() {
        try (MemoryStack stack = stackPush()) {
            vkEndCommandBuffer(window.immediateCmdBuffer);

            VkSubmitInfo.Buffer submitInfo = VkSubmitInfo.calloc(1, stack);
            submitInfo.sType(VK_STRUCTURE_TYPE_SUBMIT_INFO);
            submitInfo.pCommandBuffers(stack.pointers(window.immediateCmdBuffer));

            vkQueueSubmit(window.graphicsQueue, submitInfo, immediateFence);

            vkWaitForFences(window.GPUWrapper, immediateFence, true, 0xFFFFFFFFFFFFFFFFL);
            vkResetFences(window.GPUWrapper, immediateFence);
            vkResetCommandBuffer(window.immediateCmdBuffer, 0);
        }

    }

    public boolean transitionImageLayout(VkCommandBuffer commandBuffer, long image, int format, int oldLayout, int newLayout, int mipLevels) {
        try (MemoryStack stack = stackPush()) {
            VkImageMemoryBarrier.Buffer barrier = VkImageMemoryBarrier.calloc(1, stack);
            barrier.sType(VK_STRUCTURE_TYPE_IMAGE_MEMORY_BARRIER);
            barrier.oldLayout(oldLayout);
            barrier.newLayout(newLayout);
            barrier.srcQueueFamilyIndex(VK_QUEUE_FAMILY_IGNORED);
            barrier.dstQueueFamilyIndex(VK_QUEUE_FAMILY_IGNORED);
            barrier.image(image);

            barrier.subresourceRange().baseMipLevel(0);
            barrier.subresourceRange().levelCount(mipLevels);
            barrier.subresourceRange().baseArrayLayer(0);
            barrier.subresourceRange().layerCount(1);

            if (newLayout == VK_IMAGE_LAYOUT_DEPTH_STENCIL_ATTACHMENT_OPTIMAL) {
                barrier.subresourceRange().aspectMask(VK_IMAGE_ASPECT_DEPTH_BIT);
                if (hasStencilComponent(format)) {
                    barrier.subresourceRange().aspectMask(barrier.subresourceRange().aspectMask() | VK_IMAGE_ASPECT_STENCIL_BIT);
                }
            } else {
                barrier.subresourceRange().aspectMask(VK_IMAGE_ASPECT_COLOR_BIT);
            }

            int sourceStage;
            int destinationStage;

            if (oldLayout == VK_IMAGE_LAYOUT_UNDEFINED && newLayout == VK_IMAGE_LAYOUT_TRANSFER_DST_OPTIMAL) {
                barrier.srcAccessMask(0);
                barrier.dstAccessMask(VK_ACCESS_TRANSFER_WRITE_BIT);

                sourceStage = VK_PIPELINE_STAGE_TOP_OF_PIPE_BIT;
                destinationStage = VK_PIPELINE_STAGE_TRANSFER_BIT;
            } else if (oldLayout == VK_IMAGE_LAYOUT_TRANSFER_DST_OPTIMAL && newLayout == VK_IMAGE_LAYOUT_SHADER_READ_ONLY_OPTIMAL) {
                barrier.srcAccessMask(VK_ACCESS_TRANSFER_WRITE_BIT);
                barrier.dstAccessMask(VK_ACCESS_SHADER_READ_BIT);

                sourceStage = VK_PIPELINE_STAGE_TRANSFER_BIT;
                destinationStage = VK_PIPELINE_STAGE_FRAGMENT_SHADER_BIT;
            } else if (oldLayout == VK_IMAGE_LAYOUT_UNDEFINED && newLayout == VK_IMAGE_LAYOUT_DEPTH_STENCIL_ATTACHMENT_OPTIMAL) {
                barrier.srcAccessMask(0);
                barrier.dstAccessMask(VK_ACCESS_DEPTH_STENCIL_ATTACHMENT_READ_BIT | VK_ACCESS_DEPTH_STENCIL_ATTACHMENT_WRITE_BIT);

                sourceStage = VK_PIPELINE_STAGE_TOP_OF_PIPE_BIT;
                destinationStage = VK_PIPELINE_STAGE_EARLY_FRAGMENT_TESTS_BIT;
            } else {
                return false;
            }

            vkCmdPipelineBarrier(commandBuffer, sourceStage, destinationStage, 0, null, null, barrier);
        }
        return true;
    }

    private void allocateImmediateCmdBuffer() {
        try(MemoryStack stack = stackPush()) {
            VkCommandBufferAllocateInfo allocInfo = VkCommandBufferAllocateInfo.callocStack(stack);
            allocInfo.sType(VK_STRUCTURE_TYPE_COMMAND_BUFFER_ALLOCATE_INFO);
            allocInfo.level(VK_COMMAND_BUFFER_LEVEL_PRIMARY);
            allocInfo.commandPool(commandPool);
            allocInfo.commandBufferCount(1);

            PointerBuffer pCommandBuffer = stack.mallocPointer(1);
            vkAllocateCommandBuffers(window.GPUWrapper, allocInfo, pCommandBuffer);
            window.immediateCmdBuffer = new VkCommandBuffer(pCommandBuffer.get(0), window.GPUWrapper);

            VkFenceCreateInfo fenceInfo = VkFenceCreateInfo.callocStack(stack);
            fenceInfo.sType(VK_STRUCTURE_TYPE_FENCE_CREATE_INFO);
            fenceInfo.flags(VK_FENCE_CREATE_SIGNALED_BIT);

            LongBuffer pFence = stack.mallocLong(1);
            vkCreateFence(window.GPUWrapper, fenceInfo, null, pFence);
            vkResetFences(window.GPUWrapper,  pFence.get(0));

            immediateFence = pFence.get(0);
        }
    }

    private boolean createFramebuffers() {
        window.swapChain.framebuffers = new ArrayList<>(window.swapChain.imageViews.size());
        try(MemoryStack stack = stackPush()) {
            LongBuffer attachments = stack.longs(VK_NULL_HANDLE, depthImageView);
            LongBuffer pFramebuffer = stack.mallocLong(1);

            VkFramebufferCreateInfo framebufferInfo = VkFramebufferCreateInfo.callocStack(stack);
            framebufferInfo.sType(VK_STRUCTURE_TYPE_FRAMEBUFFER_CREATE_INFO);
            framebufferInfo.renderPass(window.renderPass);
            framebufferInfo.width(window.swapChain.extent.width());
            framebufferInfo.height(window.swapChain.extent.height());
            framebufferInfo.layers(1);

            for(long imageView : window.swapChain.imageViews) {
                attachments.put(0, imageView);
                framebufferInfo.pAttachments(attachments);
                if(vkCreateFramebuffer(window.GPUWrapper, framebufferInfo, null, pFramebuffer) != VK_SUCCESS) {
                    return false;
                }
                window.swapChain.framebuffers.add(pFramebuffer.get(0));
            }
        }
        return true;
    }

    private void createStagingBuffers() {
        window.stagingBuffers = new VulkanStagingBuffer[window.swapChain.images.size()];

        for(int i = 0; i < window.stagingBuffers.length; ++i) {
            window.stagingBuffers[i] = new VulkanStagingBuffer(30 * 1024 * 1024, window);
        }
    }
}
