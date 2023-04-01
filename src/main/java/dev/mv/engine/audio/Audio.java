package dev.mv.engine.audio;

import dev.mv.engine.exceptions.Exceptions;

import org.lwjgl.BufferUtils;
import org.lwjgl.openal.AL;
import org.lwjgl.openal.ALC;
import org.lwjgl.openal.ALCCapabilities;
import org.lwjgl.openal.ALCapabilities;

import java.nio.Buffer;
import java.nio.FloatBuffer;
import java.util.Arrays;

import static org.lwjgl.openal.AL11.*;
import static org.lwjgl.openal.ALC11.*;

public class Audio {

    private static Audio instance;

    private long device, context;
    private int[] sources;
    private int[] freeSources;
    private Sound[] bound;
    private String deviceName;
    private ALCapabilities capabilities;
    private MusicPlayer musicPlayer;

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
        musicPlayer = new MusicPlayer(this);

        FloatBuffer orientation = BufferUtils.createFloatBuffer(6)
            .put(new float[] {0.0f, 0.0f, -1.0f, 0.0f, 1.0f, 0.0f});
        ((Buffer)orientation).flip();
        alListenerfv(AL_ORIENTATION, orientation);
        FloatBuffer velocity = BufferUtils.createFloatBuffer(3).put(new float[] {0.0f, 0.0f, 0.0f});
        ((Buffer)velocity).flip();
        alListenerfv(AL_VELOCITY, velocity);
        FloatBuffer position = BufferUtils.createFloatBuffer(3).put(new float[] {0.0f, 0.0f, 0.0f});
        ((Buffer)position).flip();
        alListenerfv(AL_POSITION, position);
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

    public MusicPlayer getMusicPlayer() {
        return musicPlayer;
    }

    public static Audio init(int simultaneousSources) {
        if (instance!= null) Exceptions.send(new IllegalStateException("Audio already initialized"));
        instance = new Audio(simultaneousSources);
        return instance;
    }

    public void terminate() {
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
