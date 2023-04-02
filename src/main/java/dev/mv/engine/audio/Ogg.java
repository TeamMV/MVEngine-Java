package dev.mv.engine.audio;

import dev.mv.engine.exceptions.Exceptions;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.Objects;

import static org.lwjgl.stb.STBVorbis.stb_vorbis_decode_memory;

public class Ogg implements SoundFormat {
    @Override
    public Sound.Raw load(InputStream stream, String name) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer channels = stack.callocInt(1);
            IntBuffer sampleRate = stack.callocInt(1);

            byte[] bytes = stream.readAllBytes();
            ByteBuffer buffer = MemoryUtil.memAlloc(bytes.length).put(bytes).flip();
            ByteBuffer raw = MemoryUtil.memByteBuffer(Objects.requireNonNullElse(stb_vorbis_decode_memory(buffer, channels, sampleRate), MemoryUtil.memAllocShort(1)));
            return new Sound.Raw(raw, channels.get(), sampleRate.get());
        } catch (Exception e) {
            Exceptions.send("BROKEN_AUDIO_FILE", name);
        }
        return null;
    }
}
