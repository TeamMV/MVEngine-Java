package dev.mv.engine.gui.theme;

import dev.mv.engine.gui.components.animations.ElementAnimation;
import dev.mv.engine.render.shared.Color;
import dev.mv.engine.render.shared.Gradient;
import dev.mv.engine.render.shared.font.BitmapFont;

public class Theme {
    //font

    private BitmapFont font;

    public BitmapFont getFont() {
        return font;
    }

    public void setFont(BitmapFont font) {
        this.font = font;
    }

    //outline

    private int outlineThickness;
    private boolean outline = false;

    public int getOutlineThickness() {
        return outlineThickness;
    }

    public void setOutlineThickness(int outlineThickness) {
        this.outlineThickness = outlineThickness;
    }

    public boolean hasOutline() {
        return outline;
    }

    public void setHasOutline(boolean outline) {
        this.outline = outline;
    }

    //colors

    private Color baseColor;
    private Color outlineColor;
    private Gradient baseGradient;
    private Gradient outlineGradient;
    private Color text_base;
    private Gradient text_gradient;
    private Color extraColor;
    private Color disabledBaseColor;
    private Color disabledOutlineColor;
    private Color disabledTextColor;
    private Color diabledExtraColor;
    private Color indicatorColor;

    public Color getBaseColor() {
        return baseColor;
    }

    public Color getOutlineColor() {
        return outlineColor;
    }

    public Gradient getBaseGradient() {
        return baseGradient;
    }

    public Gradient getOutlineGradient() {
        return outlineGradient;
    }

    public Color getText_base() {
        return text_base;
    }

    public Gradient getText_gradient() {
        return text_gradient;
    }

    public void setBaseColor(Color baseColor) {
        this.baseColor = baseColor;
    }

    public void setOutlineColor(Color outlineColor) {
        this.outlineColor = outlineColor;
    }

    public void setBaseGradient(Gradient baseGradient) {
        this.baseGradient = baseGradient;
    }

    public void setOutlineGradient(Gradient outlineGradient) {
        this.outlineGradient = outlineGradient;
    }

    public void setText_base(Color text_base) {
        this.text_base = text_base;
    }

    public void setText_gradient(Gradient text_gradient) {
        this.text_gradient = text_gradient;
    }

    public Color getDisabledBaseColor() {
        return disabledBaseColor;
    }

    public void setDisabledBaseColor(Color disabledBaseColor) {
        this.disabledBaseColor = disabledBaseColor;
    }

    public Color getDisabledOutlineColor() {
        return disabledOutlineColor;
    }

    public void setDisabledOutlineColor(Color disabledOutlineColor) {
        this.disabledOutlineColor = disabledOutlineColor;
    }

    public Color getDisabledTextColor() {
        return disabledTextColor;
    }

    public void setDisabledTextColor(Color disabledTextColor) {
        this.disabledTextColor = disabledTextColor;
    }

    public Color getIndicatorColor() {
        return indicatorColor;
    }

    public void setIndicatorColor(Color indicatorColor) {
        this.indicatorColor = indicatorColor;
    }

    public Color getExtraColor() {
        return extraColor;
    }

    public void setExtraColor(Color extraColor) {
        this.extraColor = extraColor;
    }

    public Color getDiabledExtraColor() {
        return diabledExtraColor;
    }

    public void setDiabledExtraColor(Color diabledExtraColor) {
        this.diabledExtraColor = diabledExtraColor;
    }

    //edges

    private EdgeStyle edgeStyle;

    public EdgeStyle getEdgeStyle() {
        return edgeStyle;
    }

    public void setEdgeStyle(EdgeStyle edgeStyle) {
        this.edgeStyle = edgeStyle;
    }

    public enum EdgeStyle{
        ROUND,
        TRIANGLE,
        SQUARE
    }

    //--round and triangle
    private int edgeRadius;

    public void setEdgeRadius(int edgeRadius) {
        this.edgeRadius = edgeRadius;
    }

    public int getEdgeRadius() {
        return edgeRadius;
    }

    //animations

    //--buttons
    private int animationInTime;
    private int animationOutTime;
    private int animationFrames;
    private ElementAnimation buttonAnimator = new ElementAnimation() {
        @Override
        public ElementAnimation.AnimationState transform(int frame, int totalFrames, ElementAnimation.AnimationState lastState) {
            return lastState;
        }

        @Override
        public ElementAnimation.AnimationState transformBack(int frame, int totalFrames, ElementAnimation.AnimationState lastState) {
            return lastState;
        }
    };

    public ElementAnimation getButtonAnimator() {
        return buttonAnimator;
    }

    public void setButtonAnimator(ElementAnimation buttonAnimator) {
        this.buttonAnimator = buttonAnimator;
    }

    public int getAnimationFrames() {
        return animationFrames;
    }

    public void setAnimationFrames(int animationFrames) {
        this.animationFrames = animationFrames;
    }

    public int getAnimationInTime() {
        return animationInTime;
    }

    public void setAnimationInTime(int animationInTime) {
        this.animationInTime = animationInTime;
    }

    public int getAnimationOutTime() {
        return animationOutTime;
    }

    public void setAnimationOutTime(int animationOutTime) {
        this.animationOutTime = animationOutTime;
    }

    //assets

    private String guiAssetPath;
    private int guiAssetsIconWidth;
    private int guiAssetsIconHeight;

    public String getGuiAssetPath() {
        return guiAssetPath;
    }

    public void setGuiAssetPath(String guiAssetPath) {
        this.guiAssetPath = guiAssetPath;
    }

    public int getGuiAssetsIconWidth() {
        return guiAssetsIconWidth;
    }

    public void setGuiAssetsIconWidth(int guiAssetsIconWidth) {
        this.guiAssetsIconWidth = guiAssetsIconWidth;
    }

    public int getGuiAssetsIconHeight() {
        return guiAssetsIconHeight;
    }

    public void setGuiAssetsIconHeight(int guiAssetsIconHeight) {
        this.guiAssetsIconHeight = guiAssetsIconHeight;
    }
}
