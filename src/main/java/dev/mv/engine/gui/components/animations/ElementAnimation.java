package dev.mv.engine.gui.components.animations;

import dev.mv.engine.render.shared.Color;

public interface ElementAnimation {
    public static class AnimationState{
        public int width, height;
        public int rotation;
        public int originX, originY;
        public int posX, posY;
        public Color baseColor, outlineColor, textColor, extraColor;
        public String text;

        public void copyValuesTo(AnimationState dest) {
            dest.width = width;
            dest.height = height;
            dest.rotation = rotation;
            dest.originX = originX;
            dest.originY = originY;
            dest.posX = posX;
            dest.posY = posY;
            if(dest.baseColor != null) baseColor.copyValuesTo(dest.baseColor);
            if(dest.outlineColor != null) outlineColor.copyValuesTo(dest.outlineColor);
            if(dest.textColor != null) textColor.copyValuesTo(dest.textColor);
            if(dest.extraColor != null) extraColor.copyValuesTo(dest.extraColor);
            dest.text = text;
        }
    }
    AnimationState transform(int frame, int totalFrames, AnimationState lastState);
    AnimationState transformBack(int frame, int totalFrames, AnimationState lastState);
}
