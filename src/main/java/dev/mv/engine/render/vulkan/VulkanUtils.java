package dev.mv.engine.render.vulkan;

import static org.lwjgl.vulkan.VK13.VK_SUCCESS;

public class VulkanUtils {
    private VulkanUtils() {
        // Utility class
    }

    public static void vkCheck(int err, String errMsg) {
        if (err != VK_SUCCESS) {
            throw new RuntimeException(errMsg + ": " + err);
        }
    }
}
