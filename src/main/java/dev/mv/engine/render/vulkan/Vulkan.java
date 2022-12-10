package dev.mv.engine.render.vulkan;

import dev.mv.engine.MVEngine;
import dev.mv.engine.render.utils.RenderUtils;
import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.*;

import java.io.IOException;
import java.nio.IntBuffer;
import java.nio.LongBuffer;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static dev.mv.engine.render.utils.RenderUtils.asPointerBuffer;
import static org.lwjgl.glfw.GLFWVulkan.glfwCreateWindowSurface;
import static org.lwjgl.glfw.GLFWVulkan.glfwGetRequiredInstanceExtensions;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.vulkan.KHRSurface.*;
import static org.lwjgl.vulkan.KHRSwapchain.*;
import static org.lwjgl.vulkan.VK13.*;

public class Vulkan {
    VulkanContext context;
    long allocator;
    long commandPool;

    private static final Set<String> DEVICE_EXTENSIONS = Stream.of(VK_KHR_SWAPCHAIN_EXTENSION_NAME).collect(Collectors.toSet());

    Vulkan(VulkanContext context) {
        this.context = context;
    }

    private static boolean hasStencilComponent(int format) {
        return format == VK_FORMAT_D32_SFLOAT_S8_UINT || format == VK_FORMAT_D24_UNORM_S8_UINT;
    }

    boolean init() {
        VulkanShaderCreateInfo shaderCreateInfo = new VulkanShaderCreateInfo();
        shaderCreateInfo.vertexPath = "/shaders/2d/vDefault.vert";
        shaderCreateInfo.fragmentPath = "/shaders/2d/vDefault.frag";

        VulkanProgramCreateInfo programCreateInfo = new VulkanProgramCreateInfo();
        programCreateInfo.renderMode = RenderMode.TRIANGLES;
        programCreateInfo.shaderCreateInfo = shaderCreateInfo;

        VulkanProgramsCreateInfo programsCreateInfo = new VulkanProgramsCreateInfo();
        programsCreateInfo.programs.add(programCreateInfo);

        if(!createInstance()) return false;
        if(!createSurface()) return false;
        if(!pickPhysicalDevice()) return false;
        if(!createLogicalDevice()) return false;
        if(!createSwapChain()) return false;
        System.out.println(5);
        if(!createGraphicsPipelinesAndRenderPasses(programsCreateInfo)) return false;
        System.out.println(6);
        return true;
    }

    public void terminate() {
        vkDestroyDevice(context.logicalGPU, null);
        vkDestroySurfaceKHR(context.instance, context.window.surface, null);
        vkDestroyInstance(context.instance, null);
    }

    private boolean createInstance() {
        try (MemoryStack stack = stackPush()) {
            VkApplicationInfo appInfo = VkApplicationInfo.calloc(stack);
            appInfo.sType(VK_STRUCTURE_TYPE_APPLICATION_INFO);
            appInfo.pApplicationName(stack.UTF8Safe(context.window.info.title));
            appInfo.applicationVersion(MVEngine.getApplicationConfig().getVersion().toVulkanVersion());
            appInfo.pEngineName(stack.UTF8Safe("MVEngine"));
            appInfo.engineVersion(MVEngine.VERSION.toVulkanVersion());
            appInfo.apiVersion(VK_API_VERSION_1_3);

            VkInstanceCreateInfo createInfo = VkInstanceCreateInfo.calloc(stack);
            createInfo.sType(VK_STRUCTURE_TYPE_INSTANCE_CREATE_INFO);
            createInfo.pApplicationInfo(appInfo);

            createInfo.ppEnabledExtensionNames(glfwGetRequiredInstanceExtensions());

            PointerBuffer instancePtr = stack.mallocPointer(1);

            if (vkCreateInstance(createInfo, null, instancePtr) != VK_SUCCESS) {
                return false;
            }

            context.instance = new VkInstance(instancePtr.get(0), createInfo);

            return true;
        }
    }
    
