package dev.mv.engine.render.vulkan;

import dev.mv.utils.logger.Logger;
import org.lwjgl.PointerBuffer;
import org.lwjgl.glfw.GLFWVulkan;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.vulkan.*;

import java.net.UnknownServiceException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.LongBuffer;
import java.util.ArrayList;
import java.util.List;

import static dev.mv.engine.render.vulkan.VulkanUtils.vkCheck;
import static org.lwjgl.vulkan.EXTDebugUtils.*;
import static org.lwjgl.vulkan.VK13.*;

public class VulkanInstance {

    public static final int MESSAGE_SEVERITY_BITMASK = VK_DEBUG_UTILS_MESSAGE_SEVERITY_ERROR_BIT_EXT |
        VK_DEBUG_UTILS_MESSAGE_SEVERITY_WARNING_BIT_EXT;
    public static final int MESSAGE_TYPE_BITMASK = VK_DEBUG_UTILS_MESSAGE_TYPE_GENERAL_BIT_EXT |
        VK_DEBUG_UTILS_MESSAGE_TYPE_VALIDATION_BIT_EXT |
        VK_DEBUG_UTILS_MESSAGE_TYPE_PERFORMANCE_BIT_EXT;

    private final VkInstance vkInstance;

    private VkDebugUtilsMessengerCreateInfoEXT debugUtils;
    private long vkDebugHandle;


    public VulkanInstance(boolean validate) throws UnknownServiceException {
        Logger.debug("Creating Vulkan instance");
        try (MemoryStack stack = MemoryStack.stackPush()) {
            ByteBuffer appShortName = stack.UTF8("MVEngine");
            VkApplicationInfo appInfo = VkApplicationInfo.calloc(stack)
                .sType(VK_STRUCTURE_TYPE_APPLICATION_INFO)
                .pApplicationName(appShortName)
                .applicationVersion(1)
                .pEngineName(appShortName)
                .engineVersion(0)
                .apiVersion(VK_API_VERSION_1_3);
            List<String> validationLayers = getSupportedValidationLayers();
            int numValidationLayers = validationLayers.size();
            boolean supportsValidation = validate;
            if (validate && numValidationLayers == 0) {
                supportsValidation = false;
            }

            // Set required  layers
            PointerBuffer requiredLayers = null;
            if (supportsValidation) {
                requiredLayers = stack.mallocPointer(numValidationLayers);
                for (int i = 0; i < numValidationLayers; i++) {
                    Logger.debug("Using validation layer " + validationLayers.get(i));
                    requiredLayers.put(i, stack.ASCII(validationLayers.get(i)));
                }
            }

            // GLFW Extension
            PointerBuffer glfwExtensions = GLFWVulkan.glfwGetRequiredInstanceExtensions();
            if (glfwExtensions == null) {
                throw new UnknownServiceException("Failed to find the GLFW platform surface extensions");
            }

            PointerBuffer requiredExtensions;
            if (supportsValidation) {
                ByteBuffer vkDebugUtilsExtension = stack.UTF8(EXTDebugUtils.VK_EXT_DEBUG_UTILS_EXTENSION_NAME);
                requiredExtensions = stack.mallocPointer(glfwExtensions.remaining() + 1);
                requiredExtensions.put(glfwExtensions).put(vkDebugUtilsExtension);
            } else {
                requiredExtensions = stack.mallocPointer(glfwExtensions.remaining());
                requiredExtensions.put(glfwExtensions);
            }
            requiredExtensions.flip();

            long extension = MemoryUtil.NULL;
            if (supportsValidation) {
                debugUtils = createDebugCallBack();
                extension = debugUtils.address();
            }

            // Create instance info
            VkInstanceCreateInfo instanceInfo = VkInstanceCreateInfo.calloc(stack)
                .sType(VK_STRUCTURE_TYPE_INSTANCE_CREATE_INFO)
                .pNext(extension)
                .pApplicationInfo(appInfo)
                .ppEnabledLayerNames(requiredLayers)
                .ppEnabledExtensionNames(requiredExtensions);

            PointerBuffer pInstance = stack.mallocPointer(1);
            vkCheck(vkCreateInstance(instanceInfo, null, pInstance), "Error creating instance");
            vkInstance = new VkInstance(pInstance.get(0), instanceInfo);

            vkDebugHandle = VK_NULL_HANDLE;
            if (supportsValidation) {
                LongBuffer longBuff = stack.mallocLong(1);
                vkCheck(vkCreateDebugUtilsMessengerEXT(vkInstance, debugUtils, null, longBuff), "Error creating debug utils");
                vkDebugHandle = longBuff.get(0);
            }
        }
    }

