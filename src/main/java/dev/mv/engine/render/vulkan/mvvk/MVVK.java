package dev.mv.engine.render.vulkan.mvvk;

import java.nio.FloatBuffer;

public class MVVK {
    public enum RenderMode{
        TRIANGLES(1),
        QUAD(2);

        private int i;

        RenderMode(int i) {
        }
    }

    public static void mvvkDrawIndices(RenderMode mode) {
        FloatBuffer vertexData = MVVKBufferAllocator.currentVertexData();
        FloatBuffer indexData = MVVKBufferAllocator.currentIndexData();
    }
}
