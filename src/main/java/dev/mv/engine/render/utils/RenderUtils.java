package dev.mv.engine.render.utils;

import org.joml.*;
import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

import java.lang.reflect.Array;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

import static org.lwjgl.system.MemoryStack.stackGet;

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

    public static FloatBuffer storeTerminated(float[] data) {
        FloatBuffer buffer = MemoryUtil.memAllocFloat(data.length + 1);
        buffer.put(data).put(0f).flip();
        return buffer;
    }

    public static IntBuffer storeTerminated(int[] data) {
        IntBuffer buffer = MemoryUtil.memAllocInt(data.length + 1);
        buffer.put(data).put(0).flip();
        return buffer;
    }

    public static ByteBuffer storeTerminated(byte[] data) {
        ByteBuffer buffer = MemoryUtil.memAlloc(data.length + 1);
        buffer.put(data).put((byte) 0).flip();
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
        return new float[]{data.x, data.y};
    }

    public static float[] array(Vector3f data) {
        return new float[]{data.x, data.y, data.z};
    }

    public static float[] array(Vector4f data) {
        return new float[]{data.x, data.y, data.z, data.w};
    }

    public static PointerBuffer asPointerBuffer(Collection<String> collection) {
        MemoryStack stack = stackGet();

        PointerBuffer buffer = stack.mallocPointer(collection.size());

        collection.stream()
            .map(stack::UTF8)
            .forEach(buffer::put);

        return buffer.rewind();
    }

    public static <T> T vectorize(Collection<Float> data) {
        if (data.size() == 2) {
            Vector2f out = new Vector2f();
            Float[] arr = (Float[]) data.toArray();
            out.x = arr[0];
            out.y = arr[1];
            return (T) out;
        }
        if (data.size() == 3) {
            Vector3f out = new Vector3f();
            Float[] arr = (Float[]) data.toArray();
            out.x = arr[0];
            out.y = arr[1];
            out.z = arr[2];
            return (T) out;
        }
        if (data.size() == 4) {
            Vector4f out = new Vector4f();
            Float[] arr = (Float[]) data.toArray();
            out.x = arr[0];
            out.y = arr[1];
            out.z = arr[2];
            out.w = arr[3];
            return (T) out;
        }
        return null;
    }
}
