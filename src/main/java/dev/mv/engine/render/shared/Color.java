package dev.mv.engine.render.shared;

import dev.mv.engine.exceptions.Exceptions;
import dev.mv.engine.resources.Resource;
import dev.mv.utils.ByteUtils;

public class Color implements Resource {
    public static Color WHITE = new Color(255, 255, 255, 255);
    public static Color BLACK = new Color(0, 0, 0, 255);
    public static Color RED = new Color(255, 0, 0, 255);
    public static Color GREEN = new Color(0, 255, 0, 255);
    public static Color BLUE = new Color(0, 0, 255, 255);
    public static Color YELLOW = new Color(255, 255, 0, 255);
    public static Color MAGENTA = new Color(255, 0, 255, 255);
    public static Color CYAN = new Color(0, 255, 255, 255);

    byte r, g, b, a;

    public Color(byte r, byte g, byte b, byte a) {
        this.r = r;
        this.g = g;
        this.b = b;
        this.a = a;
    }

    public Color(int r, int g, int b, int a) {
        this.r = (byte) r;
        this.g = (byte) g;
        this.b = (byte) b;
        this.a = (byte) a;
    }

    public Color(int color) {
        byte[] col = ByteUtils.toBytes(color);
        this.r = col[0];
        this.g = col[1];
        this.b = col[2];
        this.a = col[3];
    }

    public static Color parse(String string) {
        if (string.startsWith("#")) {
            string = string.replaceAll("#", "");
            if (!string.matches("-?[0-9a-fA-F]+")) {
                Exceptions.send(new IllegalStateException("Color parser: # colors must be hexadecimal characters!"));
            }
            String[] colors = string.split("(?<=\\G.{2})");
            if (colors.length < 3 || colors.length > 4) {
                Exceptions.send(new IllegalStateException("Color parser: # colors must contain 6 or 8 characters!"));
            }
            int r = Integer.parseInt(colors[0], 16);
            int g = Integer.parseInt(colors[1], 16);
            int b = Integer.parseInt(colors[2], 16);
            int a = 255;
            if (colors.length == 4) {
                a = Integer.parseInt(colors[3], 16);
            }
            return new Color(r, g, b, a);
        } else if (string.startsWith("0x")) {
            string = string.replaceAll("0x", "");
            if (!string.matches("-?[0-9a-fA-F]+")) {
                Exceptions.send(new IllegalStateException("Color parser: 0x colors must be hexadecimal characters!"));
            }
            String[] colors = string.split("(?<=\\G.{2})");
            if (colors.length < 3 || colors.length > 4) {
                Exceptions.send(new IllegalStateException("Color parser: 0x colors must contain 6 or 8 characters!"));
            }
            int r = Integer.parseInt(colors[0], 16);
            int g = Integer.parseInt(colors[1], 16);
            int b = Integer.parseInt(colors[2], 16);
            int a = 255;
            if (colors.length == 4) {
                a = Integer.parseInt(colors[3], 16);
            }
            return new Color(r, g, b, a);
        } else {
            String split = ",";
            if (string.contains(" ") && string.contains(",")) {
                string = string.replaceAll(" ", "");
            } else if (string.contains(" ")) {
                split = " ";
            }
            String[] colors = string.replaceAll(" ", "").split(split);
            if (colors.length < 3 || colors.length > 4) {
                Exceptions.send(new IllegalStateException("Color parser: colors must contain 3 or 4 sets of numbers!"));
            }
            int r = Integer.parseInt(colors[0]);
            int g = Integer.parseInt(colors[1]);
            int b = Integer.parseInt(colors[2]);
            int a = 255;
            if (colors.length == 4) {
                a = Integer.parseInt(colors[3]);
            }
            return new Color(r, g, b, a);
        }
    }

    public Color set(byte r, byte g, byte b, byte a) {
        this.r = r;
        this.g = g;
        this.b = b;
        this.a = a;
        return this;
    }

    public Color set(int r, int g, int b, int a) {
        this.r = (byte) r;
        this.g = (byte) g;
        this.b = (byte) b;
        this.a = (byte) a;
        return this;
    }

    public Color set(int color) {
        set(color >> 24 & 0xff, color >> 16 & 0xff, color >> 8 & 0xff, color & 0xff);
        return this;
    }

    public int getRed() {
        return ByteUtils.unsign(r);
    }

    public void setRed(byte r) {
        this.r = r;
    }

    public void setRed(int r) {
        this.r = (byte) r;
    }

    public int getGreen() {
        return ByteUtils.unsign(g);
    }

    public void setGreen(byte g) {
        this.g = g;
    }

    public void setGreen(int g) {
        this.g = (byte) g;
    }

    public int getBlue() {
        return ByteUtils.unsign(b);
    }

    public void setBlue(byte b) {
        this.b = b;
    }

    public void setBlue(int b) {
        this.b = (byte) b;
    }

    public int getAlpha() {
        return ByteUtils.unsign(a);
    }

    public void setAlpha(byte a) {
        this.a = a;
    }

    public void setAlpha(int a) {
        this.a = (byte) a;
    }

    public NormalizedColor normalize() {
        return new NormalizedColor(getRed() / 255.0f, getGreen() / 255.0f, getBlue() / 255.0f, getAlpha() / 255.0f);
    }

    public Color copy() {
        return new Color(r, g, b, a);
    }

    public void copyValuesTo(Color dest) {
        dest.setRed(getRed());
        dest.setGreen(getGreen());
        dest.setBlue(getBlue());
        dest.setAlpha(getAlpha());
    }

    @Override
    public String toString() {
        return "Color{" +
            "r=" + ByteUtils.unsign(r) +
            ", g=" + ByteUtils.unsign(g) +
            ", b=" + ByteUtils.unsign(b) +
            ", a=" + ByteUtils.unsign(a) +
            '}';
    }

    public Color toRGB(float hue, float saturation, float value) {
        final int h = (int) hue / 60;
        final float f = hue / 60 - h;
        final float p = value * (1 - saturation);
        final float q = value * (1 - f * saturation);
        final float t = value * (1 - (1 - f) * saturation);

        float[] rgb = switch (h) {
            case 0 -> new float[]{value, t, p};
            case 1 -> new float[]{q, value, p};
            case 2 -> new float[]{p, value, t};
            case 3 -> new float[]{p, q, value};
            case 4 -> new float[]{t, p, value};
            case 5, 6 -> new float[]{value, p, q};
            default -> throw new IllegalStateException();
        };
        byte r = (byte) (rgb[0] * 255);
        byte g = (byte) (rgb[1] * 255);
        byte b = (byte) (rgb[2] * 255);
        return set(r, g, b, 255);
    }

    public void copyFrom(Color other) {
        set(other.getRed(), other.getGreen(), other.getBlue(), other.getAlpha());
    }

    public int toInt() {
        return ByteUtils.intFromBytes(r, g, b, a);
    }
}
