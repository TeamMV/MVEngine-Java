package dev.mv.engine.render.vulkan;

import java.nio.ByteBuffer;

public class VulkanStagingBuffer extends VulkanBuffer{
    public VulkanStagingBuffer() {
        super(VK_BUFFER_USAGE_TRANSFER_SRC_BIT, VulkanMemoryTypes.HOST_MEM);
        this.usedBytes = 0;
        this.offset = 0;

        this.createStagingBuffer(bufferSize);
    }

    //TODO: use createBuffer instead
    private void createStagingBuffer(int bufferSize) {
        this.createBuffer(bufferSize);
    }

    public void copyBuffer(int size, ByteBuffer byteBuffer) {

        if(size > this.bufferSize - this.usedBytes) {
            resizeBuffer((int)(this.bufferSize + size) * 2);
        }

        Copy(this.data,
            (data) -> VUtil.memcpy(data.getByteBuffer(0, (int) this.bufferSize), byteBuffer, this.usedBytes)
        );

        offset = usedBytes;
        usedBytes += size;

        //createVertexBuffer(vertexSize, vertexCount, byteBuffer);
    }

    private void resizeBuffer(int newSize) {
        //TODO
        VulkanMemoryManager.addToFreeable(this);
        createStagingBuffer(newSize);

        System.out.println("resized staging buffer to: " + newSize);
    }

    public void reset() {
        usedBytes = 0;
    }

    public long getOffset() {
        return offset;
    }

    public long getBufferId() {
        return id;
    }
}
