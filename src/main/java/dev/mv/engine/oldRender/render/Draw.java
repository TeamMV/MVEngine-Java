package dev.mv.engine.oldRender.render;

import dev.mv.engine.oldRender.animation.Animation;
import dev.mv.engine.oldRender.exception.ExceptionHandler;
import dev.mv.engine.oldRender.text.BitmapFont;
import dev.mv.engine.oldRender.text.SizeLayout;
import dev.mv.engine.oldRender.texture.Texture;
import dev.mv.engine.oldRender.texture.TextureRegion;
import dev.mv.engine.oldRender.window.Window;
import org.joml.Vector2f;

public class Draw {

    public final static int CAMERA_DYNAMIC = 0, CAMERA_STATIC = 1;
    private float r = 0.0f, g = 0.0f, b = 0.0f, a = 1.0f;
    private float currentCamMode = CAMERA_DYNAMIC;

    private Vertices verts = new Vertices();
    private Vertex v1 = new Vertex(), v2 = new Vertex(), v3 = new Vertex(), v4 = new Vertex();

    private SizeLayout layout = new SizeLayout();

    public Draw(int w, int h, Window win) {
        BatchController.init(win, 1000);
    }

    public void color(int r, int g, int b, int a) {
        this.r = r / 255.0f;
        this.g = g / 255.0f;
        this.b = b / 255.0f;
        this.a = a / 255.0f;
    }

    public void mode(int mode) {
        if (mode < 0 && mode > 1) {
            ExceptionHandler.throwNew(new IllegalArgumentException("Camera mode cannot be smaller than 0 or bigger than 1!"));
        }

        currentCamMode = (float) mode;
    }

    public void rectangle(int x, int y, int width, int height) {
        float ax = x;
        float ay = y;
        float ax2 = x + width;
        float ay2 = y + height;

        BatchController.addVertices(verts.set(
            v1.put(ax, ay2, 0.0f, 0.0f, r, g, b, a, 0.0f, 0.0f, 0.0f, currentCamMode, 0.0f, 0.0f),
            v2.put(ax, ay, 0.0f, 0.0f, r, g, b, a, 0.0f, 0.0f, 0.0f, currentCamMode, 0.0f, 0.0f),
            v3.put(ax2, ay, 0.0f, 0.0f, r, g, b, a, 0.0f, 0.0f, 0.0f, currentCamMode, 0.0f, 0.0f),
            v4.put(ax2, ay2, 0.0f, 0.0f, r, g, b, a, 0.0f, 0.0f, 0.0f, currentCamMode, 0.0f, 0.0f)
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
            v1.put(ax, ay2, 0.0f, radRotation, r, g, b, a, 0.0f, 0.0f, 0.0f, currentCamMode, (float) originX, (float) originY),
            v2.put(ax, ay, 0.0f, radRotation, r, g, b, a, 0.0f, 0.0f, 0.0f, currentCamMode, (float) originX, (float) originY),
            v3.put(ax2, ay, 0.0f, radRotation, r, g, b, a, 0.0f, 0.0f, 0.0f, currentCamMode, (float) originX, (float) originY),
            v4.put(ax2, ay2, 0.0f, radRotation, r, g, b, a, 0.0f, 0.0f, 0.0f, currentCamMode, (float) originX, (float) originY)
        ));
    }

    public void triangle(int x1, int y1, int x2, int y2, int x3, int y3) {

        BatchController.addVertices(verts.set(
            v1.put(x1, y1, 0.0f, 0.0f, r, g, b, a, 0.0f, 0.0f, 0.0f, currentCamMode, 0.0f, 0.0f),
            v2.put(x2, y2, 0.0f, 0.0f, r, g, b, a, 0.0f, 0.0f, 0.0f, currentCamMode, 0.0f, 0.0f),
            v3.put(x3, y3, 0.0f, 0.0f, r, g, b, a, 0.0f, 0.0f, 0.0f, currentCamMode, 0.0f, 0.0f)
        ));
    }

    public void image(int x, int y, int width, int height, Texture tex) {
        image(x, y, width, height, tex, 0f, 0, 0);
    }

    public void image(int x, int y, int width, int height, Texture tex, float rotation) {
        image(x, y, width, height, tex, rotation, x + width / 2, y + height / 2);
    }

    public void image(int x, int y, int width, int height, Texture tex, float rotation, int originX, int originY) {
        float ax = x;
        float ay = y;
        float ax2 = x + width;
        float ay2 = y + height;

        float radRotation = (float) (rotation * (Math.PI / 180));

        int texID = BatchController.addTexture(tex);

        BatchController.addVertices(verts.set(
            v1.put(ax, ay2, 0.0f, radRotation, r, g, b, a, 0.0f, 0.0f, (float) texID, currentCamMode, (float) originX, (float) originY),
            v2.put(ax, ay, 0.0f, radRotation, r, g, b, a, 0.0f, 1.0f, (float) texID, currentCamMode, (float) originX, (float) originY),
            v3.put(ax2, ay, 0.0f, radRotation, r, g, b, a, 1.0f, 1.0f, (float) texID, currentCamMode, (float) originX, (float) originY),
            v4.put(ax2, ay2, 0.0f, radRotation, r, g, b, a, 1.0f, 0.0f, (float) texID, currentCamMode, (float) originX, (float) originY)
        ));
    }

