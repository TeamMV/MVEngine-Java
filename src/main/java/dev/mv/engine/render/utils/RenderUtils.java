package dev.mv.engine.render.utils;

import org.lwjgl.system.MemoryUtil;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.charset.StandardCharsets;

public class RenderUtils {
    public static FloatBuffer store(float[] data) {
        FloatBuffer buffer = MemoryUtil.memAllocFloat(data.length);
        buffer.put(data).flip();
        return buffer;
    }

    public static IntBuffer store(int[] data) {
        IntBuffer buffer = MemoryUtil.memAllocInt(data.length);
        buffer.put(data).flip();
        return buffer;
    }

    public static ByteBuffer store(byte[] data) {
        ByteBuffer buffer = MemoryUtil.memAlloc(data.length);
        buffer.put(data).flip();
        return buffer;
    }

    public static ByteBuffer store(String data) {
        byte[] arr = new byte[data.length() + 1];
        byte[] strBytes = data.getBytes(StandardCharsets.UTF_8);
        System.arraycopy(strBytes, 0, arr, 0, strBytes.length);
        arr[arr.length - 1] = 0b0;
        return store(arr);
    }
}
