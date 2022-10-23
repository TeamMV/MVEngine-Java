package dev.mv.engine.oldRender.animation;

import dev.mv.engine.oldRender.texture.TextureRegion;
import dev.mv.engine.oldRender.utils.VariablePosition;
import dev.mv.engine.oldRender.window.Window;

import java.util.ArrayList;
import java.util.List;

public class Animation {
    private List<TextureRegion> frames = new ArrayList<TextureRegion>();
    private int speed = 0, currentFrame = 0;
    private boolean playing;

    private long millis;

    public Animation(TextureRegion... frames) {
        for (TextureRegion frame : frames) {
            this.frames.add(frame);
        }

        millis = System.currentTimeMillis();
    }

    public Animation(TextureRegion[] frames, int start, int end) {
        for (int i = start; i < end + 1; i++) {
            this.frames.add(frames[i]);
        }

        millis = System.currentTimeMillis();
    }

    public void addFrame(TextureRegion tex) {
        frames.add(tex);
    }

    public Animation speed(int millis) {
        speed = millis;
        return this;
    }

    public Animation play() {
        playing = true;
        return this;
    }

    public Animation stop() {
        playing = false;
        return this;
    }

    public int getCurrentIndex() {
        return currentFrame;
    }

    public TextureRegion getFrame(int index) {
        return frames.get(index);
    }

    public TextureRegion getCurrentFrame() {
        return frames.get(currentFrame);
    }

    public TextureRegion getPlayingFrame() {
        if (playing) {
            if (millis + speed <= System.currentTimeMillis()) {
                currentFrame++;
                millis = System.currentTimeMillis();
                if (currentFrame >= frames.size()) {
                    currentFrame = 0;
                }
            }
            return frames.get(currentFrame);
        }
        return frames.get(currentFrame);
    }

    public TextureRegion nextFrame() {
        if (currentFrame++ >= frames.size() - 1) currentFrame = 0;
        return frames.get(currentFrame);
    }

    public void skipFrames(int amount) {
        if (currentFrame + amount >= frames.size() - 1) {
            int extra = amount - (frames.size() - 1 - currentFrame);
            currentFrame = 0;
            currentFrame += extra;
        } else {
            currentFrame += amount;
        }
    }

    public void draw(Window w, VariablePosition position, float rotation) {
        draw(w, position.getX(), position.getY(), position.getWidth(), position.getHeight(), rotation);
    }

    public void draw(Window w, int x, int y, int width, int height, float rotation) {
        if (playing) {
            w.draw.image(x, y, width, height, frames.get(currentFrame), rotation);

            if (millis + speed <= System.currentTimeMillis()) {
                currentFrame++;
                millis = System.currentTimeMillis();
                if (currentFrame >= frames.size()) {
                    currentFrame = 0;
                }
            }
        } else {
            w.draw.image(x, y, width, height, frames.get(currentFrame), rotation);
        }
    }
}
