package dev.mv.engine.gui.theme;

import dev.mv.engine.render.shared.Color;
import dev.mv.engine.render.shared.Gradient;

public class Theme {
    public Normal normal;
    public Hover hover;

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
}
