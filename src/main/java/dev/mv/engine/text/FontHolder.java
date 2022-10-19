package dev.mv.engine.text;

public class FontHolder {

    public static BitmapFont font;

    public static void onStart() {
        font = new BitmapFont("/fonts/designerBlock/designerBlock.png", "/fonts/designerBlock/designerBlock.fnt");
    }
}