    private boolean pickPhysicalDevice() {
        try(MemoryStack stack = stackPush()) {
            IntBuffer deviceCount = stack.ints(0);
            vkEnumeratePhysicalDevices(context.instance, deviceCount, null);
            if(deviceCount.get(0) == 0) {
                return false;
            }
            PointerBuffer ppPhysicalDevices = stack.mallocPointer(deviceCount.get(0));
            vkEnumeratePhysicalDevices(context.instance, deviceCount, ppPhysicalDevices);

            int score = 0;
            int bestScore = 0;
            for(int i = 0;i < ppPhysicalDevices.capacity();i++) {
                VkPhysicalDevice device = new VkPhysicalDevice(ppPhysicalDevices.get(i), context.instance);
                if(isDeviceSuitable(device)) {
                    if((score = rateDevice(device)) > bestScore) {
                        bestScore = score;
                        context.GPU = device;
                    }
                }
            }
            if (bestScore == 0) {
                return false;
            }
            return true;
        }
    }

    private boolean createLogicalDevice() {
        VulkanQueueFamilyIndices indices = findQueueFamilies(context.GPU);

        try(MemoryStack stack = MemoryStack.stackPush()) {
            VkDeviceQueueCreateInfo queueCreateInfo = VkDeviceQueueCreateInfo.calloc(stack);
            queueCreateInfo.sType(VK_STRUCTURE_TYPE_DEVICE_QUEUE_CREATE_INFO);
            queueCreateInfo.queueFamilyIndex(indices.graphicsFamily);
            float queuePriority = 1.0f;
            queueCreateInfo.pQueuePriorities(RenderUtils.store(new float[] {queuePriority}));

            VkDeviceQueueCreateInfo.Buffer pQueueCreateInfo = VkDeviceQueueCreateInfo.calloc(1, stack);
            pQueueCreateInfo.put(0, queueCreateInfo);

            VkPhysicalDeviceFeatures deviceFeatures = VkPhysicalDeviceFeatures.calloc(stack);
            VkDeviceCreateInfo createInfo = VkDeviceCreateInfo.calloc(stack);
            createInfo.sType(VK_STRUCTURE_TYPE_DEVICE_CREATE_INFO);
            createInfo.pQueueCreateInfos(pQueueCreateInfo);
            createInfo.pEnabledFeatures(deviceFeatures);
            createInfo.ppEnabledExtensionNames(asPointerBuffer(DEVICE_EXTENSIONS));

            PointerBuffer pLogicalDevice = stack.callocPointer(1);
            if (vkCreateDevice(context.GPU, createInfo, null, pLogicalDevice) != VK_SUCCESS) {
                return false;
            }

            context.logicalGPU = new VkDevice(pLogicalDevice.get(0), context.GPU, createInfo);
        }

        return true;
    }

    private boolean createSurface() {
        try(MemoryStack stack = stackPush()) {
            LongBuffer pSurface = stack.longs(VK_NULL_HANDLE);
            if(glfwCreateWindowSurface(context.instance, context.window.window, null, pSurface) != VK_SUCCESS) {
                return false;
            }
            context.window.surface = pSurface.get(0);
        }
        return true;
    }

