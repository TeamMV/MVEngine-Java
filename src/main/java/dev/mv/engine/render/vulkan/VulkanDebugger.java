package dev.mv.engine.render.vulkan;

import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.*;

import java.nio.IntBuffer;
import java.nio.LongBuffer;
import java.util.HashSet;
import java.util.Set;

import static java.util.stream.Collectors.toSet;
import static org.lwjgl.system.MemoryStack.stackGet;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.NULL;
import static org.lwjgl.vulkan.EXTDebugUtils.*;
import static org.lwjgl.vulkan.VK10.*;

public class VulkanDebugger {

    private static final boolean ENABLE_VALIDATION_LAYERS = false;
    private static final Set<String> VALIDATION_LAYERS;

    private static long debugMessenger;

    static {
        if (ENABLE_VALIDATION_LAYERS) {
            VALIDATION_LAYERS = new HashSet<>();
            VALIDATION_LAYERS.add("VK_LAYER_KHRONOS_validation");
        } else {
            VALIDATION_LAYERS = null;
        }
    }

    static void checks() {
        if (ENABLE_VALIDATION_LAYERS && !checkValidationLayerSupport()) {
            throw new RuntimeException("Validation requested but not supported");
        }
    }

    static void init(MemoryStack stack, VkInstanceCreateInfo createInfo) {
        createInfo.ppEnabledExtensionNames(getRequiredExtensions());
        if (ENABLE_VALIDATION_LAYERS) {
            createInfo.ppEnabledLayerNames(validationLayersAsPointerBuffer());
            VkDebugUtilsMessengerCreateInfoEXT debugCreateInfo = VkDebugUtilsMessengerCreateInfoEXT.calloc(stack);
            populateDebugMessengerCreateInfo(debugCreateInfo);
            createInfo.pNext(debugCreateInfo.address());
        }
    }

    static void init(VkDeviceCreateInfo createInfo) {
        if (VulkanDebugger.ENABLE_VALIDATION_LAYERS) {
            createInfo.ppEnabledLayerNames(validationLayersAsPointerBuffer());
        }
    }

    private static int debugCallback(int messageSeverity, int messageType, long pCallbackData, long pUserData) {
        VkDebugUtilsMessengerCallbackDataEXT callbackData = VkDebugUtilsMessengerCallbackDataEXT.create(pCallbackData);
        System.err.println("Validation layer: " + callbackData.pMessageString());
        return VK_FALSE;
    }

    private static int createDebugUtilsMessengerEXT(VkInstance instance, VkDebugUtilsMessengerCreateInfoEXT createInfo, VkAllocationCallbacks allocationCallbacks, LongBuffer pDebugMessenger) {
        if (vkGetInstanceProcAddr(instance, "vkCreateDebugUtilsMessengerEXT") != NULL) {
            return vkCreateDebugUtilsMessengerEXT(instance, createInfo, allocationCallbacks, pDebugMessenger);
        }
        return VK_ERROR_EXTENSION_NOT_PRESENT;
    }

    static void destroyDebugUtilsMessengerEXT(VkInstance instance, VkAllocationCallbacks allocationCallbacks) {
        if (!ENABLE_VALIDATION_LAYERS) return;
        if (vkGetInstanceProcAddr(instance, "vkDestroyDebugUtilsMessengerEXT") != NULL) {
            vkDestroyDebugUtilsMessengerEXT(instance, debugMessenger, allocationCallbacks);
        }
    }

    private static void populateDebugMessengerCreateInfo(VkDebugUtilsMessengerCreateInfoEXT debugCreateInfo) {
        debugCreateInfo.sType(VK_STRUCTURE_TYPE_DEBUG_UTILS_MESSENGER_CREATE_INFO_EXT);
        debugCreateInfo.messageSeverity(VK_DEBUG_UTILS_MESSAGE_SEVERITY_VERBOSE_BIT_EXT | VK_DEBUG_UTILS_MESSAGE_SEVERITY_WARNING_BIT_EXT | VK_DEBUG_UTILS_MESSAGE_SEVERITY_ERROR_BIT_EXT);
        debugCreateInfo.messageType(VK_DEBUG_UTILS_MESSAGE_TYPE_GENERAL_BIT_EXT | VK_DEBUG_UTILS_MESSAGE_TYPE_VALIDATION_BIT_EXT | VK_DEBUG_UTILS_MESSAGE_TYPE_PERFORMANCE_BIT_EXT);
        debugCreateInfo.pfnUserCallback(VulkanDebugger::debugCallback);
    }

    static void setupDebugger(VkInstance instance) {
        if (!ENABLE_VALIDATION_LAYERS) return;

        try (MemoryStack stack = stackPush()) {
            VkDebugUtilsMessengerCreateInfoEXT createInfo = VkDebugUtilsMessengerCreateInfoEXT.calloc(stack);

            populateDebugMessengerCreateInfo(createInfo);
            LongBuffer pDebugMessenger = stack.longs(VK_NULL_HANDLE);

            if (createDebugUtilsMessengerEXT(instance, createInfo, null, pDebugMessenger) != VK_SUCCESS) {
                throw new RuntimeException("Failed to set up debug messenger");
            }
            debugMessenger = pDebugMessenger.get(0);
        }
    }

    private static PointerBuffer validationLayersAsPointerBuffer() {
        MemoryStack stack = stackGet();

        PointerBuffer buffer = stack.mallocPointer(VALIDATION_LAYERS.size());

        VALIDATION_LAYERS.stream().map(stack::UTF8).forEach(buffer::put);
        return buffer.rewind();
    }

    private static PointerBuffer getRequiredExtensions() {
        PointerBuffer glfwExtensions = Vulkan.getRequiredExtensions();
        if (ENABLE_VALIDATION_LAYERS) {
            MemoryStack stack = stackGet();

            PointerBuffer extensions = stack.mallocPointer(glfwExtensions.capacity() + 1);

            extensions.put(glfwExtensions);
            extensions.put(stack.UTF8(VK_EXT_DEBUG_UTILS_EXTENSION_NAME));
            return extensions.rewind();
        }
        return glfwExtensions;
    }

    private static boolean checkValidationLayerSupport() {
        try (MemoryStack stack = stackPush()) {
            IntBuffer layerCount = stack.ints(0);

            vkEnumerateInstanceLayerProperties(layerCount, null);
            VkLayerProperties.Buffer availableLayers = VkLayerProperties.malloc(layerCount.get(0), stack);

            vkEnumerateInstanceLayerProperties(layerCount, availableLayers);
            Set<String> availableLayerNames = availableLayers.stream().map(VkLayerProperties::layerNameString).collect(toSet());

            return availableLayerNames.containsAll(VALIDATION_LAYERS);
        }
    }

}
