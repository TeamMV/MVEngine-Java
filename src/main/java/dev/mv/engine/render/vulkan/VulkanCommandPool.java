package dev.mv.engine.render.vulkan;

import lombok.Getter;
import org.lwjgl.vulkan.VkCommandBuffer;

import java.util.ArrayList;
import java.util.List;

public class VulkanCommandPool {
    @Getter
    long id;
    @Getter
    List<VkCommandBuffer> buffers = new ArrayList<>();
}
