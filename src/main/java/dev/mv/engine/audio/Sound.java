package dev.mv.engine.audio;

import dev.mv.engine.exceptions.Exceptions;
import dev.mv.engine.resources.Resource;
import org.lwjgl.stb.STBVorbis;
import org.lwjgl.system.MemoryStack;

import java.io.IOException;
import java.io.InputStream;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

import static org.lwjgl.openal.AL11.*;
import static org.lwjgl.system.libc.LibCStdlib.free;

public class Sound implements Resource {

    private Audio audio;
    private int id, buffer;
    private boolean loop;
    private State state;

    Sound(Audio audio, InputStream stream, boolean loop) throws IOException {
        this.audio = audio;
        this.loop = loop;
        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer channelsBuffer = stack.mallocInt(1);
            IntBuffer sampleRateBuffer = stack.mallocInt(1);

            ShortBuffer rawAudioBuffer = STBVorbis.stb_vorbis_decode_memory(stack.bytes(stream.readAllBytes()), channelsBuffer, sampleRateBuffer);
            if (rawAudioBuffer == null) {
                Exceptions.send("SOUND_INIT", "sound buffer was null");
                return;
            }

            buffer = alGenBuffers();
            alBufferData(buffer, channelsBuffer.get() == 1 ? AL_FORMAT_MONO16 : AL_FORMAT_STEREO16, rawAudioBuffer, sampleRateBuffer.get());

            id = audio.nextFreeSource();
            alSourcei(id, AL_BUFFER, buffer);
            alSourcei(id, AL_LOOPING, loop ? 0 : 1);
            alSourcei(id, AL_POSITION, 0);
            alSourcef(id, AL_GAIN, 0.3f);
            free(rawAudioBuffer);
        }
    }

    public void terminate() {
        audio.freeSource(id);
        alDeleteBuffers(buffer);
    }

    public enum State {
        PLAYING(AL_PLAYING),
        STOPPED(AL_STOPPED),
        PAUSED(AL_PAUSED);

        State(int state) {
        }
    }

    public void play() {
        if (state != State.PLAYING) {
            state = State.PLAYING;
            alSourcePlay(id);
        }
    }

    public void pause() {
        if (state == State.PLAYING) {
            state = State.PAUSED;
            alSourcePause(id);
        }
    }

    public void stop() {
        if (state != State.STOPPED) {
            state = State.STOPPED;
            alSourceStop(id);
            alSourcei(id, AL_POSITION, 0);
        }
    }
}
