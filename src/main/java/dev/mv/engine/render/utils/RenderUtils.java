package dev.mv.engine.render.utils;

import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.LongBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Collection;

import static org.lwjgl.system.MemoryStack.stackGet;

public class RenderUtils {

    public static FloatBuffer store(float... data) {
        FloatBuffer buffer = MemoryUtil.memAllocFloat(data.length);
        buffer.put(data).flip();
        return buffer;
    }

    public static IntBuffer store(int... data) {
        IntBuffer buffer = MemoryUtil.memAllocInt(data.length);
        buffer.put(data).flip();
        return buffer;
    }

    public static LongBuffer store(long... data) {
        LongBuffer buffer = MemoryUtil.memAllocLong(data.length);
        buffer.put(data).flip();
        return buffer;
    }

    public static ByteBuffer storeAsByte(float... data) {
        ByteBuffer buffer = MemoryUtil.memAlloc(data.length * Float.BYTES);
        for (float f : data) {
            buffer.putFloat(f);
        }
        buffer.flip();
        return buffer;
    }

    public static ByteBuffer storeAsByte(int... data) {
        ByteBuffer buffer = MemoryUtil.memAlloc(data.length * Integer.BYTES);
        for (int i : data) {
            buffer.putInt(i);
        }
        buffer.flip();
        return buffer;
    }

    public static ByteBuffer storeAsByte(long... data) {
        ByteBuffer buffer = MemoryUtil.memAlloc(data.length * Long.BYTES);
        for (long l : data) {
            buffer.putLong(l);
        }
        buffer.flip();
        return buffer;
    }

    public static ByteBuffer storeAsByteTerminated(float... data) {
        ByteBuffer buffer = MemoryUtil.memAlloc(data.length * Float.BYTES + 1);
        for (float f : data) {
            buffer.putFloat(f);
        }
        buffer.put((byte) 0b0);
        buffer.flip();
        return buffer;
    }

    public static ByteBuffer storeAsByteTerminated(int... data) {
        ByteBuffer buffer = MemoryUtil.memAlloc(data.length * Integer.BYTES + 1);
        for (int i : data) {
            buffer.putInt(i);
        }
        buffer.put((byte) 0b0);
        buffer.flip();
        return buffer;
    }

    public static ByteBuffer storeAsByteTerminated(long... data) {
        ByteBuffer buffer = MemoryUtil.memAlloc(data.length * Long.BYTES + 1);
        for (long l : data) {
            buffer.putLong(l);
        }
        buffer.put((byte) 0b0);
        buffer.flip();
        return buffer;
    }

    public static ByteBuffer store(byte... data) {
        ByteBuffer buffer = MemoryUtil.memAlloc(data.length);
        buffer.put(data).flip();
        return buffer;
    }

    public static FloatBuffer storeTerminated(float... data) {
        FloatBuffer buffer = MemoryUtil.memAllocFloat(data.length + 1);
        buffer.put(data).put(0f).flip();
        return buffer;
    }

    public static IntBuffer storeTerminated(int... data) {
        IntBuffer buffer = MemoryUtil.memAllocInt(data.length + 1);
        buffer.put(data).put(0).flip();
        return buffer;
    }

    public static LongBuffer storeTerminated(long... data) {
        LongBuffer buffer = MemoryUtil.memAllocLong(data.length + 1);
        buffer.put(data).put(0).flip();
        return buffer;
    }

    public static ByteBuffer storeTerminated(byte... data) {
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
