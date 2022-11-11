package dev.mv.engine.render.vulkan;

import lombok.Getter;
import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.util.vma.Vma;
import org.lwjgl.util.vma.VmaAllocatorCreateInfo;
import org.lwjgl.vulkan.VK10;

import static org.lwjgl.system.MemoryStack.stackGet;
import static org.lwjgl.vulkan.VK10.VK_NULL_HANDLE;
import static org.lwjgl.vulkan.VK10.VK_SUCCESS;

public class VulkanMemoryAllocator {

    static long allocator = VK_NULL_HANDLE;

    static boolean init() {
        try (MemoryStack stack = stackGet()) {
            VmaAllocatorCreateInfo createInfo = VmaAllocatorCreateInfo.calloc();

            createInfo.instance(Vulkan.getInstance());
            createInfo.device(Vulkan.getLogicalDevice());
            createInfo.physicalDevice(Vulkan.getGPU());
            createInfo.vulkanApiVersion(Vulkan.getVulkanVersion());

            PointerBuffer pAllocator = stack.callocPointer(1);

            if (Vma.vmaCreateAllocator(createInfo, pAllocator) != VK_SUCCESS) {
                return false;
            }

            allocator = pAllocator.get(0);

            if (allocator == 0) {
                return false;
            }
        }
        return true;
    }

    public static long pointer() {
        if (allocator == 0) {
            throw new RuntimeException("Vulkan Memory Allocator was not initialized properly.");
        }
        return allocator;
    }

}
