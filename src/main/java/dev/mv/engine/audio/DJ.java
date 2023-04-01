package dev.mv.engine.audio;

import dev.mv.utils.Utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DJ {

    private Album playlist;
    private volatile boolean interrupted = false;

    public DJ(Album playlist) {
        this.playlist = playlist;
    }

    public Album getPlaylist() {
        return playlist;
    }

    public void setPlaylist(Album playlist) {
        this.playlist = playlist;
        interrupted = true;
    }

    public void shuffle() {
        Collections.shuffle(playlist.getPieces());
    }

    public void playAll() {
        Utils.async(() -> {
           for (Music music : playlist.getPieces()) {
               music.play();
               while (music.getState() != Sound.State.STOPPED) {
                   Utils.await(Utils.sleep(1000));
               }
           }
        });
    }

    public void interrupt() {
        interrupted = true;
    }

    public void playInfiniteOrdered() {
        Utils.async(() -> {
            while(!interrupted) {
                for (Music music : playlist.getPieces()) {
                    music.play();
                    while (music.getState() != Sound.State.STOPPED) {
                        Utils.await(Utils.sleep(1000));
                    }
                }
            }
            interrupted = false;
        });
    }

    public void playInfiniteRandom() {
        Utils.async(() -> {
            while(!interrupted) {
                for (Music music : playlist.getPieces()) {
                    music.play();
                    while (music.getState() != Sound.State.STOPPED) {
                        Utils.await(Utils.sleep(1000));
                    }
                }
                shuffle();
            }
            interrupted = false;
        });
    }
}
