package dev.mv.engine.input;

public interface InputProcessor {
    void mousePosUpdate(int x, int y);
    void mouseScrollUpdate(int sx, int sy);
    void mouseButtonUpdate(int btn, InputCollector.MouseAction action);
    void keyUpdate(int key, InputCollector.KeyAction action);

    static InputProcessor defaultProcessor() {
        return DefaultInputProcessor.INSTANCE;
    }
}
