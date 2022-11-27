package dev.mv.engine.render.vulkan;

public class QueueFamilyIndices {
    Integer graphicsFamily;

    boolean isComplete() {
        return graphicsFamily != null;
    }
}