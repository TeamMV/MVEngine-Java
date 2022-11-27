package dev.mv.engine.render.vulkan;

import dev.mv.engine.ApplicationConfig;
import dev.mv.engine.MVEngine;
import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.*;

import java.nio.IntBuffer;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.IntStream;

import static org.lwjgl.glfw.GLFWVulkan.glfwGetRequiredInstanceExtensions;
import static org.lwjgl.vulkan.VK10.*;

public class Vulkan {

    static VkInstance instance;
    static VkPhysicalDevice GPU;
    static VkDevice GPUWrapper;

    public static boolean init(ApplicationConfig config) {
        if (!createInstance(config)) return false;
        if (!VulkanDebugger.setupDebugMessenger()) return false;
        if (!pickPhysicalDevice()) return false;
        return true;
    }

    public static void terminate() {
        VulkanDebugger.terminateDebugger();
        vkDestroyInstance(instance, null);
    }

    private static boolean createInstance(ApplicationConfig config) {
        if(!VulkanDebugger.checkDebugStatus()){
            return false;
        }
        try(MemoryStack stack = MemoryStack.stackPush()) {
            VkApplicationInfo info = VkApplicationInfo.calloc(stack);
            info.sType(VK_STRUCTURE_TYPE_APPLICATION_INFO);
            info.pApplicationName(stack.UTF8Safe(config.getName()));
            info.applicationVersion(config.getVersion().toVulkanVersion());
            info.pEngineName(stack.UTF8Safe("MVEngine"));
            info.engineVersion(MVEngine.VERSION.toVulkanVersion());
            info.apiVersion(VK_MAKE_API_VERSION(1, 1, 0, 0));

            VkInstanceCreateInfo createInfo = VkInstanceCreateInfo.calloc(stack);
            createInfo.sType(VK_STRUCTURE_TYPE_INSTANCE_CREATE_INFO);
            createInfo.pApplicationInfo(info);
            createInfo.ppEnabledExtensionNames(glfwGetRequiredInstanceExtensions());
            createInfo.ppEnabledLayerNames(null);

            PointerBuffer instancePtr = stack.mallocPointer(1);
            if(vkCreateInstance(createInfo, null, instancePtr) != VK_SUCCESS) {
                return false;
            }
            instance = new VkInstance(instancePtr.get(0), createInfo);
        }
        return true;
    }

    private static boolean pickPhysicalDevice() {
        int bestScore = 0;
        try(MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer deviceCount = stack.ints(0);
            vkEnumeratePhysicalDevices(instance, deviceCount, null);

            if(deviceCount.get(0) == 0) {
                return false;
            }

            PointerBuffer ppPhysicalDevices = stack.mallocPointer(deviceCount.get(0));
            vkEnumeratePhysicalDevices(instance, deviceCount, ppPhysicalDevices);

            int score = 0;
            for(int i = 0;i < ppPhysicalDevices.capacity();i++) {
                VkPhysicalDevice device = new VkPhysicalDevice(ppPhysicalDevices.get(i), instance);
                if(isDeviceSuitable(device)) {
                    if((score = rateDevice(device)) > bestScore) {
                        bestScore = score;
                        GPU = device;
                    }
                }
            }
            if (bestScore == 0 || GPU == null) {
                return false;
            }
        }
        return true;
    }

    private static int rateDevice(VkPhysicalDevice device) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            int score = 0;
            VkPhysicalDeviceFeatures deviceFeatures = VkPhysicalDeviceFeatures.calloc(stack);
            vkGetPhysicalDeviceFeatures(device, deviceFeatures);
            VkPhysicalDeviceProperties deviceProperties = VkPhysicalDeviceProperties.calloc(stack);
            vkGetPhysicalDeviceProperties(device, deviceProperties);

            if (deviceProperties.deviceType() == VK_PHYSICAL_DEVICE_TYPE_DISCRETE_GPU) {
                score += 32000;
            }
            else if (deviceProperties.deviceType() == VK_PHYSICAL_DEVICE_TYPE_INTEGRATED_GPU) {
                score += 16000;
            }
            else if (deviceProperties.deviceType() == VK_PHYSICAL_DEVICE_TYPE_VIRTUAL_GPU) {
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

    private static boolean isDeviceSuitable(VkPhysicalDevice device){
        QueueFamilyIndices indices = findQueueFamilies(device);

        return indices.isComplete();
    }

    private static QueueFamilyIndices findQueueFamilies(VkPhysicalDevice device) {
        QueueFamilyIndices indices = new QueueFamilyIndices();
        try(MemoryStack stack = MemoryStack.stackPush()) {
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
}
