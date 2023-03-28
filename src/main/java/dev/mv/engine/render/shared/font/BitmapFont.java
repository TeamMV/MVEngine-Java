package dev.mv.engine.render.shared.font;

import dev.mv.engine.exceptions.Exceptions;
import dev.mv.engine.render.shared.create.RenderBuilder;
import dev.mv.engine.render.shared.texture.Texture;
import dev.mv.engine.resources.Resource;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BitmapFont implements Resource {
    private Map<Integer, Glyph> chars;
    private Texture bitmap;
    private int maxWidth = 0, maxHeight = 0, maxXOff = 0, maxYOff = 0;
    private int spacing = 0;

    public BitmapFont(String pngFileStream, String fntFileStream) throws IOException {
        bitmap = loadTexture(pngFileStream);
        chars = createCharacters(fntFileStream);
    }

    private Texture loadTexture(String pngFileStream) throws IOException {
        BufferedImage img = null;
        try {
            img = ImageIO.read(this.getClass().getResourceAsStream(pngFileStream));
        } catch (IOException | NullPointerException e) {
            throw new IOException("PNG-File not found!");
        }

        if (img == null) {
            return null;
        }
        return RenderBuilder.newTexture(img);
    }

    private Map<Integer, Glyph> createCharacters(String fntFileStream) throws IOException {
        BufferedReader reader = null;
        Map<Integer, Glyph> map = new HashMap<>();
        try {
            reader = new BufferedReader(new InputStreamReader(this.getClass().getResourceAsStream(fntFileStream)));
        } catch (NullPointerException e) {
            throw new IOException("FNT-File not found!");
        }

        if (reader == null) {
            return null;
        }

        int totalChars = -1;
        int lineHeight = -1;
        int atlasWidth = 1;
        int atlasHeight = 1;

        while (totalChars == -1) {
            String line = reader.readLine();
            if (line.contains("common ")) {
                lineHeight = Integer.parseInt(getCharAttrib(line, "lineHeight"));
                atlasWidth = Integer.parseInt(getCharAttrib(line, "scaleW"));
                atlasHeight = Integer.parseInt(getCharAttrib(line, "scaleH"));
            }
            if (line.contains("chars ")) {
                totalChars = Integer.parseInt(getCharAttrib(line, "count"));
            }
        }

        for (int i = 0; i < totalChars; i++) {
            String line = reader.readLine();
            maxWidth = Math.max(maxWidth, Integer.parseInt(getCharAttrib(line, "width")));
            maxHeight = Math.max(maxHeight, Integer.parseInt(getCharAttrib(line, "height")));
            maxXOff = Math.max(maxXOff, Integer.parseInt(getCharAttrib(line, "xoffset")));
            maxYOff = Math.max(maxYOff, Integer.parseInt(getCharAttrib(line, "yoffset")));

            Glyph glyph = new Glyph(
                Integer.parseInt(getCharAttrib(line, "x")),
                Integer.parseInt(getCharAttrib(line, "y")),
                Integer.parseInt(getCharAttrib(line, "width")),
                Integer.parseInt(getCharAttrib(line, "height")),
                Integer.parseInt(getCharAttrib(line, "xoffset")),
                Integer.parseInt(getCharAttrib(line, "yoffset")),
                Integer.parseInt(getCharAttrib(line, "xadvance"))
            );

            map.put(Integer.parseInt(getCharAttrib(line, "id")), glyph);
        }

        for (Glyph glyph : map.values()) {
            glyph.makeCoordinates(atlasWidth, atlasHeight, maxHeight);
        }

        return map;
    }

    private String getCharAttrib(String line, String name) {
        Pattern pattern = Pattern.compile("\s+");
        Matcher matcher = pattern.matcher(line);
        line = matcher.replaceAll(" ");
        String[] attribs = line.split(" ");

        for (String s : attribs) {
            if (s.contains(name)) {
                return s.split("=")[1];
            }
        }

        return "";
    }

    public int getSpacing() {
        return (int) (maxWidth / 10f);
    }

    public int getMaxHeight() {
        return maxHeight;
    }

    public int getMaxHeight(int height) {
        return (int) (maxHeight * multiplier(height));
    }

    public int getHeight(char c) {
        try {
            return chars.get(c + 0).getHeight();
        } catch (NullPointerException e) {
            Exceptions.send(new IllegalArgumentException("Character '" + c + "' not supported by this font!"));
            return -1;
        }
    }

    private int getWidth(char c) {
        try {
            return (int) (chars.get(c + 0).getWidth());
        } catch (NullPointerException e) {
            Exceptions.send(new IllegalArgumentException("Character '" + c + "' not supported by this font!"));
            return -1;
        }
    }

    public int getWidth(String s, int height) {
        if (s.length() == 0) return 0;
        int result = 0;
        float multiplier = multiplier(height);

        for (char c : s.toCharArray()) {
            result += getGlyph(c).getXAdvance() * multiplier;
        }
        char last = s.charAt(s.length() - 1);
        if (last != '\s') {
            result -= (getGlyph(last).getXAdvance() - getWidth(last)) * multiplier;
        }

        return result;
    }

    public int possibleAmountOfChars(String s, int limitWidth, int height) {
        for (int i = 0; i <= s.length(); i++) {
            if (limitWidth < getWidth(s.substring(s.length() - i), height)) {
                return i - 1;
            }
        }
        return s.length();
    }

    public int getMaxXOffset() {
        return maxXOff;
    }

    public int getMaxXOffset(int height) {
        return (int) (maxXOff * multiplier(height));
    }

    public int getMaxYOffset() {
        return maxYOff;
    }

    public int getMaxYOffset(int height) {
        return (int) (maxYOff * multiplier(height));
    }

    public Glyph getGlyph(char c) {
        try {
            return chars.get(c + 0);
        } catch (NullPointerException e) {
            Exceptions.send(new IllegalArgumentException("Character '" + c + "' not supported by this font!"));
            return null;
        }
    }

    public Glyph[] getGlyphs(String s) {
        Glyph[] glyphs = new Glyph[s.length()];

        for (int i = 0; i < s.length(); i++) {
            glyphs[i] = getGlyph(s.charAt(i));
        }

        return glyphs;
    }

    public boolean contains(char c) {
        return chars.containsKey(c + 0);
    }

    public Texture getBitmap() {
        return bitmap;
    }

    private float multiplier(int height) {
        return (float) height / (float) maxHeight;
    }
}
