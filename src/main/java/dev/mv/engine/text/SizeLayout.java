package dev.mv.engine.text;

import lombok.Getter;
import lombok.Setter;

public class SizeLayout {
    @Setter
    private BitmapFont font = null;
    @Setter
    private String text = "";
    private int height = 0;
    @Getter
    private float multiplier = 0f;

    public SizeLayout(BitmapFont font, String text, int height) {
        this.font = font;
        this.height = height;
        this.text = text;
        this.multiplier = (float) height / (float) font.getDefaultHeight();
    }

    public SizeLayout() {

    }

    public static int getWidth(String s, int height, BitmapFont font) {
        int res = 0;
        for (int i = 0; i < s.length(); i++) {
            res += getWidth(s.charAt(i), height, font);
        }
        return res;
    }

    public static int getWidth(char c, int height, BitmapFont font) {
        if (c <= 31) return -1;
        float multiplier = (float) height / (float) font.getDefaultHeight();
        if (c == ' ') return (int) ((font.getHeight('.')) * multiplier);
        return (int) ((font.getWidth(c) + font.getSpacing()) * multiplier);
    }

    public int getWidth() {
        return getWidth(text);
    }

    public int getWidth(String s) {
        int res = 0;
        for (int i = 0; i < s.length(); i++) {
            res += getWidth(s.charAt(i));
        }
        return res;
    }

    public int getWidth(char c) {
        if (c <= 31) return -1;
        if (c == ' ') return (int) ((getHeight('.')) * multiplier);
        return (int) ((font.getWidth(c) + font.getSpacing()) * multiplier);
    }

    public int getHeight() {
        return (int) (font.getDefaultHeight() * multiplier);
    }

    public SizeLayout setHeight(int height) {
        this.height = height;
        calculate();
        return this;
    }

    public int getHeight(char c) {
        if (c <= 32) return -1;
        return (int) (font.getHeight(c) * multiplier);
    }

    public float getXOffset(char c) {
        if (c <= 32) return -1;
        return font.getGlyph(c).getxOff() * multiplier;
    }

    public float getYOffset(char c) {
        if (c <= 32) return -1;
        return font.getGlyph(c).getyOff() * multiplier;
    }

    public SizeLayout set(BitmapFont font, String text, int height) {
        this.font = font;
        this.height = height;
        this.text = text;
        this.multiplier = (float) height / (float) font.getDefaultHeight();
        return this;
    }

    public SizeLayout set(BitmapFont font, String text) {
        this.font = font;
        this.text = text;
        this.height = font.getDefaultHeight();
        this.multiplier = (float) height / (float) font.getDefaultHeight();
        return this;
    }

    public SizeLayout calculate() {
        this.multiplier = (float) height / (float) font.getDefaultHeight();
        return this;
    }
}
