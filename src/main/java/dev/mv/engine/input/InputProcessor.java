package dev.mv.engine.input;

public interface InputProcessor {
    static InputProcessor defaultProcessor() {
        return DefaultInputProcessor.INSTANCE;
    }

    void mousePosUpdate(int x, int y);

    void mouseScrollUpdate(int sx, int sy);

    void mouseButtonUpdate(int btn, InputCollector.MouseAction action);

    void keyUpdate(int key, InputCollector.KeyAction action, int mods);

    void charTyped(int charCode);
}
