package dev.mv.engine.gui.components.animations;

import dev.mv.engine.gui.components.Element;
import dev.mv.utils.Utils;
import dev.mv.utils.async.PromiseNull;

import java.util.concurrent.atomic.AtomicInteger;

public class ElementAnimator {

    private boolean shouldAnimate;
    private final AtomicInteger currentFrame = new AtomicInteger();
    private boolean shouldAnimateBack;
    private ElementAnimation.AnimationState state;
    private ElementAnimation animation;
    private Runnable onFinish;
    private boolean isWorking = false;
    private Element target;

    public ElementAnimator(Element target) {
        this.target = target;
        this.state = new ElementAnimation.AnimationState();
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
            while (currentFrame.get() <= frameCount && shouldAnimate) {
                currentFrame.getAndIncrement();
                Utils.await(Utils.sleep(milliseconds / frameCount));
            }
        });
    }

    public void animateBack(int milliseconds, int frameCount) {
        target.getInitialState().copyValuesTo(state);
        shouldAnimateBack = true;
        shouldAnimate = false;

        new PromiseNull((res, rej) -> {
            while (currentFrame.get() >= 0 && shouldAnimateBack) {
                currentFrame.getAndDecrement();
                Utils.await(Utils.sleep(milliseconds / frameCount));
            }
        });
    }

    public void loop(int frameCount) {
        if (shouldAnimate) {
            if (animation != null) {
                state = animation.transform(currentFrame.get(), frameCount, state);
            }
            if (currentFrame.get() >= frameCount) {
                shouldAnimate = false;
            }
        }
        if (shouldAnimateBack) {
            if (animation != null) {
                state = animation.transformBack(currentFrame.get(), frameCount, state);
            }
            if (currentFrame.get() <= 0) {
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
