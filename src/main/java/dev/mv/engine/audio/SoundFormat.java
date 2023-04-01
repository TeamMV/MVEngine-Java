package dev.mv.engine.audio;

import java.io.InputStream;

public interface SoundFormat {
    Sound.Raw load(InputStream stream, String name);
}
