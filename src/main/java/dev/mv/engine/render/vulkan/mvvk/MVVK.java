package dev.mv.engine.render.vulkan.mvvk;

import java.nio.FloatBuffer;

public class MVVK {
    public static void mvvkDrawIndices(RenderMode mode) {
    }

    public enum RenderMode {
        TRIANGLES(1),
        QUAD(2);

        private int i;

        RenderMode(int i) {
        }
    }
}
