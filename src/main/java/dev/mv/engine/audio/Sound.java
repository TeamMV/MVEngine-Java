package dev.mv.engine.audio;

import dev.mv.engine.exceptions.Exceptions;
import dev.mv.engine.game.mod.loader.ModIntegration;
import dev.mv.engine.resources.HeavyResource;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

import static org.lwjgl.openal.AL11.*;

public sealed class Sound implements HeavyResource permits Music {

    protected Audio audio;
    protected boolean loop;
    protected State state;
    protected float volume = 0.3f;
    protected String path;
    protected boolean loaded;
    int alID, id, buffer;

    Sound(Audio audio, String path, boolean loop) {
        this.audio = audio;
        this.loop = loop;
        this.path = path;
    }

    @Override
    public void load() {
        if (loaded) return;
        buffer = alGenBuffers();
        try (InputStream stream = ModIntegration.getResourceAsStream(path)) {
            SoundFormat format = audio.getFormat(stream);
            Raw data = format.load(stream, path);
            alBufferData(buffer, data.channels > 1 ? AL_FORMAT_STEREO16 : AL_FORMAT_MONO16, data.bytes, data.sampleRate);
            loaded = true;
        } catch (IOException e) {
            alDeleteBuffers(buffer);
            Exceptions.send(e);
        }
    }

    @Override
    public void unload() {
        if (getState() != State.STOPPED) stop();
        alDeleteBuffers(buffer);
        loaded = false;
    }

    public int play() {
        checkLoad();
        alID = audio.nextFreeSource();
        if (alID == -1) return -1;
        alSourcei(alID, AL_BUFFER, buffer);
        alSourcei(alID, AL_LOOPING, loop ? 0 : 1);
        alSourcef(alID, AL_GAIN, volume);
        getState();
        if (state != State.PLAYING) {
            state = State.PLAYING;
            alSourcePlay(alID);
        }
        return audio.bind(this);
    }

    public void pause() {
        if (alID == -1) return;
        getState();
        if (state == State.PLAYING) {
            state = State.PAUSED;
            alSourcePause(alID);
        }
    }

    public void stop() {
        if (alID == -1) return;
        getState();
        if (state != State.STOPPED) {
            state = State.STOPPED;
            alSourceStop(alID);
            alSourcei(alID, AL_BUFFER, 0);
            audio.freeSource(alID);
            alID = -1;
            audio.unbind(id);
        }
    }

    public State getState() {
        if (alID == -1) return State.STOPPED;
        state = State.valueOf(alGetSourcei(alID, AL_SOURCE_STATE));
        return state;
    }

    private void checkLoad() {
        if (!loaded) Exceptions.send(new IllegalStateException("Sound not loaded"));
    }

    public boolean isLooping() {
        return loop;
    }

    public void setLooping(boolean loop) {
        this.loop = loop;
    }

    public float getVolume() {
        return volume;
    }

    public void setVolume(float volume) {
        this.volume = volume;
    }

    public enum State {
        PLAYING(AL_PLAYING),
        STOPPED(AL_STOPPED),
        PAUSED(AL_PAUSED);

        State(int state) {
        }

        public static State valueOf(int val) {
            return switch (val) {
                case AL_PLAYING -> PLAYING;
                case AL_STOPPED -> STOPPED;
                case AL_PAUSED -> PAUSED;
                default -> null;
            };
        }
    }

    record Raw(ByteBuffer bytes, int channels, int sampleRate) {
    }
}
