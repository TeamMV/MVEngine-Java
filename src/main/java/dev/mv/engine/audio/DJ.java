package dev.mv.engine.audio;

import dev.mv.utils.Utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DJ {
    public static class Playlist {
        private List<Music> pieces;
        
        public Playlist() {
            pieces = new ArrayList<>();
        }

        public void addMusic(Music music) {
            pieces.add(music);
        }

        public void remove(Music music) {
            pieces.remove(music);
        }

        private List<Music> getPieces() {
            return pieces;
        }
    }

    private Playlist playlist;
    private volatile boolean interrupted = false;

    public DJ(Playlist playlist) {
        this.playlist = playlist;
    }

    public Playlist getPlaylist() {
        return playlist;
    }

    public void setPlaylist(Playlist playlist) {
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
