package dev.mv.engine.render.vulkan;

import dev.mv.engine.MVEngine;
import dev.mv.engine.render.utils.RenderUtils;
import dev.mv.engine.render.vulkan.shader.SPIRV;
import dev.mv.engine.render.vulkan.shader.VulkanShader;
import dev.mv.utils.misc.Version;
import lombok.SneakyThrows;
import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.*;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.LongBuffer;
import java.util.*;
import java.util.stream.IntStream;

import static dev.mv.engine.render.utils.RenderUtils.asPointerBuffer;
import static dev.mv.engine.render.vulkan.VulkanSwapChain.SwapChainSupportDetails;
import static dev.mv.engine.render.vulkan.shader.SPIRV.ShaderKind.FRAGMENT_SHADER;
import static dev.mv.engine.render.vulkan.shader.SPIRV.ShaderKind.VERTEX_SHADER;
import static java.util.stream.Collectors.toSet;
import static org.lwjgl.glfw.GLFWVulkan.glfwGetRequiredInstanceExtensions;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.vulkan.KHRSurface.*;
import static org.lwjgl.vulkan.KHRSwapchain.*;
import static org.lwjgl.vulkan.VK10.*;


public class Vulkan {

    private static VkInstance instance;
    private static VkPhysicalDevice GPU;
    private static VkDevice logicalDevice;
    private static VkQueue graphicsQueue, presentQueue;
    private static boolean initialized = false;
    private static Set<String> DEVICE_EXTENSIONS;

    static {
        DEVICE_EXTENSIONS = new HashSet<String>();
        DEVICE_EXTENSIONS.add(VK_KHR_SWAPCHAIN_EXTENSION_NAME);
    }

    public static boolean init() {
        return init("", new Version(1));
    }

    public static boolean init(String name) {
        return init(name, new Version(1));
    }

    public static boolean init(Version version) {
        return init("", version);
    }

    public static boolean init(String name, Version version) {
        if (!_init(name, version)) {
            instance = null;
            GPU = null;
            logicalDevice = null;
            graphicsQueue = null;
            VulkanMemoryAllocator.allocator = VK_NULL_HANDLE;
            return false;
        }
        initialized = true;
        return true;
    }

    private static boolean _init(String name, Version version) {
        if (!createInstance(name, version)) return false;
        VulkanDebugger.setupDebugger(instance);
        if (!pickPhysicalDevices()) return false;
        if (!createLogicalDevice()) return false;
        if (!VulkanMemoryAllocator.init()) return false;
        return true;
    }

    private static boolean createInstance(String name, Version version) {
        VulkanDebugger.checks();
        try (MemoryStack stack = stackPush()) {
            VkApplicationInfo appInfo = VkApplicationInfo.calloc(stack);

            appInfo.sType(VK_STRUCTURE_TYPE_APPLICATION_INFO);
            appInfo.pApplicationName(RenderUtils.store(name));
            appInfo.applicationVersion(VK_MAKE_VERSION(version.getMajor(), version.getMinor(), version.getPatch()));
            appInfo.pEngineName(RenderUtils.store("MVEngine"));
            appInfo.engineVersion(VK_MAKE_VERSION(0, 1, 0));
            appInfo.apiVersion(getVulkanVersion());

            VkInstanceCreateInfo createInfo = VkInstanceCreateInfo.calloc(stack);

            createInfo.sType(VK_STRUCTURE_TYPE_INSTANCE_CREATE_INFO);
            createInfo.pApplicationInfo(appInfo);

            VulkanDebugger.init(stack, createInfo);

            if (System.getProperty("os.name").toLowerCase().contains("mac")) {
                createInfo.flags(createInfo.flags() | 0x00000001); //VK_INSTANCE_CREATE_ENUMERATE_PORTABILITY_BIT_KHR
            }

            PointerBuffer instancePtr = stack.mallocPointer(1);

            if (vkCreateInstance(createInfo, null, instancePtr) != VK_SUCCESS) {
                return false;
            }

            instance = new VkInstance(instancePtr.get(0), createInfo);
        }
        return true;
    }

    private static boolean pickPhysicalDevices() {
        try (MemoryStack stack = stackPush()) {
            IntBuffer deviceCount = stack.ints(0);
            vkEnumeratePhysicalDevices(instance, deviceCount, null);

            if (deviceCount.get(0) == 0) {
                return false;
            }

            PointerBuffer devices = stack.mallocPointer(deviceCount.get(0));
            vkEnumeratePhysicalDevices(instance, deviceCount, devices);

            List<VkPhysicalDevice> possibleDevices = new ArrayList<>();

            for (int i = 0; i < devices.capacity(); i++) {
                VkPhysicalDevice device = new VkPhysicalDevice(devices.get(i), instance);
                if (isDeviceSuitable(device)) {
                    possibleDevices.add(device);
                }
            }

            if (possibleDevices.size() == 0) {
                return false;
            }

            int highestScore = 0;

            for (VkPhysicalDevice device : possibleDevices) {
                int score = rateDevice(device);
                if (score > highestScore) {
                    highestScore = score;
                    GPU = device;
                }
            }

            if (highestScore == 0) {
                GPU = null;
                return false;
            }
        }
        return true;
    }

