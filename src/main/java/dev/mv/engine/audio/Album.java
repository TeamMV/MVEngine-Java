package dev.mv.engine.audio;

import dev.mv.engine.resources.Resource;

import java.util.ArrayList;
import java.util.List;

public class Album implements Resource {
    private List<String> songs;

    public Album() {
        songs = new ArrayList<>();
    }

    public Album(List<String> songs) {
        this.songs = songs;
    }

    public void addMusic(String music) {
        songs.add(music);
    }

    public void remove(String music) {
        songs.remove(music);
    }

    public List<String> getSongs() {
        return songs;
    }
}
