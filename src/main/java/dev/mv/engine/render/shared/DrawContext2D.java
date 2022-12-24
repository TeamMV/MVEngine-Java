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

public class DrawContext2D {
    private boolean useCamera;
    private Gradient gradient;
    private BitmapFont font;
    private VertexGroup verts = new VertexGroup();
    private Vertex v1 = new Vertex(), v2 = new Vertex(), v3 = new Vertex(), v4 = new Vertex();

    public DrawContext2D(Window window) {
        BatchController.init(window, 1000);
        gradient = new Gradient();
    }

    public DrawContext2D(Window window, int batchLimit) {
        BatchController.init(window, batchLimit);
        gradient = new Gradient();
    }


    public void color(float r, float g, float b, float a) {
        Color c = new Color(
            r / 255.0f,
            g / 255.0f,
            b / 255.0f,
            a / 255.0f);
        this.gradient.topLeft = c;
        this.gradient.topRight = c;
        this.gradient.bottomLeft = c;
        this.gradient.bottomRight = c;
    }

    public void color(Color color) {
        color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
    }

    public void color(Gradient gradient) {
        this.gradient = gradient.copy().normalize(1.0f);
    }

    public void font(BitmapFont font) {
        this.font = font;
    }

    public void useCamera(boolean useCamera) {
        this.useCamera = useCamera;
    }

    public void triangle(int x1, int y1, int x2, int y2, int x3, int y3) {
        triangle(x1, y1, x2, y2, x3, y3, 0.0f);
    }

    public void triangle(int x1, int y1, int x2, int y2, int x3, int y3, float rotation) {
        triangle(x1, y1, x2, y2, x3, y3, rotation, (x1 + x2 + x3) / 3, (y1 + y2 + y3) / 3);
    }

    public void triangle(int x1, int y1, int x2, int y2, int x3, int y3, float rotation, int originX, int originY) {
        float radRotation = (float) Math.toRadians(rotation);
        BatchController.addVertices(verts.set(
            v1.put(x1, y1, 0.0f, radRotation, (float) originX, (float) originY, gradient.bottomLeft.getRed(), gradient.bottomLeft.getGreen(), gradient.bottomLeft.getBlue(), gradient.bottomLeft.getAlpha(), 0.0f, 0.0f, 0.0f),
            v2.put(x2, y2, 0.0f, radRotation, (float) originX, (float) originY, gradient.topLeft.getRed(), gradient.topLeft.getGreen(), gradient.topLeft.getBlue(), gradient.topLeft.getAlpha(), 0.0f, 0.0f, 0.0f),
            v3.put(x3, y3, 0.0f, radRotation, (float) originX, (float) originY, gradient.topRight.getRed(), gradient.topRight.getGreen(), gradient.topRight.getBlue(), gradient.topRight.getAlpha(), 0.0f, 0.0f, 0.0f)
        ), useCamera);
    }

