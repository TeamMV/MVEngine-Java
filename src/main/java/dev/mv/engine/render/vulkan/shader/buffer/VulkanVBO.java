package dev.mv.engine.render.vulkan.shader.buffer;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class VulkanVBO {
    private int offset, stride;
    private byte[] data;
    private List<VulkanVBOPointer> pointers;

    public VulkanVBO(int offset, int stride, byte[] data) {
        this.offset = offset;
        this.stride = stride;
        this.data = data;
        pointers = new ArrayList<>();
    }

    public void createVertexBufferObjectPointer(int offsetInVBO, int attribSize, int shaderLocation) {
        pointers.add(new VulkanVBOPointer(offsetInVBO, attribSize, shaderLocation));
    }

    public List<VulkanVBOPointer> getAllPointers() {
        return pointers;
    }

    public int getOffset() {
        return offset;
    }

    public int getStride() {
        return stride;
    }

    public byte[] getData() {
        return data;
    }
}
