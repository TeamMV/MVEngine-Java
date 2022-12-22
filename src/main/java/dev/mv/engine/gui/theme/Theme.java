package dev.mv.engine.gui.theme;

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

    public void setOutline(boolean outline) {
        this.outline = outline;
    }

    //colors

    public Normal normal;
    public Hover hover;
    private EdgeStyle edgeStyle;

    public Theme(Normal normal, Hover hover) {
        this.normal = normal;
        this.hover = hover;
    }

    public static class Normal {
        private Color base;
        private Color outline;
        private Gradient baseGradient;
        private Gradient outlineGradient;

        private Color text_base;
        private Gradient text_gradient;

        public Normal(Color base, Color outline, Gradient baseGradient, Gradient outlineGradient, Color text_base, Gradient text_gradient) {
            this.base = base;
            this.outline = outline;
            this.baseGradient = baseGradient;
            this.outlineGradient = outlineGradient;
            this.text_base = text_base;
            this.text_gradient = text_gradient;
        }

        public Color getBase() {
            return base;
        }

        public Color getOutline() {
            return outline;
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
    }

    public class Hover {
        private Color base;
        private Color outline;
        private Gradient baseGradient;
        private Gradient outlineGradient;

        private Color text_base;
        private Gradient text_gradient;

        public Hover(Color base, Color outline, Gradient baseGradient, Gradient outlineGradient, Color text_base, Gradient text_gradient) {
            this.base = base;
            this.outline = outline;
            this.baseGradient = baseGradient;
            this.outlineGradient = outlineGradient;
            this.text_base = text_base;
            this.text_gradient = text_gradient;
        }

        public Color getBase() {
            return base;
        }

        public Color getOutline() {
            return outline;
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
    }

    public EdgeStyle getEdgeStyle() {
        return edgeStyle;
    }

    public void setEdgeStyle(EdgeStyle edgeStyle) {
        this.edgeStyle = edgeStyle;
    }

    public enum EdgeStyle{
        ROUND,
        TRIANGLE,
        ARC
    }

    //round
    private int edgeRadius;

    public void setEdgeRadius(int edgeRadius) {
        this.edgeRadius = edgeRadius;
    }

    public int getEdgeRadius() {
        return edgeRadius;
    }
}