    private static boolean createLogicalDevice() {
        try (MemoryStack stack = stackPush()) {
            Integer indices = findQueueFamilies(GPU, VK_QUEUE_GRAPHICS_BIT);

            VkDeviceQueueCreateInfo.Buffer queueCreateInfos = VkDeviceQueueCreateInfo.calloc(1, stack);

            queueCreateInfos.sType(VK_STRUCTURE_TYPE_DEVICE_QUEUE_CREATE_INFO);
            queueCreateInfos.queueFamilyIndex(indices);
            queueCreateInfos.pQueuePriorities(stack.floats(1.0f));

            VkPhysicalDeviceFeatures deviceFeatures = VkPhysicalDeviceFeatures.calloc(stack);

            VkDeviceCreateInfo createInfo = VkDeviceCreateInfo.calloc(stack);

            createInfo.sType(VK_STRUCTURE_TYPE_DEVICE_CREATE_INFO);
            createInfo.pQueueCreateInfos(queueCreateInfos);

            createInfo.pEnabledFeatures(deviceFeatures);

            createInfo.ppEnabledExtensionNames(asPointerBuffer(DEVICE_EXTENSIONS));

            VulkanDebugger.init(createInfo);

            PointerBuffer pDevice = stack.pointers(VK_NULL_HANDLE);

            if (vkCreateDevice(GPU, createInfo, null, pDevice) != VK_SUCCESS) {
                return false;
            }

            logicalDevice = new VkDevice(pDevice.get(0), GPU, createInfo);

            PointerBuffer pGraphicsQueue = stack.pointers(VK_NULL_HANDLE);

            vkGetDeviceQueue(logicalDevice, indices, 0, pGraphicsQueue);

            graphicsQueue = new VkQueue(pGraphicsQueue.get(0), logicalDevice);
        }
        return true;
    }

    private static boolean isDeviceSuitable(VkPhysicalDevice device) {
        if (!hasQueueSupport(device, VK_QUEUE_GRAPHICS_BIT)) return false;
        if (!checkExtensionSupport(device)) return false;
        return true;
    }

    private static boolean checkExtensionSupport(VkPhysicalDevice device) {
        try (MemoryStack stack = stackPush()) {
            IntBuffer extensionCount = stack.ints(0);
            vkEnumerateDeviceExtensionProperties(device, (String) null, extensionCount, null);

            VkExtensionProperties.Buffer availableExtensions = VkExtensionProperties.malloc(extensionCount.get(0), stack);
            vkEnumerateDeviceExtensionProperties(device, (String) null, extensionCount, availableExtensions);

            return availableExtensions.stream()
                .map(VkExtensionProperties::extensionNameString)
                .collect(toSet())
                .containsAll(DEVICE_EXTENSIONS);
        }
    }

    private static int rateDevice(VkPhysicalDevice device) {
        int score = 0;
        try (MemoryStack stack = stackPush()) {
            VkPhysicalDeviceProperties deviceProperties = VkPhysicalDeviceProperties.calloc(stack);
            VkPhysicalDeviceFeatures deviceFeatures = VkPhysicalDeviceFeatures.calloc(stack);
            VkPhysicalDeviceMemoryProperties deviceMemoryProperties = VkPhysicalDeviceMemoryProperties.calloc(stack);
            vkGetPhysicalDeviceProperties(device, deviceProperties);
            vkGetPhysicalDeviceFeatures(device, deviceFeatures);
            vkGetPhysicalDeviceMemoryProperties(device, deviceMemoryProperties);

            if (deviceProperties.deviceType() == VK_PHYSICAL_DEVICE_TYPE_DISCRETE_GPU) {
                score += 10000;
            } else if (deviceProperties.deviceType() == VK_PHYSICAL_DEVICE_TYPE_INTEGRATED_GPU) {
                score += 2500;
            } else if (deviceProperties.deviceType() == VK_PHYSICAL_DEVICE_TYPE_VIRTUAL_GPU) {
                score += 500;
            }

            score += deviceProperties.limits().maxImageDimension2D();

            long memory = 0;

            for (int i = 0; i < deviceMemoryProperties.memoryHeapCount(); i++) {
                memory += deviceMemoryProperties.memoryHeaps(i).size();
            }

            memory /= 1048576;

            if (!deviceFeatures.geometryShader()) {
                score = 0;
            }

            if (deviceProperties.deviceType() == VK_PHYSICAL_DEVICE_TYPE_CPU || deviceProperties.deviceType() == VK_PHYSICAL_DEVICE_TYPE_OTHER) {
                score /= 4;
            }
        }
        return score;
    }

    private static boolean hasQueueSupport(VkPhysicalDevice device, int queueBit) {
        return findQueueFamilies(device, queueBit) != null;
    }

