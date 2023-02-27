package dev.mv.engine.gui.components.animations;

import dev.mv.engine.gui.components.Element;
import dev.mv.utils.async.PromiseNull;

public class ElementAnimator {

    private boolean shouldAnimate;
    private volatile int currentFrame = 0;
    private boolean shouldAnimateBack;
    private ElementAnimation.AnimationState state;
    private ElementAnimation animation;
    private Runnable onFinish;
    private boolean isWorking = false;
    private Element target;

    public ElementAnimator(Element target) {
        this.target = target;
    }

    public ElementAnimation getAnimation() {
        return animation;
    }

    public void setAnimation(ElementAnimation animation) {
        this.animation = animation;
    }

    public void setOnFinish(Runnable onFinish) {
        this.onFinish = onFinish;
    }

    public void animate(int milliseconds, int frameCount) {
        shouldAnimate = true;
        shouldAnimateBack = false;
        isWorking = true;
        new PromiseNull((res, rej) -> {
            while (currentFrame <= frameCount && shouldAnimate) {
                currentFrame++;
                try {
                    Thread.sleep(milliseconds / frameCount);
                } catch (InterruptedException e) {
                    rej.reject(e);
                }
            }
        });
    }

    public void animateBack(int milliseconds, int frameCount) {
        target.getInitialState().copyValuesTo(state);
        shouldAnimateBack = true;
        shouldAnimate = false;

        new PromiseNull((res, rej) -> {
            while (currentFrame >= 0 && shouldAnimateBack) {
                currentFrame--;
                try {
                    Thread.sleep(milliseconds / frameCount);
                } catch (InterruptedException e) {
                    rej.reject(e);
                }
            }
        });
    }

    public void loop(int frameCount) {
        if (shouldAnimate) {
            if (animation != null) {
                state = animation.transform(currentFrame, frameCount, state);
            }
            if (currentFrame >= frameCount) {
                shouldAnimate = false;
            }
        }
        if (shouldAnimateBack) {
            if (animation != null) {
                state = animation.transformBack(currentFrame, frameCount, state);
            }
            if (currentFrame <= 0) {
                shouldAnimateBack = false;
                isWorking = false;
                if (onFinish != null) {
                    onFinish.run();
                }
            }
        }
    }

    public ElementAnimation.AnimationState getState() {
        return state;
    }

    public void setState(ElementAnimation.AnimationState state) {
        this.state = state;
    }

    public boolean isAnimating() {
        return isWorking;
    }
}
