package dev.mv.engine.render.utils;

import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;
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

    public static float[] array(Vector2f data) {
        return new float[] {data.x, data.y};
    }

    public static float[] array(Vector3f data) {
        return new float[] {data.x, data.y, data.z};
    }

    public static float[] array(Vector4f data) {
        return new float[] {data.x, data.y, data.z, data.w};
    }
}
