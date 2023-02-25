package dev.mv.engine.gui.input;


public interface Clickable {
    void click(int x, int y, int btn);
    void clickRelease(int x, int y, int btn);
}
