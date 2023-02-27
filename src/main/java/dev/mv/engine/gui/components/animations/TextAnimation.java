package dev.mv.engine.gui.components.animations;

import dev.mv.engine.render.shared.Color;
import dev.mv.utils.Utils;

import java.util.Random;

public abstract class TextAnimation {
    public static TextAnimation CHROMA;

    static {
        CHROMA = new TextAnimation() {
            Random random = new Random();

            @Override
            public TextState animateChar(String text, char c, int charIndex, long callIndex, TextState lastState) {
                int mod = (int) (charIndex % 5 + callIndex % 5);
                mod = Utils.overlap(0, mod, 4);
                if (mod == 0) lastState.color = Color.CYAN;
                if (mod == 1) lastState.color = Color.MAGENTA;
                if (mod == 2) lastState.color = Color.YELLOW;
                if (mod == 3) lastState.color = Color.GREEN;
                if (mod == 4) lastState.color = Color.RED;
                return lastState;
            }
        };

        CHROMA.setTimeBetweenChars(0.1f);
    }

    private float timeBetweenChars = 100f;

    public abstract TextState animateChar(String text, char c, int charIndex, long callIndex, TextState lastState);

    public float getTimeBetweenChars() {
        return timeBetweenChars;
    }

    public void setTimeBetweenChars(float secs) {
        this.timeBetweenChars = secs;
    }

    public static class TextState {
        public int height, x, y;
        public Color color;
        public char content;

        public void copyValuesTo(TextAnimation.TextState dest) {
            if (dest.color != null) color.copyValuesTo(dest.color);
        }
    }
}
