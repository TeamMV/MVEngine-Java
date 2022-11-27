package dev.mv.engine.render.shared;

import dev.mv.engine.render.shared.font.BitmapFont;
import dev.mv.engine.render.shared.font.Glyph;
import dev.mv.engine.render.shared.texture.Animation;
import dev.mv.engine.render.shared.texture.Texture;
import dev.mv.engine.render.shared.batch.BatchController;
import dev.mv.engine.render.shared.batch.Vertex;
import dev.mv.engine.render.shared.batch.VertexGroup;
import dev.mv.engine.render.shared.texture.TextureRegion;
import org.joml.Vector2f;

import java.awt.Color;

public class DrawContext2D {

    private float r = 0.0f, g = 0.0f, b = 0.0f, a = 1.0f;
    private BitmapFont font;
    private VertexGroup verts = new VertexGroup();
    private Vertex v1 = new Vertex(), v2 = new Vertex(), v3 = new Vertex(), v4 = new Vertex();

    public DrawContext2D(Window window) {
        BatchController.init(window, 1000);
    }

    public DrawContext2D(Window window, int batchLimit) {
        BatchController.init(window, batchLimit);
    }


    public void color(int r, int g, int b, int a) {
        this.r = r / 255.0f;
        this.g = g / 255.0f;
        this.b = b / 255.0f;
        this.a = a / 255.0f;
    }

    public void color(Color color) {
        color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
    }

    public void font(BitmapFont font) {
        this.font = font;
    }


    public void triangle(int x1, int y1, int x2, int y2, int x3, int y3) {
        BatchController.addVertices(verts.set(
            v1.put(x1, y1, 0.0f, 0.0f, 0.0f, 0.0f, r, g, b, a, 0.0f, 0.0f, 0.0f),
            v2.put(x2, y2, 0.0f, 0.0f, 0.0f, 0.0f, r, g, b, a, 0.0f, 0.0f, 0.0f),
            v3.put(x3, y3, 0.0f, 0.0f, 0.0f, 0.0f, r, g, b, a, 0.0f, 0.0f, 0.0f)
        ));
    }

    public void rectangle(int x, int y, int width, int height) {
        float ax = x;
        float ay = y;
        float ax2 = x + width;
        float ay2 = y + height;

        BatchController.addVertices(verts.set(
            v1.put(ax, ay2, 0.0f, 0.0f, 0.0f, 0.0f, r, g, b, a, 0.0f, 0.0f, 0.0f),
            v2.put(ax, ay, 0.0f, 0.0f, 0.0f, 0.0f, r, g, b, a, 0.0f, 0.0f, 0.0f),
            v3.put(ax2, ay, 0.0f, 0.0f, 0.0f, 0.0f, r, g, b, a, 0.0f, 0.0f, 0.0f),
            v4.put(ax2, ay2, 0.0f, 0.0f, 0.0f, 0.0f, r, g, b, a, 0.0f, 0.0f, 0.0f)
        ));
    }

    public void rectangle(int x, int y, int width, int height, float rotation) {
        rectangle(x, y, width, height, rotation, x + width / 2, y + height / 2);
    }

    public void rectangle(int x, int y, int width, int height, float rotation, int originX, int originY) {
        float ax = x;
        float ay = y;
        float ax2 = x + width;
        float ay2 = y + height;

        float radRotation = (float) (rotation * (Math.PI / 180));

        BatchController.addVertices(verts.set(
            v1.put(ax, ay2, 0.0f, radRotation, (float) originX, (float) originY, r, g, b, a, 0.0f, 0.0f, 0.0f),
            v2.put(ax, ay, 0.0f, radRotation, (float) originX, (float) originY, r, g, b, a, 0.0f, 0.0f, 0.0f),
            v3.put(ax2, ay, 0.0f, radRotation, (float) originX, (float) originY, r, g, b, a, 0.0f, 0.0f, 0.0f),
            v4.put(ax2, ay2, 0.0f, radRotation, (float) originX, (float) originY, r, g, b, a, 0.0f, 0.0f, 0.0f)
        ));
    }

    public void line(int x1, int y1, int x2, int y2, int depth) {
        int w = (x2 - x1);
        int h = (y2 - y1);

        float alpha = (float) (Math.atan2(x2 - x1, y2 - y1 - depth / 2f) * (180 / Math.PI)) - 90f;
        int width = (int) Math.sqrt((w * w) + (h * h));

        rectangle(x1, y1, width, depth, alpha, x1, y1 + depth / 2);
    }

    public void image(int x, int y, int width, int height, Texture texture) {
        image(x, y, width, height, texture, 0f, 0, 0);
    }

    public void image(int x, int y, int width, int height, TextureRegion texture) {
        image(x, y, width, height, texture, 0f, 0, 0);
    }

    public void image(int x, int y, int width, int height, Texture texture, float rotation) {
        image(x, y, width, height, texture, rotation, x + width / 2, y + height / 2);
    }

    public void image(int x, int y, int width, int height, TextureRegion texture, float rotation) {
        image(x, y, width, height, texture, rotation, x + width / 2, y + height / 2);
    }

