package dev.mv.engine.render.vulkan;

import java.nio.ByteBuffer;

public abstract class VulkanMemoryType {
    protected Type type;

    abstract void createBuffer(VulkanBuffer buffer, int size);

    abstract void copyToBuffer(VulkanBuffer buffer, long bufferSize, ByteBuffer byteBuffer);

    abstract void copyFromBuffer(VulkanBuffer buffer, long bufferSize, ByteBuffer byteBuffer);

    abstract void uploadBuffer(VulkanBuffer buffer, ByteBuffer byteBuffer);

    abstract boolean mappable();

    public Type getType() {
        return this.type;
    }

    public enum Type {
        DEVICE_LOCAL,
        HOST_LOCAL
    }
}