    private static Integer findQueueFamilies(VkPhysicalDevice device, int queueBit) {
        try (MemoryStack stack = stackPush()) {
            IntBuffer queueFamilyCount = stack.ints(0);
            vkGetPhysicalDeviceQueueFamilyProperties(device, queueFamilyCount, null);

            VkQueueFamilyProperties.Buffer queueFamilies = VkQueueFamilyProperties.malloc(queueFamilyCount.get(0), stack);
            vkGetPhysicalDeviceQueueFamilyProperties(device, queueFamilyCount, queueFamilies);

            IntBuffer presentSupport = stack.ints(VK_FALSE);

            OptionalInt optionalIndex = IntStream.range(0, queueFamilies.capacity()).filter(index -> (queueFamilies.get(index).queueFlags() & queueBit) != 0).findFirst();
            return optionalIndex.isPresent() ? optionalIndex.getAsInt() : null;
        }
    }

    private static Integer getPresentFamily(long surface) {
        try (MemoryStack stack = stackPush()) {
            Integer presentFamily = null;
            IntBuffer queueFamilyCount = stack.ints(0);
            vkGetPhysicalDeviceQueueFamilyProperties(GPU, queueFamilyCount, null);

            VkQueueFamilyProperties.Buffer queueFamilies = VkQueueFamilyProperties.malloc(queueFamilyCount.get(0), stack);
            vkGetPhysicalDeviceQueueFamilyProperties(GPU, queueFamilyCount, queueFamilies);

            IntBuffer presentSupport = stack.ints(VK_FALSE);

            for (int i = 0; i < queueFamilies.capacity() && presentFamily == null; i++) {
                vkGetPhysicalDeviceSurfaceSupportKHR(GPU, i, surface, presentSupport);

                if (presentSupport.get(0) == VK_TRUE) {
                    presentFamily = i;
                }
            }
            return presentFamily;
        }
    }

    static boolean setupSurfaceSupport(long surface) {
        try (MemoryStack stack = stackPush()) {
            Integer presentFamily = getPresentFamily(surface);
            if (presentFamily == null) {
                return false;
            }

            PointerBuffer pQueue = stack.pointers(VK_NULL_HANDLE);

            vkGetDeviceQueue(logicalDevice, presentFamily, 0, pQueue);
            presentQueue = new VkQueue(pQueue.get(0), logicalDevice);
        }
        return true;
    }

    static PointerBuffer getRequiredExtensions() {
        if (System.getProperty("os.name").toLowerCase().contains("mac")) {
            PointerBuffer extensions = glfwGetRequiredInstanceExtensions();
            PointerBuffer fullExtensions = PointerBuffer.allocateDirect(extensions.capacity() + 1);
            for (int i = 0; i < extensions.capacity(); i++) {
                fullExtensions.put(i, extensions.get(i));
            }
            fullExtensions.put(extensions.capacity(), RenderUtils.store("VK_KHR_PORTABILITY_ENUMERATION_EXTENSION_NAME"));
            return fullExtensions.rewind();
        } else {
            return glfwGetRequiredInstanceExtensions();
        }
    }

    static VulkanSwapChain createSwapChain(long surface, int width, int height, boolean vsync) {
        try (MemoryStack stack = stackPush()) {
            SwapChainSupportDetails swapChainSupport = querySwapChainSupport(GPU, stack, surface);

            VulkanSwapChain swapChain = new VulkanSwapChain();

            VkSurfaceFormatKHR surfaceFormat = chooseSwapSurfaceFormat(swapChainSupport.formats);
            int presentMode = chooseSwapPresentMode(swapChainSupport.presentModes, vsync);
            VkExtent2D extent = chooseSwapExtent(swapChainSupport.capabilities, width, height);

            IntBuffer imageCount = stack.ints(swapChainSupport.capabilities.minImageCount() + 1);

            if (swapChainSupport.capabilities.maxImageCount() > 0 && imageCount.get(0) > swapChainSupport.capabilities.maxImageCount()) {
                imageCount.put(0, swapChainSupport.capabilities.maxImageCount());
            }

            VkSwapchainCreateInfoKHR createInfo = VkSwapchainCreateInfoKHR.calloc(stack);

            createInfo.sType(VK_STRUCTURE_TYPE_SWAPCHAIN_CREATE_INFO_KHR);
            createInfo.surface(surface);

            createInfo.minImageCount(imageCount.get(0));
            createInfo.imageFormat(surfaceFormat.format());
            createInfo.imageColorSpace(surfaceFormat.colorSpace());
            createInfo.imageExtent(extent);
            createInfo.imageArrayLayers(1);
            createInfo.imageUsage(VK_IMAGE_USAGE_COLOR_ATTACHMENT_BIT);

            Integer graphicsFamily = findQueueFamilies(GPU, VK_QUEUE_GRAPHICS_BIT);
            Integer presentFamily = getPresentFamily(surface);

            if (!graphicsFamily.equals(presentFamily)) {
                createInfo.imageSharingMode(VK_SHARING_MODE_CONCURRENT);
                createInfo.pQueueFamilyIndices(stack.ints(graphicsFamily, presentFamily));
            } else {
                createInfo.imageSharingMode(VK_SHARING_MODE_EXCLUSIVE);
            }

            createInfo.preTransform(swapChainSupport.capabilities.currentTransform());
            createInfo.compositeAlpha(VK_COMPOSITE_ALPHA_OPAQUE_BIT_KHR);
            createInfo.presentMode(presentMode);
            createInfo.clipped(true);

            createInfo.oldSwapchain(VK_NULL_HANDLE);

            LongBuffer pSwapChain = stack.longs(VK_NULL_HANDLE);

            if (vkCreateSwapchainKHR(logicalDevice, createInfo, null, pSwapChain) != VK_SUCCESS) {
                return null;
            }

            swapChain.id = pSwapChain.get(0);

            vkGetSwapchainImagesKHR(logicalDevice, swapChain.id, imageCount, null);

            LongBuffer pSwapChainImages = stack.mallocLong(imageCount.get(0));

            vkGetSwapchainImagesKHR(logicalDevice, swapChain.id, imageCount, pSwapChainImages);

            swapChain.images = new ArrayList<>(imageCount.get(0));

            for (int i = 0; i < pSwapChainImages.capacity(); i++) {
                swapChain.images.add(pSwapChainImages.get(i));
            }

            swapChain.imageFormat = surfaceFormat.format();
            swapChain.extent = VkExtent2D.create().set(extent);
            return swapChain;
        }
    }

