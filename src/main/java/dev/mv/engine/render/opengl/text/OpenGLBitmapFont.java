package dev.mv.engine.render.opengl.text;

import dev.mv.engine.MVEngine;
import dev.mv.engine.render.drawables.Texture;
import dev.mv.engine.render.drawables.text.BitmapFont;
import dev.mv.engine.render.drawables.text.Glyph;
import dev.mv.engine.render.opengl.texture.OpenGLTexture;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class OpenGLBitmapFont implements BitmapFont {
    private Map<Integer, OpenGLGlyph> chars;
    private OpenGLTexture bitmap;
    private int maxWidth = 0, maxHeight = 0, maxXOff = 0, maxYOff = 0;
    private int spacing = 0;

    public OpenGLBitmapFont(String pngFileStream, String fntFileStream) {

        try {
            bitmap = loadTexture(pngFileStream);
            chars = createCharacters(fntFileStream);
        } catch (IOException e) {
            MVEngine.Exceptions.Throw(e);
        }
    }

    private OpenGLTexture loadTexture(String pngFileStream) {
        BufferedImage img = null;
        try {
            img = ImageIO.read(this.getClass().getResourceAsStream(pngFileStream));
        } catch (IOException e) {
            MVEngine.Exceptions.Throw(e);
        }

        if (img == null) {
            return null;
        }
        return new OpenGLTexture(img);
    }

    private Map<Integer, OpenGLGlyph> createCharacters(String fntFileStream) throws IOException {
        BufferedReader reader = null;
        Map<Integer, OpenGLGlyph> map = new HashMap<>();
        reader = new BufferedReader(new InputStreamReader(this.getClass().getResourceAsStream(fntFileStream)));

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

            OpenGLGlyph glyph = new OpenGLGlyph(
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

        for (OpenGLGlyph glyph : map.values()) {
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

    @Override
    public int getSpacing() {
        return (int) (maxWidth / 10f);
    }

    @Override
    public int getMaxHeight() {
        return maxHeight;
    }

    @Override
    public int getMaxHeight(int height) {
        return (int) (maxHeight * multiplier(height));
    }

    @Override
    public int getHeight(char c) {
        try {
            return chars.get(c + 0).getHeight();
        } catch (NullPointerException e) {
            MVEngine.Exceptions.Throw(new IllegalArgumentException("Character '" + c + "' not supported by this font!"));
            return -1;
        }
    }

    @Override
    public int getWidth(char c) {
        try {
            return chars.get(c + 0).getWidth();
        } catch (NullPointerException e) {
            MVEngine.Exceptions.Throw(new IllegalArgumentException("Character '" + c + "' not supported by this font!"));
            return -1;
        }
    }

    @Override
    public int getWidth(String s) {
        int result = 0;

        for (char c : s.toCharArray()) {
            result += getWidth(c) + getGlyph(c).getXAdvance();
        }
        result -= getGlyph('a').getXAdvance();

        return result;
    }

    @Override
    public int getMaxXOffset() {
        return maxXOff;
    }

    @Override
    public int getMaxXOffset(int height) {
        return (int) (maxXOff * multiplier(height));
    }

    @Override
    public int getMaxYOffset() {
        return maxYOff;
    }

    @Override
    public int getMaxYOffset(int height) {
        System.out.println((int) (maxYOff));
        return (int) (maxYOff * multiplier(height));
    }

    @Override
    public Glyph getGlyph(char c) {
        try {
            return chars.get(c + 0);
        } catch (NullPointerException e) {
            MVEngine.Exceptions.Throw(new IllegalArgumentException("Character '" + c + "' not supported by this font!"));
            return null;
        }
    }

    @Override
    public Glyph[] getGlyphs(String s) {
        Glyph[] glyphs = new OpenGLGlyph[s.length()];

        for (int i = 0; i < s.length(); i++) {
            glyphs[i] = getGlyph(s.charAt(i));
        }

        return glyphs;
    }

    @Override
    public Texture getBitmap() {
        return bitmap;
    }

    private float multiplier(int height) {
        return (float) height / (float) maxHeight;
    }
}
