package dev.mv.engine.audio;

import dev.mv.engine.exceptions.Exceptions;
import dev.mv.engine.exceptions.IllegalAudioFormatException;
import org.lwjgl.BufferUtils;
import org.lwjgl.openal.AL;
import org.lwjgl.openal.ALC;
import org.lwjgl.openal.ALCCapabilities;
import org.lwjgl.openal.ALCapabilities;

import java.io.IOException;
import java.io.InputStream;
import java.nio.Buffer;
import java.nio.FloatBuffer;
import java.util.Arrays;

import static org.lwjgl.openal.AL11.*;
import static org.lwjgl.openal.ALC11.*;

public class Audio {

    private static final byte[] FORM = new byte[]{0x46, 0x4F, 0x52, 0x4D};
    private static final byte[] AIFF = new byte[]{0x41, 0x49, 0x46, 0x46};

    private static final byte[] RIFF = new byte[]{0x52, 0x49, 0x46, 0x46};
    private static final byte[] WAVE = new byte[]{0x57, 0x41, 0x56, 0x45};
    private static final byte[] AVI_ = new byte[]{0x41, 0x56, 0x49, 0x20};

    private static final byte[] OGGS = new byte[]{0x4F, 0x67, 0x67, 0x53};

    private static final byte[] AU = new byte[]{0x2E, 0x73, 0x6E, 0x64};

    private static final byte[][] MP3_2 = new byte[][]{new byte[]{(byte) 0xFF, (byte) 0xFB}, new byte[]{(byte) 0xFF, (byte) 0xF3}, new byte[]{(byte) 0xFF, (byte) 0xF2}};
    private static final byte[] MP3_3 = new byte[]{0x49, 0x44, 0x33};

    private static final byte[][] AAC = new byte[][]{new byte[]{(byte) 0xFF, (byte) 0xF1}, new byte[]{(byte) 0xFF, (byte) 0xF1}};

    private static volatile Audio instance;

    private long device, context;
    private int[] sources;
    private int[] freeSources;
    private Sound[] bound;
    private String deviceName;
    private ALCapabilities capabilities;
    private DJ DJ;

    private SoundFormat ogg = new Ogg();
    private SoundFormat wav = new Wav();
    private SoundFormat mp3 = new Mp3();

    private Audio(int simultaneousSources) {
        deviceName = alcGetString(0, ALC_DEFAULT_DEVICE_SPECIFIER);
        device = alcOpenDevice(deviceName);
        if (device == 0L) Exceptions.send("AUDIO_INIT", "couldn't open device");
        ALCCapabilities alcCapabilities = ALC.createCapabilities(device);
        context = alcCreateContext(device, new int[]{0});
        if (context == 0L) {
            alcCloseDevice(device);
            Exceptions.send("AUDIO_INIT", "couldn't create context");
        }
        if (!alcMakeContextCurrent(context)) Exceptions.send("AUDIO_INIT", "couldn't make context current");
        capabilities = AL.createCapabilities(alcCapabilities);

        if (!capabilities.OpenAL11) Exceptions.send("AUDIO_INIT", "OpenAL not supported by your computer");
        if (alGetError() != AL_NO_ERROR) Exceptions.send("AUDIO_INIT", "error when creating capabilities");
        sources = new int[simultaneousSources];
        for (int i = 0; i < sources.length; i++) {
            sources[i] = alGenSources();
            if (alGetError() != AL_NO_ERROR) Exceptions.send("AUDIO_INIT", "couldn't generate source");
        }
        freeSources = Arrays.copyOf(sources, sources.length);
        bound = new Sound[simultaneousSources];
        DJ = new DJ(this);

        FloatBuffer orientation = BufferUtils.createFloatBuffer(6)
            .put(new float[]{0.0f, 0.0f, -1.0f, 0.0f, 1.0f, 0.0f});
        ((Buffer) orientation).flip();
        alListenerfv(AL_ORIENTATION, orientation);
        FloatBuffer velocity = BufferUtils.createFloatBuffer(3).put(new float[]{0.0f, 0.0f, 0.0f});
        ((Buffer) velocity).flip();
        alListenerfv(AL_VELOCITY, velocity);
        FloatBuffer position = BufferUtils.createFloatBuffer(3).put(new float[]{0.0f, 0.0f, 0.0f});
        ((Buffer) position).flip();
        alListenerfv(AL_POSITION, position);
    }