    public void image(int x, int y, int width, int height, Texture texture, float rotation, int originX, int originY) {
        float ax = x;
        float ay = y;
        float ax2 = x + width;
        float ay2 = y + height;

        float radRotation = (float) (rotation * (Math.PI / 180));

        int texID = BatchController.addTexture(texture);

        BatchController.addVertices(verts.set(
            v1.put(ax, ay2, 0.0f, radRotation, (float) originX, (float) originY, r, g, b, a, 0.0f, 0.0f, (float) texID),
            v2.put(ax, ay, 0.0f, radRotation, (float) originX, (float) originY, r, g, b, a, 0.0f, 1.0f, (float) texID),
            v3.put(ax2, ay, 0.0f, radRotation, (float) originX, (float) originY, r, g, b, a, 1.0f, 1.0f, (float) texID),
            v4.put(ax2, ay2, 0.0f, radRotation, (float) originX, (float) originY, r, g, b, a, 1.0f, 0.0f, (float) texID)
        ));
    }

    public void image(int x, int y, int width, int height, TextureRegion texture, float rotation, int originX, int originY) {
        float ax = x;
        float ay = y;
        float ax2 = x + width;
        float ay2 = y + height;

        float ux0 = texture.getUVCoordinates()[0];
        float ux1 = texture.getUVCoordinates()[1];
        float uy1 = texture.getUVCoordinates()[2];
        float uy0 = texture.getUVCoordinates()[3];

        float radRotation = (float) (rotation * (Math.PI / 180));

        int texID = BatchController.addTexture(texture.getParentTexture());

        BatchController.addVertices(verts.set(
            v1.put(ax, ay2, 0.0f, radRotation, (float) originX, (float) originY, r, g, b, a, ux0, uy0, (float) texID),
            v2.put(ax, ay, 0.0f, radRotation, (float) originX, (float) originY, r, g, b, a, ux0, uy1, (float) texID),
            v3.put(ax2, ay, 0.0f, radRotation, (float) originX, (float) originY, r, g, b, a, ux1, uy1, (float) texID),
            v4.put(ax2, ay2, 0.0f, radRotation, (float) originX, (float) originY, r, g, b, a, ux1, uy0, (float) texID)
        ));
    }

    public void imageFromTo(int x1, int y1, int x2, int y2, int depth, Texture texture) {
        int w = (x2 - x1);
        int h = (y2 - y1);

        float alpha = (float) (Math.atan2(y2 - y1, y2 - y1 - depth / 2f) * (180 / Math.PI)) - 90f;
        int width = (int) Math.sqrt((w * w) + (h * h));

        image(x1, y1, width, depth, texture, alpha, x1, y1 + depth / 2);
    }

    public void imageFromTo(int x1, int y1, int x2, int y2, int depth, TextureRegion texture) {
        int w = (x2 - x1);
        int h = (y2 - y1);

        float alpha = (float) (Math.atan2(y2 - y1, y2 - y1 - depth / 2f) * (180 / Math.PI)) - 90f;
        int width = (int) Math.sqrt((w * w) + (h * h));

        image(x1, y1, width, depth, texture, alpha, x1, y1 + depth / 2);
    }

    public void animation(int x, int y, int width, int height, Animation animation) {

    }

    public void animation(int x, int y, int width, int height, Animation animation, float rotation) {

    }

    public void animation(int x, int y, int width, int height, Animation animation, float rotation, int originX, int originY) {

    }

    public void text(int x, int y, int height, String text) {
        text(x, y, height, text, font);
    }

    public void text(int x, int y, int height, String text, BitmapFont font) {
        int charX = 0;

        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);

            if (c <= 31) continue;

            Glyph glyph = font.getGlyph(c);

            int yOff = glyph.getYOffset(height) - (font.getMaxHeight(height) - glyph.getHeight(height));

            float ax = x + charX + glyph.getXOffset(height);
            float ay = y - yOff;
            float ax2 = x + charX + glyph.getXOffset(height) + glyph.getWidth(height);
            float ay2 = y + glyph.getHeight(height) - yOff;

            charX += glyph.getXAdvance(height);

            Vector2f[] uvs = glyph.getCoordinates();
            float ux0 = uvs[0].x;
            float ux1 = uvs[1].x;
            float uy1 = uvs[0].y;
            float uy0 = uvs[1].y;

            int texID = BatchController.addTexture(font.getBitmap());

            BatchController.addVertices(verts.set(
                v1.put(ax, ay2, 0.0f, 0.0f, 0.0f, 0.0f, r, g, b, a, ux0, uy0, (float) texID),
                v2.put(ax, ay, 0.0f, 0.0f, 0.0f, 0.0f, r, g, b, a, ux0, uy1, (float) texID),
                v3.put(ax2, ay, 0.0f, 0.0f, 0.0f, 0.0f, r, g, b, a, ux1, uy1, (float) texID),
                v4.put(ax2, ay2, 0.0f, 0.0f, 0.0f, 0.0f, r, g, b, a, ux1, uy0, (float) texID)
            ));
        }
    }
}