    public void image(int x, int y, int width, int height, TextureRegion tex) {
        image(x, y, width, height, tex, 0f, 0, 0);
    }

    public void image(int x, int y, int width, int height, TextureRegion tex, float rotation) {
        image(x, y, width, height, tex, rotation, x + width / 2, y + height / 2);
    }

    public void image(int x, int y, int width, int height, TextureRegion tex, float rotation, int originX, int originY) {
        float ax = x;
        float ay = y;
        float ax2 = x + width;
        float ay2 = y + height;

        float ux0 = tex.getUV()[0];
        float ux1 = tex.getUV()[1];
        float uy1 = tex.getUV()[2];
        float uy0 = tex.getUV()[3];

        float radRotation = (float) (rotation * (Math.PI / 180));

        int texID = BatchController.addTexture(tex.getTexture());

        BatchController.addVertices(verts.set(
            v1.put(ax, ay2, 0.0f, radRotation, r, g, b, a, ux0, uy0, (float) texID, currentCamMode, (float) originX, (float) originY),
            v2.put(ax, ay, 0.0f, radRotation, r, g, b, a, ux0, uy1, (float) texID, currentCamMode, (float) originX, (float) originY),
            v3.put(ax2, ay, 0.0f, radRotation, r, g, b, a, ux1, uy1, (float) texID, currentCamMode, (float) originX, (float) originY),
            v4.put(ax2, ay2, 0.0f, radRotation, r, g, b, a, ux1, uy0, (float) texID, currentCamMode, (float) originX, (float) originY)
        ));
    }

    public void image(int x, int y, int width, int height, Animation anim) {
        image(x, y, width, height, anim.getPlayingFrame(), 0f, 0, 0);
    }

    public void image(int x, int y, int width, int height, Animation anim, float rotation) {
        image(x, y, width, height, anim.getPlayingFrame(), rotation, x + width / 2, y + height / 2);
    }

    public void image(int x, int y, int width, int height, Animation anim, float rotation, int originX, int originY) {
        image(x, y, width, height, anim.getPlayingFrame(), rotation, originX, originY);
    }

    public void rectangleFromTo(int ax, int ay, int bx, int by, int thickness) {
        int w = (bx - ax);
        int h = (by - ay);

        float alpha = (float) (Math.atan2(bx - ax, by - ay - thickness / 2f) * (180 / Math.PI)) - 90f;
        int width = (int) Math.sqrt((w * w) + (h * h));

        rectangle(ax, ay, width, thickness, alpha, ax, ay + thickness / 2);
    }

    public void imageFromTo(int ax, int ay, int bx, int by, int thickness, Texture tex) {
        int w = (bx - ax);
        int h = (by - ay);

        float alpha = (float) (Math.atan2(bx - ax, by - ay - thickness / 2f) * (180 / Math.PI)) - 90f;
        int width = (int) Math.sqrt((w * w) + (h * h));

        image(ax, ay, width, thickness, tex, alpha, ax, ay + thickness / 2);
    }

    public void imageFromTo(int ax, int ay, int bx, int by, int thickness, TextureRegion tex) {
        int w = (bx - ax);
        int h = (by - ay);

        float alpha = (float) (Math.atan2(bx - ax, by - ay - thickness / 2f) * (180 / Math.PI)) - 90f;
        int width = (int) Math.sqrt((w * w) + (h * h));

        image(ax, ay, width, thickness, tex, alpha, ax, ay + thickness / 2);
    }


    public void text(int x, int y, int height, String s, BitmapFont font) {

        int charX = 0;

        layout.set(font, "", height);

        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);

            if (c <= 31) continue;

            float ax = x + charX + layout.getXOffset(c);
            float ay = y - (layout.getHeight(c) + layout.getYOffset(c)) + layout.getHeight();
            float ax2 = x + charX + layout.getWidth(c) + layout.getXOffset(c);
            float ay2 = y + layout.getHeight(c) - (layout.getHeight(c) + layout.getYOffset(c)) + layout.getHeight();

            charX += layout.getWidth(c);

            Vector2f[] uvs = font.getUV(c);
            float ux0 = uvs[0].x;
            float ux1 = uvs[1].x;
            float uy1 = uvs[0].y;
            float uy0 = uvs[1].y;

            int texID = BatchController.addTexture(font.getBitmap());

            BatchController.addVertices(verts.set(
                v1.put(ax, ay2, 0.0f, 0.0f, r, g, b, a, ux0, uy0, (float) texID, currentCamMode, 0.0f, 0.0f),
                v2.put(ax, ay, 0.0f, 0.0f, r, g, b, a, ux0, uy1, (float) texID, currentCamMode, 0.0f, 0.0f),
                v3.put(ax2, ay, 0.0f, 0.0f, r, g, b, a, ux1, uy1, (float) texID, currentCamMode, 0.0f, 0.0f),
                v4.put(ax2, ay2, 0.0f, 0.0f, r, g, b, a, ux1, uy0, (float) texID, currentCamMode, 0.0f, 0.0f)
            ));
        }
    }

    public void draw() {
        BatchController.finishAndRender();
        currentCamMode = (float) CAMERA_DYNAMIC;
    }

    private float convertCoords(int val, int fullVal) {
        return (float) val / (float) fullVal * 2.0f - 1.0f;
    }
}