package dev.mv.engine.render.vulkan;

import dev.mv.engine.render.utils.RenderUtils;
import dev.mv.utils.misc.Version;
import lombok.Getter;
import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.*;

import java.nio.IntBuffer;
import java.util.*;
import java.util.stream.IntStream;

import static org.lwjgl.glfw.GLFWVulkan.glfwGetRequiredInstanceExtensions;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.vulkan.VK10.*;


public class Vulkan {

    @Getter
    private static VkInstance instance;
    @Getter
    private static VkPhysicalDevice GPU;
    @Getter
    private static VkDevice logicalDevice;
    @Getter
    private static VkQueue graphicsQueue;

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
        return true;
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

            OptionalInt optionalIndex = IntStream.range(0, queueFamilies.capacity()).filter(index -> (queueFamilies.get(index).queueFlags() & queueBit) != 0).findFirst();
            return optionalIndex.isPresent()? optionalIndex.getAsInt() : null;
        }
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

    public static void terminate() {
        vkDestroyDevice(logicalDevice, null);
        VulkanDebugger.destroyDebugUtilsMessengerEXT(instance, null);
        vkDestroyInstance(instance, null);
    }
}
