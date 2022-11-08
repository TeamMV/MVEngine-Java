package dev.mv.engine.render.vulkan.mvvk;

import dev.mv.engine.render.vulkan.VulkanWindow;
import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

public class MVVKBufferAllocator {
    private static int currentVertexBinding = -1, currentIndexBinding = -1;

    public enum Target{
        VERTEX_BUFFER(1),
        INDEX_BUFFER(2);

        private int i;

        Target(int i) {
        }

        public int getId() {
            return i;
        }
    }
    private static int nextFreeBuffer = 1;

    private static List<MVVKBuffer> buffers;
    private static MVVKBuffer currentBuffer;

    public static void __init(VulkanWindow window) {
        buffers = new ArrayList<>();
    }

    public static int mvvkAllocateBuffer() {
        return nextFreeBuffer++;
    }

    public static void mvvkLinkBuffer(int buffer) {
        if(buffer == 0) {
            currentBuffer = null;
            return;
        }
        try {
            currentBuffer = buffers.get(buffer - 1);
        } catch(NullPointerException ignore) {
            buffers.add((currentBuffer = new MVVKBuffer(buffer)));
        }
    }

    public static void mvvkBufferData(FloatBuffer data) {
        currentBuffer.setData(data);
    }

    public static void mvvkDeleteBuffer(int buffer) {
        if(buffer == currentBuffer.getId()) {
            throw new RuntimeException("Buffer "+buffer+" cannot be deleted while bound!");
        }
        MemoryUtil.nmemFree(MemoryUtil.memAddress(buffers.get(buffer).getData()));
        buffers.remove(buffer);
    }

    public static void mvvkBindBufferTo(Target target) {
        switch (target) {
            case VERTEX_BUFFER -> currentVertexBinding = currentBuffer.getId();
            case INDEX_BUFFER -> currentIndexBinding = currentBuffer.getId();
            default -> throw new RuntimeException("Invalid buffer target!");
        }
    }

    static FloatBuffer currentVertexData() {
        return buffers.get(currentVertexBinding).getData();
    }

    static FloatBuffer currentIndexData() {
        return buffers.get(currentIndexBinding).getData();
    }
}
