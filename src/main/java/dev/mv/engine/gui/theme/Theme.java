package dev.mv.engine.gui.theme;

import dev.mv.engine.gui.components.animations.ElementAnimation;
import dev.mv.engine.render.shared.Color;
import dev.mv.engine.render.shared.Gradient;
import dev.mv.engine.render.shared.font.BitmapFont;
import dev.mv.engine.resources.Resource;

public class Theme implements Resource {
    //font

    private BitmapFont font;
    private int outlineThickness;
    private boolean outline = false;

    //outline
    private Color baseColor;
    private Color outlineColor;
    private Gradient baseGradient;
    private Gradient outlineGradient;
    private Color text_base;
    private Gradient text_gradient;

    //colors
    private Color extraColor;
    private Color disabledBaseColor;
    private Color disabledOutlineColor;
    private Color disabledTextColor;
    private Color diabledExtraColor;
    private Color indicatorColor;
    private boolean shouldCheckboxUseTextColor = false;
    private boolean shouldChoiceUseTextColor = false;
    private boolean shouldPasswordInputBoxButtonUseTextColor = false;
    private EdgeStyle edgeStyle;
    //--round and triangle
    private int edgeRadius;
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
    private String guiAssetPath = "/assets/mvengine/guiassets.png";
    private int guiAssetsIconWidth;
    private int guiAssetsIconHeight;

    public BitmapFont getFont() {
        return font;
    }

    public void setFont(BitmapFont font) {
        this.font = font;
    }

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

    public Color getBaseColor() {
        return baseColor;
    }

    public void setBaseColor(Color baseColor) {
        this.baseColor = baseColor;
    }

    public Color getOutlineColor() {
        return outlineColor;
    }

    public void setOutlineColor(Color outlineColor) {
        this.outlineColor = outlineColor;
    }

    public Gradient getBaseGradient() {
        return baseGradient;
    }

    public void setBaseGradient(Gradient baseGradient) {
        this.baseGradient = baseGradient;
    }

    public Gradient getOutlineGradient() {
        return outlineGradient;
    }

    public void setOutlineGradient(Gradient outlineGradient) {
        this.outlineGradient = outlineGradient;
    }

    public Color getText_base() {
        return text_base;
    }

    public void setText_base(Color text_base) {
        this.text_base = text_base;
    }

    public Gradient getText_gradient() {
        return text_gradient;
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

    //edges

    public void setExtraColor(Color extraColor) {
        this.extraColor = extraColor;
    }

    public Color getDiabledExtraColor() {
        return diabledExtraColor;
    }

    public void setDiabledExtraColor(Color diabledExtraColor) {
        this.diabledExtraColor = diabledExtraColor;
    }

    public boolean isShouldCheckboxUseTextColor() {
        return shouldCheckboxUseTextColor;
    }

    public void setShouldCheckboxUseTextColor(boolean shouldCheckboxUseTextColor) {
        this.shouldCheckboxUseTextColor = shouldCheckboxUseTextColor;
    }

    public boolean isShouldChoiceUseTextColor() {
        return shouldChoiceUseTextColor;
    }

    public void setShouldChoiceUseTextColor(boolean shouldChoiceUseTextColor) {
        this.shouldChoiceUseTextColor = shouldChoiceUseTextColor;
    }

    //animations

    public boolean isShouldPasswordInputBoxButtonUseTextColor() {
        return shouldPasswordInputBoxButtonUseTextColor;
    }

    public void setShouldPasswordInputBoxButtonUseTextColor(boolean shouldPasswordInputBoxButtonUseTextColor) {
        this.shouldPasswordInputBoxButtonUseTextColor = shouldPasswordInputBoxButtonUseTextColor;
    }

    public EdgeStyle getEdgeStyle() {
        return edgeStyle;
    }

    public void setEdgeStyle(EdgeStyle edgeStyle) {
        this.edgeStyle = edgeStyle;
    }

    public int getEdgeRadius() {
        return edgeRadius;
    }

    public void setEdgeRadius(int edgeRadius) {
        this.edgeRadius = edgeRadius;
    }

    public ElementAnimation getButtonAnimator() {
        return buttonAnimator;
    }

    public void setAnimator(ElementAnimation buttonAnimator) {
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

    //assets

    public int getAnimationOutTime() {
        return animationOutTime;
    }

    public void setAnimationOutTime(int animationOutTime) {
        this.animationOutTime = animationOutTime;
    }

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

    public enum EdgeStyle {
        ROUND("round", 2),
        TRIANGLE("triangle", 1),
        SQUARE("square", 0);

        int i;

        EdgeStyle(String s, int i) {
            this.i = i;
        }

        public int toInt() {
            return i;
        }

    }
}
