package dev.mv.engine.render.vulkan;

import java.util.stream.IntStream;

public class VulkanQueueFamilyIndices {
    Integer graphicsFamily;
    Integer presentFamily;

    boolean isComplete() {
        return graphicsFamily != null && presentFamily != null;
    }

    public int[] unique() {
        return IntStream.of(graphicsFamily, presentFamily).distinct().toArray();
    }
}