    private static VkSurfaceFormatKHR chooseSwapSurfaceFormat(VkSurfaceFormatKHR.Buffer availableFormats) {
        return availableFormats.stream()
            .filter(availableFormat -> availableFormat.format() == VK_FORMAT_B8G8R8_UNORM)
            .filter(availableFormat -> availableFormat.colorSpace() == VK_COLOR_SPACE_SRGB_NONLINEAR_KHR)
            .findAny()
            .orElse(availableFormats.get(0));
    }

    private static int chooseSwapPresentMode(IntBuffer availablePresentModes, boolean vsync) {
        if (!vsync) {
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

    private static VkExtent2D chooseSwapExtent(VkSurfaceCapabilitiesKHR capabilities, int width, int height) {

        if (capabilities.currentExtent().width() != 0xFFFFFFFF) {
            return capabilities.currentExtent();
        }

        VkExtent2D actualExtent = VkExtent2D.malloc().set(width, height);

        VkExtent2D minExtent = capabilities.minImageExtent();
        VkExtent2D maxExtent = capabilities.maxImageExtent();

        actualExtent.width(clamp(minExtent.width(), maxExtent.width(), actualExtent.width()));
        actualExtent.height(clamp(minExtent.height(), maxExtent.height(), actualExtent.height()));

        return actualExtent;
    }

    private static SwapChainSupportDetails querySwapChainSupport(VkPhysicalDevice device, MemoryStack stack, long surface) {
        SwapChainSupportDetails details = new SwapChainSupportDetails();

        details.capabilities = VkSurfaceCapabilitiesKHR.malloc(stack);
        vkGetPhysicalDeviceSurfaceCapabilitiesKHR(device, surface, details.capabilities);

        IntBuffer count = stack.ints(0);
        vkGetPhysicalDeviceSurfaceFormatsKHR(device, surface, count, null);

        if (count.get(0) != 0) {
            details.formats = VkSurfaceFormatKHR.malloc(count.get(0), stack);
            vkGetPhysicalDeviceSurfaceFormatsKHR(device, surface, count, details.formats);
        }

        vkGetPhysicalDeviceSurfacePresentModesKHR(device, surface, count, null);

        if (count.get(0) != 0) {
            details.presentModes = stack.mallocInt(count.get(0));
            vkGetPhysicalDeviceSurfacePresentModesKHR(device, surface, count, details.presentModes);
        }

        return details;
    }

    static boolean createImageViews(VulkanSwapChain swapChain) {
        try (MemoryStack stack = stackPush()) {
            LongBuffer pImageView = stack.mallocLong(1);

            for (long swapChainImage : swapChain.images) {
                VkImageViewCreateInfo createInfo = VkImageViewCreateInfo.calloc(stack);

                createInfo.sType(VK_STRUCTURE_TYPE_IMAGE_VIEW_CREATE_INFO);
                createInfo.image(swapChainImage);
                createInfo.viewType(VK_IMAGE_VIEW_TYPE_2D);
                createInfo.format(swapChain.imageFormat);

                createInfo.components().r(VK_COMPONENT_SWIZZLE_IDENTITY);
                createInfo.components().g(VK_COMPONENT_SWIZZLE_IDENTITY);
                createInfo.components().b(VK_COMPONENT_SWIZZLE_IDENTITY);
                createInfo.components().a(VK_COMPONENT_SWIZZLE_IDENTITY);

                createInfo.subresourceRange().aspectMask(VK_IMAGE_ASPECT_COLOR_BIT);
                createInfo.subresourceRange().baseMipLevel(0);
                createInfo.subresourceRange().levelCount(1);
                createInfo.subresourceRange().baseArrayLayer(0);
                createInfo.subresourceRange().layerCount(1);

                if (vkCreateImageView(logicalDevice, createInfo, null, pImageView) != VK_SUCCESS) {
                    return false;
                }

                swapChain.imageViews.add(pImageView.get(0));
            }
        }
        return true;
    }

    private static int clamp(int min, int max, int value) {
        return Math.max(min, Math.min(max, value));
    }

    private static long createRenderPass(VulkanSwapChain swapChain) {
        try (MemoryStack stack = stackPush()) {
            VkAttachmentDescription.Buffer colorAttachment = VkAttachmentDescription.calloc(1, stack);
            colorAttachment.format(swapChain.imageFormat);
            colorAttachment.samples(VK_SAMPLE_COUNT_1_BIT);
            colorAttachment.loadOp(VK_ATTACHMENT_LOAD_OP_CLEAR);
            colorAttachment.storeOp(VK_ATTACHMENT_STORE_OP_STORE);
            colorAttachment.stencilLoadOp(VK_ATTACHMENT_LOAD_OP_DONT_CARE);
            colorAttachment.stencilStoreOp(VK_ATTACHMENT_STORE_OP_DONT_CARE);
            colorAttachment.initialLayout(VK_IMAGE_LAYOUT_UNDEFINED);
            colorAttachment.finalLayout(VK_IMAGE_LAYOUT_PRESENT_SRC_KHR);

            VkAttachmentReference.Buffer colorAttachmentRef = VkAttachmentReference.calloc(1, stack);
            colorAttachmentRef.attachment(0);
            colorAttachmentRef.layout(VK_IMAGE_LAYOUT_COLOR_ATTACHMENT_OPTIMAL);

            VkSubpassDescription.Buffer subpass = VkSubpassDescription.calloc(1, stack);
            subpass.pipelineBindPoint(VK_PIPELINE_BIND_POINT_GRAPHICS);
            subpass.colorAttachmentCount(1);
            subpass.pColorAttachments(colorAttachmentRef);

            VkSubpassDependency.Buffer dependency = VkSubpassDependency.calloc(1, stack);
            dependency.srcSubpass(VK_SUBPASS_EXTERNAL);
            dependency.dstSubpass(0);
            dependency.srcStageMask(VK_PIPELINE_STAGE_COLOR_ATTACHMENT_OUTPUT_BIT);
            dependency.srcAccessMask(0);
            dependency.dstStageMask(VK_PIPELINE_STAGE_COLOR_ATTACHMENT_OUTPUT_BIT);
            dependency.dstAccessMask(VK_ACCESS_COLOR_ATTACHMENT_READ_BIT | VK_ACCESS_COLOR_ATTACHMENT_WRITE_BIT);

            VkRenderPassCreateInfo renderPassInfo = VkRenderPassCreateInfo.calloc(stack);
            renderPassInfo.sType(VK_STRUCTURE_TYPE_RENDER_PASS_CREATE_INFO);
            renderPassInfo.pAttachments(colorAttachment);
            renderPassInfo.pSubpasses(subpass);
            renderPassInfo.pDependencies(dependency);

            LongBuffer pRenderPass = stack.mallocLong(1);

            if (vkCreateRenderPass(logicalDevice, renderPassInfo, null, pRenderPass) != VK_SUCCESS) {
                return -1;
            }

            return pRenderPass.get(0);
        }
    }

    static VulkanGraphicsPipeline createGraphicsPipeline(VulkanSwapChain swapChain, String defaultVertShaderPath, String defaultFragShaderPath) {
        try {
            VulkanShader shader = new VulkanShader(defaultVertShaderPath, defaultFragShaderPath);
            return createGraphicsPipeline(swapChain, shader);
        } catch(IOException e) {
            throw new RuntimeException(e);
        }
    }

    static VulkanGraphicsPipeline createGraphicsPipeline(VulkanSwapChain swapChain, VulkanShader shader) {
        try (MemoryStack stack = stackPush()) {
            long renderPass = createRenderPass(swapChain);

            if (renderPass == -1) {
                return null;
            }

            shader.compile();

            long vertShaderModule = createShaderModule(shader.getVertexBytecode());
            long fragShaderModule = createShaderModule(shader.getFragmentBytecode());

            if (vertShaderModule == -1 || fragShaderModule == -1) {
                return null;
            }

            ByteBuffer entryPoint = stack.UTF8("main");

            VkPipelineShaderStageCreateInfo.Buffer shaderStages = VkPipelineShaderStageCreateInfo.calloc(2, stack);

            VkPipelineShaderStageCreateInfo vertShaderStageInfo = shaderStages.get(0);

            vertShaderStageInfo.sType(VK_STRUCTURE_TYPE_PIPELINE_SHADER_STAGE_CREATE_INFO);
            vertShaderStageInfo.stage(VK_SHADER_STAGE_VERTEX_BIT);
            vertShaderStageInfo.module(vertShaderModule);
            vertShaderStageInfo.pName(entryPoint);

            VkPipelineShaderStageCreateInfo fragShaderStageInfo = shaderStages.get(1);

            fragShaderStageInfo.sType(VK_STRUCTURE_TYPE_PIPELINE_SHADER_STAGE_CREATE_INFO);
            fragShaderStageInfo.stage(VK_SHADER_STAGE_FRAGMENT_BIT);
            fragShaderStageInfo.module(fragShaderModule);
            fragShaderStageInfo.pName(entryPoint);

            VkPipelineVertexInputStateCreateInfo vertexInputInfo = VkPipelineVertexInputStateCreateInfo.calloc(stack);
            vertexInputInfo.sType(VK_STRUCTURE_TYPE_PIPELINE_VERTEX_INPUT_STATE_CREATE_INFO);
            vertexInputInfo.pVertexBindingDescriptions(shader.getBindingDescription());
            vertexInputInfo.pVertexAttributeDescriptions(shader.getAttributeDescription());

            VkPipelineInputAssemblyStateCreateInfo inputAssembly = VkPipelineInputAssemblyStateCreateInfo.calloc(stack);
            inputAssembly.sType(VK_STRUCTURE_TYPE_PIPELINE_INPUT_ASSEMBLY_STATE_CREATE_INFO);
            inputAssembly.topology(VK_PRIMITIVE_TOPOLOGY_TRIANGLE_LIST);
            inputAssembly.primitiveRestartEnable(false);

            VkViewport.Buffer viewport = VkViewport.calloc(1, stack);
            viewport.x(0.0f);
            viewport.y(0.0f);
            viewport.width(swapChain.extent.width());
            viewport.height(swapChain.extent.height());
            viewport.minDepth(0.0f);
            viewport.maxDepth(1.0f);

            VkRect2D.Buffer scissor = VkRect2D.calloc(1, stack);
            scissor.offset(VkOffset2D.calloc(stack).set(0, 0));
            scissor.extent(swapChain.extent);

            VkPipelineViewportStateCreateInfo viewportState = VkPipelineViewportStateCreateInfo.calloc(stack);
            viewportState.sType(VK_STRUCTURE_TYPE_PIPELINE_VIEWPORT_STATE_CREATE_INFO);
            viewportState.pViewports(viewport);
            viewportState.pScissors(scissor);

            VkPipelineRasterizationStateCreateInfo rasterizer = VkPipelineRasterizationStateCreateInfo.calloc(stack);
            rasterizer.sType(VK_STRUCTURE_TYPE_PIPELINE_RASTERIZATION_STATE_CREATE_INFO);
            rasterizer.depthClampEnable(false);
            rasterizer.rasterizerDiscardEnable(false);
            rasterizer.polygonMode(VK_POLYGON_MODE_FILL);
            rasterizer.lineWidth(1.0f);
            rasterizer.cullMode(VK_CULL_MODE_BACK_BIT);
            rasterizer.frontFace(VK_FRONT_FACE_CLOCKWISE);
            rasterizer.depthBiasEnable(false);

            VkPipelineMultisampleStateCreateInfo multisampling = VkPipelineMultisampleStateCreateInfo.calloc(stack);
            multisampling.sType(VK_STRUCTURE_TYPE_PIPELINE_MULTISAMPLE_STATE_CREATE_INFO);
            multisampling.sampleShadingEnable(false);
            multisampling.rasterizationSamples(VK_SAMPLE_COUNT_1_BIT);

            VkPipelineColorBlendAttachmentState.Buffer colorBlendAttachment = VkPipelineColorBlendAttachmentState.calloc(1, stack);
            colorBlendAttachment.colorWriteMask(VK_COLOR_COMPONENT_R_BIT | VK_COLOR_COMPONENT_G_BIT | VK_COLOR_COMPONENT_B_BIT | VK_COLOR_COMPONENT_A_BIT);
            colorBlendAttachment.blendEnable(false);

            VkPipelineColorBlendStateCreateInfo colorBlending = VkPipelineColorBlendStateCreateInfo.calloc(stack);
            colorBlending.sType(VK_STRUCTURE_TYPE_PIPELINE_COLOR_BLEND_STATE_CREATE_INFO);
            colorBlending.logicOpEnable(false);
            colorBlending.logicOp(VK_LOGIC_OP_COPY);
            colorBlending.pAttachments(colorBlendAttachment);
            colorBlending.blendConstants(stack.floats(0.0f, 0.0f, 0.0f, 0.0f));

            VkPipelineLayoutCreateInfo pipelineLayoutInfo = VkPipelineLayoutCreateInfo.calloc(stack);
            pipelineLayoutInfo.sType(VK_STRUCTURE_TYPE_PIPELINE_LAYOUT_CREATE_INFO);

            LongBuffer pPipelineLayout = stack.longs(VK_NULL_HANDLE);

            if (vkCreatePipelineLayout(logicalDevice, pipelineLayoutInfo, null, pPipelineLayout) != VK_SUCCESS) {
                return null;
            }

            long pipelineLayout = pPipelineLayout.get(0);

            VkGraphicsPipelineCreateInfo.Buffer pipelineInfo = VkGraphicsPipelineCreateInfo.calloc(1, stack);
            pipelineInfo.sType(VK_STRUCTURE_TYPE_GRAPHICS_PIPELINE_CREATE_INFO);
            pipelineInfo.pStages(shaderStages);
            pipelineInfo.pVertexInputState(vertexInputInfo);
            pipelineInfo.pInputAssemblyState(inputAssembly);
            pipelineInfo.pViewportState(viewportState);
            pipelineInfo.pRasterizationState(rasterizer);
            pipelineInfo.pMultisampleState(multisampling);
            pipelineInfo.pColorBlendState(colorBlending);
            pipelineInfo.layout(pipelineLayout);
            pipelineInfo.renderPass(renderPass);
            pipelineInfo.subpass(0);
            pipelineInfo.basePipelineHandle(VK_NULL_HANDLE);
            pipelineInfo.basePipelineIndex(-1);

            LongBuffer pGraphicsPipeline = stack.mallocLong(1);

            if (vkCreateGraphicsPipelines(logicalDevice, VK_NULL_HANDLE, pipelineInfo, null, pGraphicsPipeline) != VK_SUCCESS) {
                throw new RuntimeException("Failed to create graphics pipeline");
            }

            long pipelineId = pGraphicsPipeline.get(0);

            vkDestroyShaderModule(logicalDevice, vertShaderModule, null);
            vkDestroyShaderModule(logicalDevice, fragShaderModule, null);

            shader.free();

            VulkanGraphicsPipeline pipeline = new VulkanGraphicsPipeline();
            pipeline.id = pipelineId;
            pipeline.layout = pipelineLayout;
            pipeline.renderPass = renderPass;
            pipeline.shader = shader;
            return pipeline;
        }
    }

    private static long createShaderModule(ByteBuffer bytecode) {
        try (MemoryStack stack = stackPush()) {
            VkShaderModuleCreateInfo createInfo = VkShaderModuleCreateInfo.calloc(stack);

            createInfo.sType(VK_STRUCTURE_TYPE_SHADER_MODULE_CREATE_INFO);
            createInfo.pCode(bytecode);

            LongBuffer pShaderModule = stack.mallocLong(1);

            if (vkCreateShaderModule(logicalDevice, createInfo, null, pShaderModule) != VK_SUCCESS) {
                return -1;
            }
            return pShaderModule.get(0);
        }
    }

    static boolean createFramebuffers(VulkanSwapChain swapChain, VulkanGraphicsPipeline graphicsPipeline) {
        try (MemoryStack stack = stackPush()) {
            LongBuffer attachments = stack.mallocLong(1);
            LongBuffer pFramebuffer = stack.mallocLong(1);

            VkFramebufferCreateInfo framebufferInfo = VkFramebufferCreateInfo.calloc(stack);
            framebufferInfo.sType(VK_STRUCTURE_TYPE_FRAMEBUFFER_CREATE_INFO);
            framebufferInfo.renderPass(graphicsPipeline.renderPass);
            framebufferInfo.width(swapChain.extent.width());
            framebufferInfo.height(swapChain.extent.height());
            framebufferInfo.layers(1);

            for (long imageView : swapChain.imageViews) {
                attachments.put(0, imageView);
                framebufferInfo.pAttachments(attachments);

                if (vkCreateFramebuffer(logicalDevice, framebufferInfo, null, pFramebuffer) != VK_SUCCESS) {
                    return false;
                }

                swapChain.framebuffers.add(pFramebuffer.get(0));
            }
        }
        return true;
    }

    static VulkanCommandPool createCommandPool() {
        try (MemoryStack stack = stackPush()) {
            VkCommandPoolCreateInfo poolInfo = VkCommandPoolCreateInfo.calloc(stack);
            poolInfo.sType(VK_STRUCTURE_TYPE_COMMAND_POOL_CREATE_INFO);
            poolInfo.queueFamilyIndex(findQueueFamilies(GPU, VK_QUEUE_GRAPHICS_BIT));

            LongBuffer pCommandPool = stack.mallocLong(1);

            if (vkCreateCommandPool(logicalDevice, poolInfo, null, pCommandPool) != VK_SUCCESS) {
                return null;
            }

            VulkanCommandPool commandPool = new VulkanCommandPool();
            commandPool.id = pCommandPool.get(0);
            return commandPool;
        }
    }

    static boolean createCommandBuffers(VulkanCommandPool commandPool, VulkanSwapChain swapChain, VulkanGraphicsPipeline graphicsPipeline) {
        final int commandBuffersCount = swapChain.framebuffers.size();
        commandPool.buffers = new ArrayList<>();
        try (MemoryStack stack = stackPush()) {
            VkCommandBufferAllocateInfo allocInfo = VkCommandBufferAllocateInfo.calloc(stack);
            allocInfo.sType(VK_STRUCTURE_TYPE_COMMAND_BUFFER_ALLOCATE_INFO);
            allocInfo.commandPool(commandPool.id);
            allocInfo.level(VK_COMMAND_BUFFER_LEVEL_PRIMARY);
            allocInfo.commandBufferCount(commandBuffersCount);

            PointerBuffer pCommandBuffers = stack.mallocPointer(commandBuffersCount);

            if (vkAllocateCommandBuffers(logicalDevice, allocInfo, pCommandBuffers) != VK_SUCCESS) {
                return false;
            }

            for (int i = 0; i < commandBuffersCount; i++) {
                commandPool.buffers.add(new VkCommandBuffer(pCommandBuffers.get(i), logicalDevice));
            }

            VkCommandBufferBeginInfo beginInfo = VkCommandBufferBeginInfo.calloc(stack);
            beginInfo.sType(VK_STRUCTURE_TYPE_COMMAND_BUFFER_BEGIN_INFO);

            VkRenderPassBeginInfo renderPassInfo = VkRenderPassBeginInfo.calloc(stack);
            renderPassInfo.sType(VK_STRUCTURE_TYPE_RENDER_PASS_BEGIN_INFO);
            renderPassInfo.renderPass(graphicsPipeline.renderPass);
            VkRect2D renderArea = VkRect2D.calloc(stack);
            renderArea.offset(VkOffset2D.calloc(stack).set(0, 0));
            renderArea.extent(swapChain.extent);
            renderPassInfo.renderArea(renderArea);
            VkClearValue.Buffer clearValues = VkClearValue.calloc(1, stack);
            clearValues.color().float32(stack.floats(0.0f, 0.0f, 0.0f, 1.0f));
            renderPassInfo.pClearValues(clearValues);

            for (int i = 0; i < commandBuffersCount; i++) {
                VkCommandBuffer commandBuffer = commandPool.buffers.get(i);
                if (vkBeginCommandBuffer(commandBuffer, beginInfo) != VK_SUCCESS) {
                    return false;
                }

                renderPassInfo.framebuffer(swapChain.framebuffers.get(i));

                vkCmdBeginRenderPass(commandBuffer, renderPassInfo, VK_SUBPASS_CONTENTS_INLINE);
                {
                    vkCmdBindPipeline(commandBuffer, VK_PIPELINE_BIND_POINT_GRAPHICS, graphicsPipeline.id);
                }
                vkCmdEndRenderPass(commandBuffer);

                if (vkEndCommandBuffer(commandBuffer) != VK_SUCCESS) {
                    return false;
                }
            }
        }
        return true;
    }

    static VulkanRender createRender() {
        VulkanRender render = new VulkanRender();
        try (MemoryStack stack = stackPush()) {

            VkSemaphoreCreateInfo semaphoreInfo = VkSemaphoreCreateInfo.calloc(stack);
            semaphoreInfo.sType(VK_STRUCTURE_TYPE_SEMAPHORE_CREATE_INFO);

            VkFenceCreateInfo fenceInfo = VkFenceCreateInfo.calloc(stack);
            fenceInfo.sType(VK_STRUCTURE_TYPE_FENCE_CREATE_INFO);
            fenceInfo.flags(VK_FENCE_CREATE_SIGNALED_BIT);

            LongBuffer pImageAvailableSemaphore = stack.mallocLong(1);
            LongBuffer pRenderFinishedSemaphore = stack.mallocLong(1);
            LongBuffer pFence = stack.mallocLong(1);

            for (int i = 0; i < render.MAX_FRAMES_IN_FLIGHT; i++) {

                if (vkCreateSemaphore(logicalDevice, semaphoreInfo, null, pImageAvailableSemaphore) != VK_SUCCESS
                    || vkCreateSemaphore(logicalDevice, semaphoreInfo, null, pRenderFinishedSemaphore) != VK_SUCCESS
                    || vkCreateFence(logicalDevice, fenceInfo, null, pFence) != VK_SUCCESS) {

                    return null;
                }

                render.inFlightFrames.add(new Frame(pImageAvailableSemaphore.get(0), pRenderFinishedSemaphore.get(0), pFence.get(0)));
            }
        }
        return render;
    }

    public static void terminate() {
        vkDestroyDevice(logicalDevice, null);
        VulkanDebugger.destroyDebugUtilsMessengerEXT(instance, null);
        vkDestroyInstance(instance, null);

    }

    static void check() {
        if (!MVEngine.usesVulkan()) {
            throw new IllegalStateException("Vulkan support is not enabled!");
        }
        if (!initialized) {
            throw new IllegalStateException("Vulkan is not initialized");
        }
    }

    public static VkInstance getInstance() {
        check();
        return instance;
    }

    public static VkPhysicalDevice getGPU() {
        check();
        return GPU;
    }

    public static VkDevice getLogicalDevice() {
        check();
        return logicalDevice;
    }

    public static VkQueue getGraphicsQueue() {
        check();
        return graphicsQueue;
    }

    public static VkQueue getPresentQueue() {
        check();
        return presentQueue;
    }

    public static int getVulkanVersion() {
        return VK_MAKE_API_VERSION(0, 1, 2, 198);
    }
}
