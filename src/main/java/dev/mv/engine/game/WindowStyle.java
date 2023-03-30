package dev.mv.engine.game;

import dev.mv.engine.render.WindowCreateInfo;

public class WindowStyle {
    Type type;
    Object data;

    private WindowStyle(Type type, Object data) {
        this.type = type;
        this.data = data;
    }

    public static WindowStyle width(int width) {
        return new WindowStyle(Type.WIDTH, width);
    }

    public static WindowStyle height(int height) {
        return new WindowStyle(Type.HEIGHT, height);
    }

    public static WindowStyle fullscreen() {
        return new WindowStyle(Type.FULLSCREEN, true);
    }

    public static WindowStyle fullscreen(boolean fullscreen) {
        return new WindowStyle(Type.FULLSCREEN, fullscreen);
    }

    public static WindowStyle staticSize() {
        return new WindowStyle(Type.RESIZEABLE, false);
    }

    public static WindowStyle resizeable(boolean resizeable) {
        return new WindowStyle(Type.RESIZEABLE, resizeable);
    }

    public static WindowStyle noToolbar() {
        return new WindowStyle(Type.DECORATED, false);
    }

    public static WindowStyle toolbar(boolean toolbar) {
        return new WindowStyle(Type.DECORATED, toolbar);
    }

    public static WindowStyle fpsToTitle() {
        return fpsToTitle(" - ", "fps");
    }

    public static WindowStyle fpsToTitle(String between, String after) {
        WindowCreateInfo.FPSAppendInfo info = new WindowCreateInfo.FPSAppendInfo();
        info.betweenTitleAndValue = between;
        info.afterValue = after;
        return new WindowStyle(Type.FPS_TITLE, info);
    }

    public static WindowStyle fpsToTitle(WindowCreateInfo.FPSAppendInfo info) {
        return new WindowStyle(Type.FPS_TITLE, info);
    }

    public static WindowStyle title(String title) {
        return new WindowStyle(Type.TITLE, title);
    }

    public static WindowStyle ups(int ups) {
        return new WindowStyle(Type.UPS, ups);
    }

    public enum Type {
        WIDTH,
        HEIGHT,
        FULLSCREEN,
        RESIZEABLE,
        DECORATED,
        UPS,
        TITLE,
        FPS_TITLE
    }
}