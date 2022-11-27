package dev.mv.engine.render.vulkan;

import static org.lwjgl.vulkan.VK10.*;

public class VulkanBuffer {
    protected VulkanContext ctx;

    protected VulkanBuffer(VulkanContext ctx) {
        this.ctx = ctx;
    }

    protected long map() {
        result vkMapMemory();
    }
}
