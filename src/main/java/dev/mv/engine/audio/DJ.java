package dev.mv.engine.audio;

import dev.mv.engine.resources.R;
import dev.mv.utils.async.PromiseNull;
import dev.mv.utils.collection.Vec;

import java.util.Arrays;

import static dev.mv.utils.Utils.*;

public class DJ {
    volatile boolean forceStopped;
    volatile Music playing;
    volatile PromiseNull listener;
    volatile Vec<String> queued = new Vec<>();
    int songIndex = 0;
    boolean loop, shuffle;
    private final Audio audio;

    DJ(Audio audio) {
        this.audio = audio;
    }

    public void play(String id) {
        forceStopped = false;
        if (id == null) return;
        Music music = R.music.get(id);
        if (playing != null) {
            if (playing.getState() == Sound.State.PAUSED) {
                resume();
                return;
            }
            playing.stop();
            playing = null;
        }
        if (listener != null) {
            await(listener);
            listener = null;
        }
        playing = music;
        music.play();
        listen();
    }

    private void listen() {
        listener = async(() -> {
            while (playing != null && playing.getState() != Sound.State.STOPPED) {
                idle(1000);
            }
            playing = null;
            async(this::tryNext);
        });
    }

    private void tryNext() {
        if (forceStopped) return;
        safeNext();
    }

    private void safeNext() {
        if (queued.len() > songIndex) {
            play(queued.get(songIndex++));
        } else if (loop) {
            songIndex = 0;
            if (shuffle) {
                shuffleQueue();
            }
            if (queued.len() > songIndex) {
                play(queued.get(songIndex++));
            }
        }
    }

    public void pause() {
        if (playing == null) return;
        playing.pause();
    }

    public void resume() {
        if (playing == null) return;
        if (playing.getState() == Sound.State.PAUSED) {
            playing.play();
        }
    }

    public void stop() {
        forceStopped = true;
        if (playing == null) return;
        playing.stop();
        playing = null;
        queued.clear();
        loop = false;
    }

    public void skip() {
        if (playing != null) {
            playing.stop();
            playing = null;
            await(listener);
        }
        safeNext();
    }

    public void shuffleQueue() {
        //TODO
    }

    public Album createAlbum(String... songs) {
        return new Album(Arrays.asList(songs));
    }

    public void loop(String... songs) {
        loop(createAlbum(songs));
    }

    public void loopShuffle(String... songs) {
        loopShuffle(createAlbum(songs));
    }

    public void loop(Album album) {
        stop();
        songIndex = 0;
        queued.clear();
        queued.append(album.getSongs());
        safeNext();
        loop = true;
        shuffle = false;
    }

    public void loopShuffle(Album album) {
        loop(album);
        shuffle = true;
    }
}