    public void rectangle(int x, int y, int width, int height) {
        rectangle(x, y, width, height, 0.0f);
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
            v1.put(ax, ay2, 0.0f, radRotation, (float) originX, (float) originY, gradient.bottomLeft.getRed(), gradient.bottomLeft.getGreen(), gradient.bottomLeft.getBlue(), gradient.bottomLeft.getAlpha(), 0.0f, 0.0f, 0.0f),
            v2.put(ax, ay, 0.0f, radRotation, (float) originX, (float) originY, gradient.topLeft.getRed(), gradient.topLeft.getGreen(), gradient.topLeft.getBlue(), gradient.topLeft.getAlpha(), 0.0f, 0.0f, 0.0f),
            v3.put(ax2, ay, 0.0f, radRotation, (float) originX, (float) originY, gradient.topRight.getRed(), gradient.topRight.getGreen(), gradient.topRight.getBlue(), gradient.topRight.getAlpha(), 0.0f, 0.0f, 0.0f),
            v4.put(ax2, ay2, 0.0f, radRotation, (float) originX, (float) originY, gradient.bottomRight.getRed(), gradient.bottomRight.getGreen(), gradient.bottomRight.getBlue(), gradient.bottomRight.getAlpha(), 0.0f, 0.0f, 0.0f)
        ), useCamera);
    }

    public void voidRectangle(int x, int y, int width, int height, int thickness) {
        voidRectangle(x, y, width, height, thickness, 0.0f);
    }

    public void voidRectangle(int x, int y, int width, int height, int thickness, float rotation) {
        voidRectangle(x, y, width, height, thickness, rotation, x + width / 2, y + height / 2);
    }

    public void voidRectangle(int x, int y, int width, int height, int thickness, float rotation, int originX, int originY) {
        rectangle(x, y, width, thickness, rotation, originX, originY);
        rectangle(x, y + thickness, thickness, height - 2 * thickness, rotation, originX, originY);
        rectangle(x, y + height - thickness, width, thickness, rotation, originX, originY);
        rectangle(x + width - thickness, y + thickness, thickness, height - 2 * thickness, rotation, originX, originY);
    }

    public void roundedRectangle(int x, int y, int width, int height, int radius, float precision) {
        roundedRectangle(x, y, width, height, radius, precision, 0.0f);
    }

    public void roundedRectangle(int x, int y, int width, int height, int radius, float precision, float rotation) {
        roundedRectangle(x, y, width, height, radius, precision, rotation, width / 2, height / 2);
    }

    public void roundedRectangle(int x, int y, int width, int height, int radius, float precision, float rotation, int originX, int originY) {
        rectangle(x, y + radius, width, height - 2 * radius, rotation, originX, originY);
        rectangle(x + radius, y, width - 2 * radius, radius, rotation, originX, originY);
        rectangle(x + radius, y + height - radius, width - 2 * radius, radius, rotation, originX, originY);
        arc(x + radius, y + radius, radius, 90, 180, precision, rotation, originX, originY);
        arc(x + radius, y + height - radius, radius, 90, 90, precision, rotation, originX, originY);
        arc(x + width - radius, y + radius, radius, 90, 270, precision, rotation, originX, originY);
        arc(x + width - radius, y + height - radius, radius, 90, 0, precision, rotation, originX, originY);
    }

    public void triangularRectangle(int x, int y, int width, int height, int radius) {
        triangularRectangle(x, y, width, height, radius, 0.0f);
    }

    public void triangularRectangle(int x, int y, int width, int height, int radius, float rotation) {
        triangularRectangle(x, y, width, height, radius, rotation, width / 2, height / 2);
    }

    public void triangularRectangle(int x, int y, int width, int height, int radius, float rotation, int originX, int originY) {
        rectangle(x, y + radius, width, height - 2 * radius, rotation, originX, originY);
        rectangle(x + radius, y, width - 2 * radius, radius, rotation, originX, originY);
        rectangle(x + radius, y + height - radius, width - 2 * radius, radius, rotation, originX, originY);
        triangle(x + radius, y + radius, x, y + radius, x + radius, y, rotation, originX, originY);
        triangle(x, y + height - radius, x + radius, y + height - radius, x + radius, y + height, rotation, originX, originY);
        triangle(x + width - radius, y + height, x + width - radius, y + height - radius, x + width, y + height - radius, rotation, originX, originY);
        triangle(x + width, y + radius, x + width - radius, y + radius, x + width - radius, y, rotation, originX, originY);
    }

    public void voidRoundedRectangle(int x, int y, int width, int height, int thickness, int radius, float precision) {
        voidRoundedRectangle(x, y, width, height, thickness, radius, precision, 0.0f);
    }

    public void voidRoundedRectangle(int x, int y, int width, int height, int thickness, int radius, float precision, float rotation) {
        voidRoundedRectangle(x, y, width, height, thickness, radius, precision, rotation, width / 2, height / 2);
    }

    public void voidRoundedRectangle(int x, int y, int width, int height, int thickness, int radius, float precision, float rotation, int originX, int originY) {
        rectangle(x + radius, y, width - 2 * radius, thickness);
        rectangle(x + radius, y + height - thickness, width - 2 * radius, thickness);
        rectangle(x, y + radius, thickness, height - 2 * radius);
        rectangle(x + width - thickness, y + radius, thickness, height - 2 * radius);
        voidArc(x + radius, y + radius, radius, thickness, 90, 180, precision, rotation, originX, originY);
        voidArc(x + radius, y + height - radius, radius, thickness, 90, 90, precision, rotation, originX, originY);
        voidArc(x + width - radius, y + radius, radius, thickness, 90, 270, precision, rotation, originX, originY);
        voidArc(x + width - radius, y + height - radius, radius, thickness, 90, 0, precision, rotation, originX, originY);
    }

    public void circle(int x, int y, int radius, float precision) {
        circle(x, y, radius, precision, 0.0f);
    }

    public void circle(int x, int y, int radius, float precision, float rotation) {
        circle(x, y, radius, precision, rotation, x, y);
    }

    public void circle(int x, int y, int radius, float precision, float rotation, int originX, int originY) {
        double tau = Math.PI * 2.0;
        double step = tau / precision;
        float radRotation = (float) Math.toRadians(rotation);
        for (double i = 0.0; i < tau; i += step)   {
            BatchController.addVertices(verts.set(
                v1.put((float) (x + (radius * Math.cos(i))), (float) (y + (radius * Math.sin(i))), 0.0f, radRotation, (float) originX, (float) originY, gradient.bottomLeft.getRed(), gradient.bottomLeft.getGreen(), gradient.bottomLeft.getBlue(), gradient.bottomLeft.getAlpha(), 0.0f, 0.0f, 0.0f),
                v2.put((float) (x + (radius * Math.cos(i + step))), (float) (y + (radius * Math.sin(i + step))), 0.0f, radRotation, (float) originX, (float) originY, gradient.bottomLeft.getRed(), gradient.bottomLeft.getGreen(), gradient.bottomLeft.getBlue(), gradient.bottomLeft.getAlpha(), 0.0f, 0.0f, 0.0f),
                v3.put(x, y, 0.0f, radRotation, (float) originX, (float) originY, gradient.bottomLeft.getRed(), gradient.bottomLeft.getGreen(), gradient.bottomLeft.getBlue(), gradient.bottomLeft.getAlpha(), 0.0f, 0.0f, 0.0f)
            ), useCamera);
        }
    }

    public void voidCircle(int x, int y, int radius, int thickness, float precision) {
        voidCircle(x, y, radius, thickness, precision, 0.0f);
    }

    public void voidCircle(int x, int y, int radius, int thickness, float precision, float rotation) {
        voidCircle(x, y, radius, thickness, precision, rotation, x, y);
    }

    public void voidCircle(int x, int y, int radius, int thickness, float precision, float rotation, int originX, int originY) {
        double tau = Math.PI * 2.0;
        double step = tau / precision;
        for (double i = 0.0; i < tau; i += step)   {
            line((int) (x + (radius * Math.cos(i))), (int) (y + (radius * Math.sin(i))), (int) (x + (radius * Math.cos(i + step))), (int) (y + (radius * Math.sin(i + step))), thickness, rotation, originX, originY);
        }
    }

    public void arc(int x, int y, int radius, int range, int start, float precision) {
        arc(x, y, radius, range, start, precision, 0.0f);
    }

    public void arc(int x, int y, int radius, int range, int start, float precision, float rotation) {
        arc(x, y, radius, range, start, precision, rotation, x, y);
    }

    public void arc(int x, int y, int radius, int range, int start, float precision, float rotation, int originX, int originY) {
        double tau = Math.PI * 2.0;
        double rRange = Math.PI * 2.0 - Math.toRadians(range);
        double step = tau / precision;
        float radRotation = (float) Math.toRadians(rotation);
        for (double i = Math.toRadians(start); i < tau - rRange + Math.toRadians(start); i += step)   {
            BatchController.addVertices(verts.set(
                v1.put((float) (x + (radius * Math.cos(i))), (float) (y + (radius * Math.sin(i))), 0.0f, radRotation, (float) originX, (float) originY, gradient.bottomLeft.getRed(), gradient.bottomLeft.getGreen(), gradient.bottomLeft.getBlue(), gradient.bottomLeft.getAlpha(), 0.0f, 0.0f, 0.0f),
                v2.put((float) (x + (radius * Math.cos(i + step))), (float) (y + (radius * Math.sin(i + step))), 0.0f, radRotation, (float) originX, (float) originY, gradient.bottomLeft.getRed(), gradient.bottomLeft.getGreen(), gradient.bottomLeft.getBlue(), gradient.bottomLeft.getAlpha(), 0.0f, 0.0f, 0.0f),
                v3.put(x, y, 0.0f, radRotation, (float) originX, (float) originY, gradient.bottomLeft.getRed(), gradient.bottomLeft.getGreen(), gradient.bottomLeft.getBlue(), gradient.bottomLeft.getAlpha(), 0.0f, 0.0f, 0.0f)
            ), useCamera);
        }
    }

    public void voidArc(int x, int y, int radius, int thickness, int range, int start, float precision) {
        voidArc(x, y, radius, thickness, range, start, precision, 0.0f);
    }

    public void voidArc(int x, int y, int radius, int thickness, int range, int start, float precision, float rotation) {
        voidArc(x, y, radius, thickness, range, start, precision, rotation, x, y);
    }

    public void voidArc(int x, int y, int radius, int thickness, int range, int start, float precision, float rotation, int originX, int originY) {
        int rRadius = radius - 1;
        double tau = Math.PI * 2.0;
        double rRange = Math.PI * 2.0 - Math.toRadians(range);
        double step = tau / precision;
        for (double i = Math.toRadians(start); i < tau - rRange + Math.toRadians(start); i += step)   {
            line((int) (x + (rRadius * Math.cos(i))), (int) (y + (rRadius * Math.sin(i))), (int) (x + (rRadius * Math.cos(i + step))), (int) (y + (rRadius * Math.sin(i + step))), thickness + 1, rotation, originX, originY);
        }
    }

    public void line(int x1, int y1, int x2, int y2, int thickness) {
        line(x1, y1, x2, y2, thickness, 0.0f);
    }

    public void line(int x1, int y1, int x2, int y2, int thickness, float rotation) {
        line(x1, y1, x2, y2, thickness, 0.0f, (x1 + x2) / 2, (y1 + y2) / 2);
    }

    public void line(int x1, int y1, int x2, int y2, int thickness, float rotation, int originX, int originY) {
        float theta = (float) Math.atan2(x2 - x1, y2 - y1);
        float thetaSin = (float) (Math.sin(theta) * (thickness / 2));
        float thetaCos = (float) (Math.cos(theta) * (thickness / 2));
        float radRotation = (float) Math.toRadians(rotation);

        BatchController.addVertices(verts.set(
            v1.put(x1 - thetaCos, y1 + thetaSin, 0.0f, radRotation, (float) originX, (float) originY, gradient.bottomLeft.getRed(), gradient.bottomLeft.getGreen(), gradient.bottomLeft.getBlue(), gradient.bottomLeft.getAlpha(), 0.0f, 0.0f, 0.0f),
            v2.put(x1 + thetaCos, y1 - thetaSin, 0.0f, radRotation, (float) originX, (float) originY, gradient.topLeft.getRed(), gradient.topLeft.getGreen(), gradient.topLeft.getBlue(), gradient.topLeft.getAlpha(), 0.0f, 0.0f, 0.0f),
            v3.put(x2 + thetaCos, y2 - thetaSin, 0.0f, radRotation, (float) originX, (float) originY, gradient.topRight.getRed(), gradient.topRight.getGreen(), gradient.topRight.getBlue(), gradient.topRight.getAlpha(), 0.0f, 0.0f, 0.0f),
            v4.put(x2 - thetaCos, y2 + thetaSin, 0.0f, radRotation, (float) originX, (float) originY, gradient.bottomRight.getRed(), gradient.bottomRight.getGreen(), gradient.bottomRight.getBlue(), gradient.bottomRight.getAlpha(), 0.0f, 0.0f, 0.0f)
        ), useCamera);
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
            v1.put(ax, ay2, 0.0f, radRotation, (float) originX, (float) originY, gradient.bottomLeft.getRed(), gradient.bottomLeft.getGreen(), gradient.bottomLeft.getBlue(), gradient.bottomLeft.getAlpha(), 0.0f, 0.0f, (float) texID),
            v2.put(ax, ay, 0.0f, radRotation, (float) originX, (float) originY, gradient.topLeft.getRed(), gradient.topLeft.getGreen(), gradient.topLeft.getBlue(), gradient.topLeft.getAlpha(), 0.0f, 1.0f, (float) texID),
            v3.put(ax2, ay, 0.0f, radRotation, (float) originX, (float) originY, gradient.topRight.getRed(), gradient.topRight.getGreen(), gradient.topRight.getBlue(), gradient.topRight.getAlpha(), 1.0f, 1.0f, (float) texID),
            v4.put(ax2, ay2, 0.0f, radRotation, (float) originX, (float) originY, gradient.bottomRight.getRed(), gradient.bottomRight.getGreen(), gradient.bottomRight.getBlue(), gradient.bottomRight.getAlpha(), 1.0f, 0.0f, (float) texID)
        ), useCamera);
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
            v1.put(ax, ay2, 0.0f, radRotation, (float) originX, (float) originY, gradient.bottomLeft.getRed(), gradient.bottomLeft.getGreen(), gradient.bottomLeft.getBlue(), gradient.bottomLeft.getAlpha(), ux0, uy0, (float) texID),
            v2.put(ax, ay, 0.0f, radRotation, (float) originX, (float) originY, gradient.topLeft.getRed(), gradient.topLeft.getGreen(), gradient.topLeft.getBlue(), gradient.topLeft.getAlpha(), ux0, uy1, (float) texID),
            v3.put(ax2, ay, 0.0f, radRotation, (float) originX, (float) originY, gradient.topRight.getRed(), gradient.topRight.getGreen(), gradient.topRight.getBlue(), gradient.topRight.getAlpha(), ux1, uy1, (float) texID),
            v4.put(ax2, ay2, 0.0f, radRotation, (float) originX, (float) originY, gradient.bottomRight.getRed(), gradient.bottomRight.getGreen(), gradient.bottomRight.getBlue(), gradient.bottomRight.getAlpha(), ux1, uy0, (float) texID)
        ), useCamera);
    }

    public void imageFromTo(int x1, int y1, int x2, int y2, int thickness, Texture texture) {
        float theta = (float) Math.atan2(x2 - x1, y2 - y1);
        float thetaSin = (float) (Math.sin(theta) * thickness);
        float thetaCos = (float) (Math.cos(theta) * thickness);

        int texID = BatchController.addTexture(texture);

        BatchController.addVertices(verts.set(
            v1.put(x1 - thetaCos, y1 + thetaSin, 0.0f, 0.0f, 0.0f, 0.0f, gradient.bottomLeft.getRed(), gradient.bottomLeft.getGreen(), gradient.bottomLeft.getBlue(), gradient.bottomLeft.getAlpha(), 0.0f, 0.0f, texID),
            v2.put(x1 + thetaCos, y1 - thetaSin, 0.0f, 0.0f, 0.0f, 0.0f, gradient.topLeft.getRed(), gradient.topLeft.getGreen(), gradient.topLeft.getBlue(), gradient.topLeft.getAlpha(), 0.0f, 1.0f, texID),
            v3.put(x2 + thetaCos, y2 - thetaSin, 0.0f, 0.0f, 0.0f, 0.0f, gradient.topRight.getRed(), gradient.topRight.getGreen(), gradient.topRight.getBlue(), gradient.topRight.getAlpha(), 1.0f, 1.0f, texID),
            v4.put(x2 - thetaCos, y2 + thetaSin, 0.0f, 0.0f, 0.0f, 0.0f, gradient.bottomRight.getRed(), gradient.bottomRight.getGreen(), gradient.bottomRight.getBlue(), gradient.bottomRight.getAlpha(), 1.0f, 0.0f, texID)
        ), useCamera);
    }

    public void imageFromTo(int x1, int y1, int x2, int y2, int thickness, TextureRegion texture) {
        float theta = (float) Math.atan2(x2 - x1, y2 - y1);
        float thetaSin = (float) (Math.sin(theta) * thickness);
        float thetaCos = (float) (Math.cos(theta) * thickness);

        float ux0 = texture.getUVCoordinates()[0];
        float ux1 = texture.getUVCoordinates()[1];
        float uy1 = texture.getUVCoordinates()[2];
        float uy0 = texture.getUVCoordinates()[3];

        int texID = BatchController.addTexture(texture.getParentTexture());

        BatchController.addVertices(verts.set(
            v1.put(x1 - thetaCos, y1 + thetaSin, 0.0f, 0.0f, 0.0f, 0.0f, gradient.bottomLeft.getRed(), gradient.bottomLeft.getGreen(), gradient.bottomLeft.getBlue(), gradient.bottomLeft.getAlpha(), ux0, uy0, texID),
            v2.put(x1 + thetaCos, y1 - thetaSin, 0.0f, 0.0f, 0.0f, 0.0f, gradient.topLeft.getRed(), gradient.topLeft.getGreen(), gradient.topLeft.getBlue(), gradient.topLeft.getAlpha(), ux0, uy1, texID),
            v3.put(x2 + thetaCos, y2 - thetaSin, 0.0f, 0.0f, 0.0f, 0.0f, gradient.topRight.getRed(), gradient.topRight.getGreen(), gradient.topRight.getBlue(), gradient.topRight.getAlpha(), ux1, uy1, texID),
            v4.put(x2 - thetaCos, y2 + thetaSin, 0.0f, 0.0f, 0.0f, 0.0f, gradient.bottomRight.getRed(), gradient.bottomRight.getGreen(), gradient.bottomRight.getBlue(), gradient.bottomRight.getAlpha(), ux1, uy0, texID)
        ), useCamera);
    }

    public void animation(int x, int y, int width, int height, Animation animation) {

    }

    public void animation(int x, int y, int width, int height, Animation animation, float rotation) {

    }

    public void animation(int x, int y, int width, int height, Animation animation, float rotation, int originX, int originY) {

    }

    public void text(int x, int y, int height, String text) {
        text(x, y, height, text, font, 0.0f, 0, 0);
    }

    public void text(int x, int y, int height, String text, BitmapFont font) {
        text(x, y, height, text, font, 0.0f, 0, 0);
    }

    public void text(int x, int y, int height, String text, BitmapFont font, float rotation) {
        int width = font.getWidth(text, height);
        text(x, y, height, text, font, rotation, x + width / 4, y + height / 4);
    }

    public void text(int x, int y, int height, String text, BitmapFont font, float rotation, int originX, int originY) {
        int charX = 0;
        float radRotation = (float) Math.toRadians(rotation);

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
                v1.put(ax, ay2, 0.0f, radRotation, originX, originY, gradient.bottomLeft.getRed(), gradient.bottomLeft.getGreen(), gradient.bottomLeft.getBlue(), gradient.bottomLeft.getAlpha(), ux0, uy0, (float) texID),
                v2.put(ax, ay, 0.0f, radRotation, originX, originY, gradient.topLeft.getRed(), gradient.topLeft.getGreen(), gradient.topLeft.getBlue(), gradient.topLeft.getAlpha(), ux0, uy1, (float) texID),
                v3.put(ax2, ay, 0.0f, radRotation, originX, originY, gradient.topRight.getRed(), gradient.topRight.getGreen(), gradient.topRight.getBlue(), gradient.topRight.getAlpha(), ux1, uy1, (float) texID),
                v4.put(ax2, ay2, 0.0f, radRotation, originX, originY, gradient.bottomRight.getRed(), gradient.bottomRight.getGreen(), gradient.bottomRight.getBlue(), gradient.bottomRight.getAlpha(), ux1, uy0, (float) texID)
            ), useCamera);
        }
    }
}
