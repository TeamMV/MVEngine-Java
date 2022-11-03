package dev.mv.engine.render.opengl.glutils;

import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;

public class OpenGLUtils {
    public static FloatBuffer store(float[] data) {
        FloatBuffer buffer = MemoryUtil.memAllocFloat(data.length);
        buffer.put(data).flip();
        return buffer;
    }
}