    private boolean createSwapChain() {
        try(MemoryStack stack = stackPush()) {
            VulkanSwapChain.SwapChainSupportDetails swapChainSupport = querySwapChainSupport(context.GPU, stack);

            VkSurfaceFormatKHR surfaceFormat = chooseSwapSurfaceFormat(swapChainSupport.formats);
            int presentMode = chooseSwapPresentMode(swapChainSupport.presentModes);
            VkExtent2D extent = chooseSwapExtent(swapChainSupport.capabilities);

            IntBuffer imageCount = stack.ints(swapChainSupport.capabilities.minImageCount() + 1);

            if(swapChainSupport.capabilities.maxImageCount() > 0 && imageCount.get(0) > swapChainSupport.capabilities.maxImageCount()) {
                imageCount.put(0, swapChainSupport.capabilities.maxImageCount());
            }

            VkSwapchainCreateInfoKHR createInfo = VkSwapchainCreateInfoKHR.calloc(stack);

            createInfo.sType(VK_STRUCTURE_TYPE_SWAPCHAIN_CREATE_INFO_KHR);
            createInfo.surface(context.window.surface);

            // Image settings
            createInfo.minImageCount(imageCount.get(0));
            createInfo.imageFormat(surfaceFormat.format());
            createInfo.imageColorSpace(surfaceFormat.colorSpace());
            createInfo.imageExtent(extent);
            createInfo.imageArrayLayers(1);
            createInfo.imageUsage(VK_IMAGE_USAGE_COLOR_ATTACHMENT_BIT);

            VulkanQueueFamilyIndices indices = findQueueFamilies(context.GPU);

            if(!indices.graphicsFamily.equals(indices.presentFamily)) {
                createInfo.imageSharingMode(VK_SHARING_MODE_CONCURRENT);
                createInfo.pQueueFamilyIndices(stack.ints(indices.graphicsFamily, indices.presentFamily));
            } else {
                createInfo.imageSharingMode(VK_SHARING_MODE_EXCLUSIVE);
            }

            createInfo.preTransform(swapChainSupport.capabilities.currentTransform());
            createInfo.compositeAlpha(VK_COMPOSITE_ALPHA_OPAQUE_BIT_KHR);
            createInfo.presentMode(presentMode);
            createInfo.clipped(true);

            createInfo.oldSwapchain(VK_NULL_HANDLE);

            LongBuffer pSwapChain = stack.longs(VK_NULL_HANDLE);

            if(vkCreateSwapchainKHR(context.logicalGPU, createInfo, null, pSwapChain) != VK_SUCCESS) {
                return false;
            }

            context.swapChain.id = pSwapChain.get(0);

            vkGetSwapchainImagesKHR(context.logicalGPU, context.swapChain.id, imageCount, null);

            LongBuffer pSwapchainImages = stack.mallocLong(imageCount.get(0));

            vkGetSwapchainImagesKHR(context.logicalGPU, context.swapChain.id, imageCount, pSwapchainImages);

            context.swapChain.images = new ArrayList<>(imageCount.get(0));

            for(int i = 0;i < pSwapchainImages.capacity();i++) {
                context.swapChain.images.add(pSwapchainImages.get(i));
            }

            context.swapChain.imageFormat = surfaceFormat.format();
            context.swapChain.extent = VkExtent2D.create().set(extent);
        }

        return true;
    }

    private boolean createImageViews() {
        context.swapChain.imageViews = new ArrayList<>(context.swapChain.images.size());
        try(MemoryStack stack = stackPush()) {
            LongBuffer pImageView = stack.mallocLong(1);
            for(long swapChainImage : context.swapChain.images) {
                VkImageViewCreateInfo createInfo = VkImageViewCreateInfo.calloc(stack);
                createInfo.sType(VK_STRUCTURE_TYPE_IMAGE_VIEW_CREATE_INFO);
                createInfo.image(swapChainImage);
                createInfo.viewType(VK_IMAGE_VIEW_TYPE_2D);
                createInfo.format(context.swapChain.imageFormat);

                createInfo.components().r(VK_COMPONENT_SWIZZLE_IDENTITY);
                createInfo.components().g(VK_COMPONENT_SWIZZLE_IDENTITY);
                createInfo.components().b(VK_COMPONENT_SWIZZLE_IDENTITY);
                createInfo.components().a(VK_COMPONENT_SWIZZLE_IDENTITY);

                createInfo.subresourceRange().aspectMask(VK_IMAGE_ASPECT_COLOR_BIT);
                createInfo.subresourceRange().baseMipLevel(0);
                createInfo.subresourceRange().levelCount(1);
                createInfo.subresourceRange().baseArrayLayer(0);
                createInfo.subresourceRange().layerCount(1);

                if (vkCreateImageView(context.logicalGPU, createInfo, null, pImageView) != VK_SUCCESS) {
                    return false;
                }
                context.swapChain.imageViews.add(pImageView.get(0));
            }
        }
        return true;
    }

