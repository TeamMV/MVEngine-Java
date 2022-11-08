package dev.mv.engine.render.vulkan.mvvk;

import lombok.Getter;

import java.nio.FloatBuffer;

public class MVVKBuffer {
    @Getter
    private FloatBuffer data;
    @Getter
    private int id;

    private MVVKBuffer() {}

    MVVKBuffer(int id) {
        this.id = id;
    }

    void setData(FloatBuffer data) {
        this.data = data;
    }
}