    private static VkDebugUtilsMessengerCreateInfoEXT createDebugCallBack() {
        VkDebugUtilsMessengerCreateInfoEXT result = VkDebugUtilsMessengerCreateInfoEXT
            .calloc()
            .sType(VK_STRUCTURE_TYPE_DEBUG_UTILS_MESSENGER_CREATE_INFO_EXT)
            .messageSeverity(MESSAGE_SEVERITY_BITMASK)
            .messageType(MESSAGE_TYPE_BITMASK)
            .pfnUserCallback((messageSeverity, messageTypes, pCallbackData, pUserData) -> {
                VkDebugUtilsMessengerCallbackDataEXT callbackData = VkDebugUtilsMessengerCallbackDataEXT.create(pCallbackData);
                if ((messageSeverity & VK_DEBUG_UTILS_MESSAGE_SEVERITY_INFO_BIT_EXT) != 0) {
                    Logger.info("VkDebugUtilsCallback, " + callbackData.pMessageString());
                } else if ((messageSeverity & VK_DEBUG_UTILS_MESSAGE_SEVERITY_WARNING_BIT_EXT) != 0) {
                    Logger.warn("VkDebugUtilsCallback, " + callbackData.pMessageString());
                } else if ((messageSeverity & VK_DEBUG_UTILS_MESSAGE_SEVERITY_ERROR_BIT_EXT) != 0) {
                    Logger.error("VkDebugUtilsCallback, " + callbackData.pMessageString());
                } else {
                    Logger.debug("VkDebugUtilsCallback, " + callbackData.pMessageString());
                }
                return VK_FALSE;
            });
        return result;
    }

    private List<String> getSupportedValidationLayers() {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer numLayersArr = stack.callocInt(1);
            vkEnumerateInstanceLayerProperties(numLayersArr, null);
            int numLayers = numLayersArr.get(0);
            Logger.debug("Instance supports " + numLayers + " layers");

            VkLayerProperties.Buffer propsBuf = VkLayerProperties.calloc(numLayers, stack);
            vkEnumerateInstanceLayerProperties(numLayersArr, propsBuf);
            List<String> supportedLayers = new ArrayList<>();
            for (int i = 0; i < numLayers; i++) {
                VkLayerProperties props = propsBuf.get(i);
                String layerName = props.layerNameString();
                supportedLayers.add(layerName);
                Logger.debug("Supported layer " + layerName);
            }

            List<String> layersToUse = new ArrayList<>();

            // Main validation layer
            if (supportedLayers.contains("VK_LAYER_KHRONOS_validation")) {
                layersToUse.add("VK_LAYER_KHRONOS_validation");
                return layersToUse;
            }

            // Fallback 1
            if (supportedLayers.contains("VK_LAYER_LUNARG_standard_validation")) {
                layersToUse.add("VK_LAYER_LUNARG_standard_validation");
                return layersToUse;
            }

            // Fallback 2 (set)
            List<String> requestedLayers = new ArrayList<>();
            requestedLayers.add("VK_LAYER_GOOGLE_threading");
            requestedLayers.add("VK_LAYER_LUNARG_parameter_validation");
            requestedLayers.add("VK_LAYER_LUNARG_object_tracker");
            requestedLayers.add("VK_LAYER_LUNARG_core_validation");
            requestedLayers.add("VK_LAYER_GOOGLE_unique_objects");

            List<String> overlap = requestedLayers.stream().filter(supportedLayers::contains).toList();

            return overlap;
        }
    }

    public void cleanup() {
        Logger.debug("Destroying Vulkan instance");
        if (vkDebugHandle != VK_NULL_HANDLE) {
            vkDestroyDebugUtilsMessengerEXT(vkInstance, vkDebugHandle, null);
        }
        if (debugUtils != null) {
            debugUtils.pfnUserCallback().free();
            debugUtils.free();
        }
        vkDestroyInstance(vkInstance, null);
    }

    public VkInstance getVkInstance() {
        return vkInstance;
    }
}
