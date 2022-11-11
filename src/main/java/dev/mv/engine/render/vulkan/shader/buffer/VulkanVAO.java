package dev.mv.engine.render.vulkan.shader.buffer;

import org.lwjgl.BufferUtils;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class VulkanVAO {
    List<VulkanVBO> vbos;

    public VulkanVAO() {
        vbos = new ArrayList<>();
    }

    public void addVBO(VulkanVBO vbo) {
        vbos.add(vbo);
    }

    public void clearVBOs() {
        vbos.clear();
    }

    public List<VulkanVBO> getVbos() {
        return vbos;
    }

    public ByteBuffer genContainer() {
        int bytes = 0;
        for(VulkanVBO vbo : vbos) {
            bytes += vbo.getStride() * Float.BYTES;
        }

        ByteBuffer container = BufferUtils.createByteBuffer(bytes);

        for(VulkanVBO vbo : vbos) {
            container.put(vbo.getData());
        }

        return container;
    }
}
