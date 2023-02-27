package dev.mv.engine.gui.components.animations;

import dev.mv.engine.render.shared.Color;
import dev.mv.engine.render.shared.font.BitmapFont;
import dev.mv.utils.async.PromiseNull;

public class TextAnimator {
    private TextAnimation.TextState[] states;
    private TextAnimation animation = null;
    private long iterationCount = 0;
    private boolean cancel = false;
    private int charIndex = 0;
    private String text;
    private int height = 0, x = 0, y = 0;

    public TextAnimator() {

    }

    public TextAnimator(int height, int x, int y) {
        this.height = height;
        this.x = x;
        this.y = y;
    }

    public void setAnimation(TextAnimation animation) {
        this.animation = animation;
    }

    public boolean isAnimationsSet() {
        return animation != null;
    }

    public void apply(String text) {
        states = new TextAnimation.TextState[text.length()];
        updateStates();
        for (int i = 0; i < text.length(); i++) {
            states[i].content = text.charAt(i);
        }
        this.text = text;
    }

    public void animateOnce() {
        start(1);
    }

    public void startInfinite() {
        start(-1);
    }

    public void start(int times) {
        stop();
        cancel = false;
        new PromiseNull((res, rej) -> {
            while (times <= iterationCount && !cancel) {
                if (text == null) continue;
                iterationCount++;
                charIndex = 0;
                for (int i = 0; i < text.length(); i++) {
                    states[charIndex] = animation.animateChar(text, text.charAt(charIndex), charIndex, iterationCount, states[charIndex]);
                    charIndex++;
                }
                try {
                    Thread.sleep((long) (animation.getTimeBetweenChars() * 1000f));
                } catch (InterruptedException e) {
                    rej.reject(e);
                }
            }
            iterationCount = 0;
        });
    }

    public void stop() {
        iterationCount = 0;
        cancel = true;
        charIndex = 0;
    }

    public boolean isAnimating() {
        return iterationCount != 0;
    }

    public TextAnimation.TextState[] getStates() {
        return states;
    }

    public int getTotalWidth(BitmapFont font) {
        int res = 0;
        for (TextAnimation.TextState state : states) {
            res += font.getWidth(state.content + "", state.height);
        }
        return res;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
        updateStates();
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
        updateStates();
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
        updateStates();
    }

    private void updateStates() {
        for (int i = 0; i < states.length; i++) {
            if (states[i] == null)
                states[i] = new TextAnimation.TextState();
            if (text != null)
                states[i].height = height;
            states[i].y = y;
            states[i].x = x + i;
            if (states[i].color == null)
                states[i].color = new Color(255, 255, 255, 255);
        }
    }
}