    private boolean createGraphicsPipelinesAndRenderPasses(VulkanProgramsCreateInfo programsCreateInfo) {
        context.programs = new VulkanProgram[programsCreateInfo.programs.size()];
        int i = 0;
        for(VulkanProgramCreateInfo programCreateInfo : programsCreateInfo.programs) {
            try {
                VulkanShader shader = new VulkanShader(programCreateInfo.shaderCreateInfo, context);
                System.out.println("Shader parsed");
                shader.make(context.window);
                System.out.println("Shader made");
                int iShader = VulkanProgram.genShader(shader);
                System.out.println("Shader id made");
                VulkanRenderPass renderPass = new VulkanRenderPass(context);
                System.out.println("Render pass made");
                int iRenderPass = VulkanProgram.genRenderPass(renderPass);
                System.out.println("Render pass id made");
                VulkanPipeline pipeline = new VulkanPipeline(context, shader, programCreateInfo.renderMode, renderPass);
                System.out.println("Pipeline made");
                int iPipeline = VulkanProgram.genPipeline(pipeline);
                System.out.println("Pipeline id made");
                VulkanProgram program = new VulkanProgram(iShader, iPipeline, iRenderPass);
                System.out.println("Program made");
                context.programs[i] = program;
            } catch (Exception e) {
                VulkanProgram.cleanupMess();
                return false;
            }
        }
        return true;
    }

    public enum RenderMode {
        POINTS(1),
        LINES(2),
        LINE_STRIP(3),
        TRIANGLES(4),
        TRIANGLE_STRIP(5);
        int i;

        RenderMode(int i) {
            this.i = i;
        }

        int asVulkanMode() {
            return switch(i) {
                case 1: yield VK_PRIMITIVE_TOPOLOGY_POINT_LIST;
                case 2: yield VK_PRIMITIVE_TOPOLOGY_LINE_LIST;
                case 3: yield VK_PRIMITIVE_TOPOLOGY_LINE_STRIP;
                case 4: yield VK_PRIMITIVE_TOPOLOGY_TRIANGLE_LIST;
                case 5: yield VK_PRIMITIVE_TOPOLOGY_TRIANGLE_STRIP;
                default: yield -1;
            };
        }
    }

    private VkSurfaceFormatKHR chooseSwapSurfaceFormat(VkSurfaceFormatKHR.Buffer availableFormats) {
        return availableFormats.stream()
            .filter(availableFormat -> availableFormat.format() == VK_FORMAT_B8G8R8_UNORM)
            .filter(availableFormat -> availableFormat.colorSpace() == VK_COLOR_SPACE_SRGB_NONLINEAR_KHR)
            .findAny()
            .orElse(availableFormats.get(0));
    }

    private int chooseSwapPresentMode(IntBuffer availablePresentModes) {

        for(int i = 0;i < availablePresentModes.capacity();i++) {
            if(availablePresentModes.get(i) == VK_PRESENT_MODE_MAILBOX_KHR) {
                return availablePresentModes.get(i);
            }
        }

        return VK_PRESENT_MODE_FIFO_KHR;
    }

    private VkExtent2D chooseSwapExtent(VkSurfaceCapabilitiesKHR capabilities) {

        if(capabilities.currentExtent().width() != Integer.MAX_VALUE) {
            return capabilities.currentExtent();
        }

        VkExtent2D actualExtent = VkExtent2D.malloc().set(context.window.width, context.window.height);

        VkExtent2D minExtent = capabilities.minImageExtent();
        VkExtent2D maxExtent = capabilities.maxImageExtent();

        actualExtent.width(clamp(minExtent.width(), maxExtent.width(), actualExtent.width()));
        actualExtent.height(clamp(minExtent.height(), maxExtent.height(), actualExtent.height()));

        return actualExtent;
    }

    private VulkanSwapChain.SwapChainSupportDetails querySwapChainSupport(VkPhysicalDevice device, MemoryStack stack) {

        VulkanSwapChain.SwapChainSupportDetails details = new VulkanSwapChain.SwapChainSupportDetails();

        details.capabilities = VkSurfaceCapabilitiesKHR.malloc(stack);
        vkGetPhysicalDeviceSurfaceCapabilitiesKHR(device, context.window.surface, details.capabilities);

        IntBuffer count = stack.ints(0);

        vkGetPhysicalDeviceSurfaceFormatsKHR(device, context.window.surface, count, null);

        if(count.get(0) != 0) {
            details.formats = VkSurfaceFormatKHR.malloc(count.get(0), stack);
            vkGetPhysicalDeviceSurfaceFormatsKHR(device, context.window.surface, count, details.formats);
        }

        vkGetPhysicalDeviceSurfacePresentModesKHR(device,context.window.surface, count, null);

        if(count.get(0) != 0) {
            details.presentModes = stack.mallocInt(count.get(0));
            vkGetPhysicalDeviceSurfacePresentModesKHR(device, context.window.surface, count, details.presentModes);
        }

        return details;
    }


