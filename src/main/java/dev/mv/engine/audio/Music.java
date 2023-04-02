package dev.mv.engine.audio;

import static org.lwjgl.openal.AL11.*;

public final class Music extends Sound {

    private String path;
    private float nextOffset, startingOffset;

    Music(Audio audio, String path) {
        super(audio, path, false);
        this.path = path;
    }

    public void terminate() {
        if (getState() != State.STOPPED) stop();
    }

    public int play() {
        load();
        alID = audio.nextFreeSource();
        if (alID == -1) return -1;
        alSourcei(alID, AL_BUFFER, buffer);
        alSourcei(alID, AL_LOOPING, 0);
        alSourcef(alID, AL_GAIN, volume);
        if (startingOffset > 0 && nextOffset == 0) {
            alSourcef(alID, AL_SEC_OFFSET, startingOffset);
        } else if (nextOffset > 0) {
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
            unload();
        }
    }

    public void rewind() {
        setOffset(0);
    }

    public void setOffset(float seconds) {
        if (alID == -1) {
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
