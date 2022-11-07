package dev.mv.engine.render.vulkan;

import dev.mv.engine.render.utils.RenderUtils;
import dev.mv.utils.misc.Version;
import lombok.Getter;
import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.*;

import java.nio.IntBuffer;
import java.nio.LongBuffer;
import java.util.*;
import java.util.stream.IntStream;

import static dev.mv.engine.render.vulkan.VulkanSwapChain.*;
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
            appInfo.apiVersion(VK_API_VERSION_1_0);

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
            // queueCreateInfoCount is automatically set

            createInfo.pEnabledFeatures(deviceFeatures);

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
            vkEnumerateDeviceExtensionProperties(device, (String)null, extensionCount, null);

            VkExtensionProperties.Buffer availableExtensions = VkExtensionProperties.malloc(extensionCount.get(0), stack);
            vkEnumerateDeviceExtensionProperties(device, (String)null, extensionCount, availableExtensions);

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
            vkGetPhysicalDeviceProperties(device, deviceProperties);
            vkGetPhysicalDeviceFeatures(device, deviceFeatures);

            if (deviceProperties.deviceType() == VK_PHYSICAL_DEVICE_TYPE_DISCRETE_GPU) {
                score += 10000;
            } else if (deviceProperties.deviceType() == VK_PHYSICAL_DEVICE_TYPE_INTEGRATED_GPU) {
                score += 1000;
            } else if (deviceProperties.deviceType() == VK_PHYSICAL_DEVICE_TYPE_VIRTUAL_GPU) {
                score += 500;
            }

            score += deviceProperties.limits().maxImageDimension2D();

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
        try(MemoryStack stack = stackPush()) {
            IntBuffer queueFamilyCount = stack.ints(0);
            vkGetPhysicalDeviceQueueFamilyProperties(device, queueFamilyCount, null);

            VkQueueFamilyProperties.Buffer queueFamilies = VkQueueFamilyProperties.malloc(queueFamilyCount.get(0), stack);
            vkGetPhysicalDeviceQueueFamilyProperties(device, queueFamilyCount, queueFamilies);

            IntBuffer presentSupport = stack.ints(VK_FALSE);

            OptionalInt optionalIndex = IntStream.range(0, queueFamilies.capacity()).filter(index -> (queueFamilies.get(index).queueFlags() & queueBit) != 0).findFirst();
            return optionalIndex.isPresent()? optionalIndex.getAsInt() : null;
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

            for (int i = 0;i < queueFamilies.capacity() || presentFamily >= 0; i++) {
                vkGetPhysicalDeviceSurfaceSupportKHR(GPU, i, surface, presentSupport);

                if(presentSupport.get(0) == VK_TRUE) {
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

    private VulkanSwapChain createSwapChain(long surface, int width, int height) {
        try (MemoryStack stack = stackPush()) {
            SwapChainSupportDetails swapChainSupport = querySwapChainSupport(GPU, stack, surface);

            VulkanSwapChain swapChain = new VulkanSwapChain();

            VkSurfaceFormatKHR surfaceFormat = chooseSwapSurfaceFormat(swapChainSupport.formats);
            int presentMode = chooseSwapPresentMode(swapChainSupport.presentModes);
            VkExtent2D extent = chooseSwapExtent(swapChainSupport.capabilities, width, height);

            IntBuffer imageCount = stack.ints(swapChainSupport.capabilities.minImageCount() + 1);

            if(swapChainSupport.capabilities.maxImageCount() > 0 && imageCount.get(0) > swapChainSupport.capabilities.maxImageCount()) {
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

            if(!graphicsFamily.equals(presentFamily)) {
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

            if(vkCreateSwapchainKHR(logicalDevice, createInfo, null, pSwapChain) != VK_SUCCESS) {
                throw new RuntimeException("Failed to create swap chain");
            }

            swapChain.swapChain = pSwapChain.get(0);

            vkGetSwapchainImagesKHR(logicalDevice, swapChain.swapChain, imageCount, null);

            LongBuffer pSwapchainImages = stack.mallocLong(imageCount.get(0));

            vkGetSwapchainImagesKHR(logicalDevice, swapChain.swapChain, imageCount, pSwapchainImages);

            swapChain.swapChainImages = new ArrayList<>(imageCount.get(0));

            for(int i = 0;i < pSwapchainImages.capacity();i++) {
                swapChain.swapChainImages.add(pSwapchainImages.get(i));
            }

            swapChain.swapChainImageFormat = surfaceFormat.format();
            swapChain.swapChainExtent = VkExtent2D.create().set(extent);
            return swapChain;
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

    private VkExtent2D chooseSwapExtent(VkSurfaceCapabilitiesKHR capabilities, int width, int height) {

        if(capabilities.currentExtent().width() != 0xFFFFFFFF) {
            return capabilities.currentExtent();
        }

        VkExtent2D actualExtent = VkExtent2D.malloc().set(width, height);

        VkExtent2D minExtent = capabilities.minImageExtent();
        VkExtent2D maxExtent = capabilities.maxImageExtent();

        actualExtent.width(clamp(minExtent.width(), maxExtent.width(), actualExtent.width()));
        actualExtent.height(clamp(minExtent.height(), maxExtent.height(), actualExtent.height()));

        return actualExtent;
    }

    private SwapChainSupportDetails querySwapChainSupport(VkPhysicalDevice device, MemoryStack stack, long surface) {

        SwapChainSupportDetails details = new SwapChainSupportDetails();

        details.capabilities = VkSurfaceCapabilitiesKHR.mallocStack(stack);
        vkGetPhysicalDeviceSurfaceCapabilitiesKHR(device, surface, details.capabilities);

        IntBuffer count = stack.ints(0);

        vkGetPhysicalDeviceSurfaceFormatsKHR(device, surface, count, null);

        if(count.get(0) != 0) {
            details.formats = VkSurfaceFormatKHR.malloc(count.get(0), stack);
            vkGetPhysicalDeviceSurfaceFormatsKHR(device, surface, count, details.formats);
        }

        vkGetPhysicalDeviceSurfacePresentModesKHR(device,surface, count, null);

        if(count.get(0) != 0) {
            details.presentModes = stack.mallocInt(count.get(0));
            vkGetPhysicalDeviceSurfacePresentModesKHR(device, surface, count, details.presentModes);
        }

        return details;
    }

    private int clamp(int min, int max, int value) {
        return Math.max(min, Math.min(max, value));
    }

    public static void terminate() {
        vkDestroyDevice(logicalDevice, null);
        VulkanDebugger.destroyDebugUtilsMessengerEXT(instance, null);
        vkDestroyInstance(instance, null);

    }

    private static void check() {
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
}
