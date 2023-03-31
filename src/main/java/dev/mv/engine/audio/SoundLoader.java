package dev.mv.engine.audio;

import java.io.InputStream;

public interface SoundLoader {
    Sound.Raw load(InputStream stream);
}
