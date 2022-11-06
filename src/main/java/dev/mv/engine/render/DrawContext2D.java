package dev.mv.engine.render;

import dev.mv.engine.render.drawables.Animation;
import dev.mv.engine.render.drawables.Texture;
import dev.mv.engine.render.drawables.TextureRegion;
import dev.mv.engine.render.drawables.text.BitmapFont;

import java.awt.*;

public interface DrawContext2D {
    void color(int r, int g, int b, int a);

    void color(Color color);

    void font(BitmapFont font);

    void triangle(int x1, int y1, int x2, int y2, int x3, int y3);

    void rectangle(int x, int y, int width, int height);

    void rectangle(int x, int y, int width, int height, float rotation);

    void rectangle(int x, int y, int width, int height, float rotation, int originX, int originY);

    void line(int x1, int y1, int x2, int y2, int depth);

    void image(int x, int y, int width, int height, Texture texture);

    void image(int x, int y, int width, int height, TextureRegion texture);

    void image(int x, int y, int width, int height, Texture texture, float rotation);

    void image(int x, int y, int width, int height, TextureRegion texture, float rotation);

    void image(int x, int y, int width, int height, Texture texture, float rotation, int originX, int originY);

    void image(int x, int y, int width, int height, TextureRegion texture, float rotation, int originX, int originY);

    void imageFromTo(int x1, int y1, int x2, int y2, int depth, Texture texture);

    void imageFromTo(int x1, int y1, int x2, int y2, int depth, TextureRegion texture);

    void animation(int x, int y, int width, int height, Animation animation);

    void animation(int x, int y, int width, int height, Animation animation, float rotation);

    void animation(int x, int y, int width, int height, Animation animation, float rotation, int originX, int originY);

    void text(int x, int y, int height, String text);

    void text(int x, int y, int height, String text, BitmapFont font);
}