    public static synchronized Audio init(int simultaneousSources) {
        if (instance != null) Exceptions.send(new IllegalStateException("Audio already initialized"));
        instance = new Audio(simultaneousSources);
        return instance;
    }

    SoundFormat getFormat(InputStream stream) {
        try {
            stream.mark(12);
            byte[] magic = stream.readNBytes(12);
            stream.reset();
            byte[] four = Arrays.copyOf(magic, 4);
            if (Arrays.equals(four, RIFF)) {
                byte[] cont = Arrays.copyOfRange(magic, 8, 12);
                if (Arrays.equals(cont, WAVE)) return wav;
                if (Arrays.equals(cont, AVI_)) {
                    Exceptions.send(new IllegalAudioFormatException("AVI is not supported yet!"));
                    return null;
                }
                Exceptions.send(new IllegalAudioFormatException("Unknown audio format!"));
            } else if (Arrays.equals(four, FORM)) {
                byte[] cont = Arrays.copyOfRange(magic, 8, 12);
                if (Arrays.equals(cont, AIFF)) return wav;
                Exceptions.send(new IllegalAudioFormatException("Unknown audio format!"));
            } else if (Arrays.equals(four, OGGS)) {
                return ogg;
            } else if (Arrays.equals(four, AU)) {
                return wav;
            }

            byte[] two = Arrays.copyOf(magic, 2);
            for (byte[] mp3Magic : MP3_2) {
                if (Arrays.equals(two, mp3Magic)) return mp3;
            }
            for (byte[] aacMagic : AAC) {
                if (Arrays.equals(two, aacMagic)) {
                    Exceptions.send(new IllegalAudioFormatException("AAC is not supported yet!"));
                }
            }

            byte[] three = Arrays.copyOf(magic, 3);
            if (Arrays.equals(three, MP3_3)) {
                return mp3;
            }

        } catch (IOException e) {
            Exceptions.send(e);
        }
        Exceptions.send(new IllegalAudioFormatException("Unknown audio format!"));
        return null;
    }

    public Sound newSound(String path) {
        return newSound(path, false);
    }

    public Sound newSound(String path, boolean loop) {
        return new Sound(this, path, loop);
    }

    public Music newMusic(String path) {
        return new Music(this, path);
    }

    int nextFreeSource() {
        for (int i = 0; i < freeSources.length; i++) {
            if (freeSources[i] != -1) {
                int source = freeSources[i];
                freeSources[i] = -1;
                return source;
            }
        }
        return -1;
    }

    void freeSource(int source) {
        for (int i = 0; i < freeSources.length; i++) {
            if (freeSources[i] == -1) {
                freeSources[i] = source;
                return;
            }
        }
    }

    int bind(Sound sound) {
        if (sound.alID == -1) return -1;
        for (int i = 0; i < bound.length; i++) {
            if (bound[i] == null) {
                bound[i] = sound;
                sound.id = i;
                return i;
            }
        }
        return -1;
    }

    void unbind(int id) {
        bound[id].id = -1;
        bound[id] = null;
    }

    public int getSimultaneousSources() {
        return sources.length;
    }

    public DJ getDJ() {
        return DJ;
    }

    public synchronized void terminate() {
        for (int i = 0; i < sources.length; i++) {
            int id = sources[i];
            if (alGetSourcei(id, AL_SOURCE_STATE) != AL_STOPPED) {
                alSourceStop(id);
            }
            alDeleteSources(id);
        }
        alcDestroyContext(context);
        alcCloseDevice(device);
        instance = null;
    }

}
