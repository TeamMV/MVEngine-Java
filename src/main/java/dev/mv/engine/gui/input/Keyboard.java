package dev.mv.engine.gui.input;

public interface Keyboard {
    void keyPress(int key);

    void keyType(int key);

    void keyRelease(int key);
}
