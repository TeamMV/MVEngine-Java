package dev.mv.engine.audio;

import dev.mv.engine.exceptions.Exceptions;
import org.lwjgl.system.MemoryUtil;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import java.io.InputStream;

public class Wav implements SoundFormat {
    @Override
    public Sound.Raw load(InputStream stream, String name) {
        try {
            AudioInputStream audio = AudioSystem.getAudioInputStream(stream);
            AudioFormat format = audio.getFormat();
            byte[] bytes = audio.readAllBytes();
            return new Sound.Raw(MemoryUtil.memAlloc(bytes.length).put(bytes).flip(), format.getChannels(), (int) format.getSampleRate());
        } catch (Exception e) {
            Exceptions.send("BROKEN_AUDIO_FILE", name);
        }
        return null;
    }
}
