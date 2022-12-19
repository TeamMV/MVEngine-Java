package dev.mv.engine.gui.event;

public interface KeyListener extends EventListener {
    void onPress(int keyCode, char keyChar, int additions);
    void onType(int keyCode, char keyChar, int additions);
    void onRelease(int keyCode, char keyChar, int additions);
}
