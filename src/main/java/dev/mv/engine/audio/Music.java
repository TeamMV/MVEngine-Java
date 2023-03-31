package dev.mv.engine.audio;

import dev.mv.engine.game.mod.loader.ModIntegration;

import java.io.InputStream;

import static org.lwjgl.openal.AL11.*;

public final class Music extends Sound {

    private String path;
    private float nextOffset, startingOffset;
    private SoundLoader loader;

    Music(Audio audio, String path, boolean loop, SoundLoader loader) {
        super(audio, loop);
        this.path = path;
        this.loader = loader;
    }

    public void terminate() {
        if (getState() != State.STOPPED) stop();
    }

    public int play() {
        InputStream stream = ModIntegration.getResourceAsStream(path);
        Raw loaded = loader.load(stream);
        buffer = alGenBuffers();
        alBufferData(buffer, loaded.channels() > 1 ? AL_FORMAT_STEREO16 : AL_FORMAT_MONO16, loaded.bytes(), loaded.sampleRate());
        alID = audio.nextFreeSource();
        if (alID == -1) return -1;
        alSourcei(alID, AL_BUFFER, buffer);
        alSourcei(alID, AL_LOOPING, loop ? 0 : 1);
        alSourcef(alID, AL_GAIN, volume);
        if (startingOffset > 0 && nextOffset == 0) {
            alSourcef(alID, AL_SEC_OFFSET, startingOffset);
        }
        else if (nextOffset > 0) {
            alSourcef(alID, AL_SEC_OFFSET, nextOffset);
            nextOffset = 0;
        }
        getState();
        if (state != State.PLAYING) {
            state = State.PLAYING;
            alSourcePlay(alID);
        }
        return audio.bind(this);
    }

    public void stop() {
        if (alID == -1) return;
        getState();
        if (state != Sound.State.STOPPED) {
            state = Sound.State.STOPPED;
            alSourceStop(alID);
            alSourcei(alID, AL_BUFFER, 0);
            audio.freeSource(alID);
            alID = -1;
            audio.unbind(id);
            alDeleteBuffers(buffer);
        }
    }

    public void rewind() {
        setOffset(0);
    }

    public void setOffset(float seconds) {
        if (alID == -1)  {
            nextOffset = seconds;
            return;
        }
        boolean wasPlaying = getState() == State.PLAYING;
        alSourceStop(alID);
        alSourcef(alID, AL_SEC_OFFSET, seconds);
        if (wasPlaying) {
            alSourcePlay(alID);
        }
    }

    public float getStartingOffset() {
        return startingOffset;
    }

    public void setStartingOffset(float offset) {
        startingOffset = offset;
    }
}
