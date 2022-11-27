package dev.mv.engine.render.vulkan;

import org.lwjgl.PointerBuffer;

public class VulkanBuffer {
    protected long id;
    protected long allocation;

    protected int bufferSize;
    protected int usedBytes;
    protected int offset;

    //    protected Buffer.Type type;
    protected VulkanMemoryType type;
    protected int usage;
    protected PointerBuffer data;

    protected VulkanBuffer(int usage, VulkanMemoryType type) {
        //TODO: check usage
        this.usage = usage;
        this.type = type;
    }

    protected void createBuffer(int bufferSize) {
        this.type.createBuffer(this, bufferSize);

        if (this.type.mappable()) {
            this.data = Map(this.allocation);
        }
    }

    public void freeBuffer() {
        VulkanMemoryManager.addToFreeable(this);
    }

    public void reset() {
        usedBytes = 0;
    }

    public long getAllocation() {
        return allocation;
    }

    protected void setAllocation(long allocation) {
        this.allocation = allocation;
    }

    public long getUsedBytes() {
        return usedBytes;
    }

    public long getId() {
        return id;
    }

    protected void setId(long id) {
        this.id = id;
    }

    protected void setBufferSize(int size) {
        this.bufferSize = size;
    }

    public BufferInfo getBufferInfo() {
        return new BufferInfo(this.id, this.allocation, this.bufferSize, this.type.getType());
    }

    public record BufferInfo(long id, long allocation, long bufferSize, VulkanMemoryType.Type type) {
    }
}
