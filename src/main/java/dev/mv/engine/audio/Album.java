package dev.mv.engine.audio;

import java.util.ArrayList;
import java.util.List;

public class Album {
    private List<Music> pieces;

    public Album() {
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