    private int clamp(int min, int max, int value) {
        return Math.max(min, Math.min(max, value));
    }


    private int rateDevice(VkPhysicalDevice device) {
        try (MemoryStack stack = stackPush()) {
            int score = 0;

            VkPhysicalDeviceFeatures deviceFeatures = VkPhysicalDeviceFeatures.calloc(stack);
            vkGetPhysicalDeviceFeatures(device, deviceFeatures);

            if (!deviceFeatures.geometryShader()) {
                return 0;
            }

            VkPhysicalDeviceProperties deviceProps = VkPhysicalDeviceProperties.calloc(stack);
            vkGetPhysicalDeviceProperties(device, deviceProps);
            VkPhysicalDeviceMemoryProperties memProps = VkPhysicalDeviceMemoryProperties.calloc(stack);
            vkGetPhysicalDeviceMemoryProperties(device, memProps);

            if (deviceProps.deviceType() == VK_PHYSICAL_DEVICE_TYPE_DISCRETE_GPU) {
                score += 64000;
            }
            if (deviceProps.deviceType() == VK_PHYSICAL_DEVICE_TYPE_INTEGRATED_GPU) {
                score += 32000;
            }
            if (deviceProps.deviceType() == VK_PHYSICAL_DEVICE_TYPE_VIRTUAL_GPU) {
                score += 16000;
            }

            score += deviceProps.limits().maxImageDimension2D();
            score += deviceProps.limits().maxImageDimension3D();

            VkMemoryHeap.Buffer memBuffer = memProps.memoryHeaps();
            for (int i = 0; i < memBuffer.capacity(); i++) {
                score += memBuffer.get(i).size() / 1048576;
            }

            return score;
        }
    }

    private boolean isDeviceSuitable(VkPhysicalDevice device) {
        try (MemoryStack stack = stackPush()) {
            boolean swapchainExtFound = false;
            IntBuffer pCount = stack.callocInt(1);
            vkEnumerateDeviceExtensionProperties(device, (String)null, pCount, null);

            if (pCount.get(0) > 0) {
                VkExtensionProperties.Buffer device_extensions = VkExtensionProperties.malloc(pCount.get(0), stack);
                vkEnumerateDeviceExtensionProperties(device, (String)null, pCount, device_extensions);

                for (int i = 0; i < pCount.get(0); i++) {
                    device_extensions.position(i);
                    if (VK_KHR_SWAPCHAIN_EXTENSION_NAME.equals(device_extensions.extensionNameString())) {
                        swapchainExtFound = true;
                    }
                }
            }

            if (!swapchainExtFound) {
                return false;
            }
        }
        VulkanQueueFamilyIndices indices = findQueueFamilies(device);
        return indices.isComplete();
    }

    private VulkanQueueFamilyIndices findQueueFamilies(VkPhysicalDevice device) {
        VulkanQueueFamilyIndices indices = new VulkanQueueFamilyIndices();
        try(MemoryStack stack = stackPush()) {
            IntBuffer queueFamilyCount = stack.ints(0);
            vkGetPhysicalDeviceQueueFamilyProperties(device, queueFamilyCount, null);
            VkQueueFamilyProperties.Buffer queueFamilies = VkQueueFamilyProperties.malloc(queueFamilyCount.get(0), stack);
            vkGetPhysicalDeviceQueueFamilyProperties(device, queueFamilyCount, queueFamilies);
            IntBuffer presentSupport = stack.ints(VK_FALSE);
            for(int i = 0;i < queueFamilies.capacity() || !indices.isComplete();i++) {
                if((queueFamilies.get(i).queueFlags() & VK_QUEUE_GRAPHICS_BIT) != 0) {
                    indices.graphicsFamily = i;
                }
                vkGetPhysicalDeviceSurfaceSupportKHR(device, i, context.window.surface, presentSupport);
                if(presentSupport.get(0) == VK_TRUE) {
                    indices.presentFamily = i;
                }
            }
            return indices;
        }
    }
}
