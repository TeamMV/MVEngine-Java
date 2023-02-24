package dev.mv.engine.input;

import dev.mv.engine.resources.R;

public final class DefaultInputProcessor implements InputProcessor {
    static DefaultInputProcessor INSTANCE = new DefaultInputProcessor();

    private DefaultInputProcessor() {}

    @Override
    public void mousePosUpdate(int x, int y) {
        Input.updateMouse(x, y);
    }

    @Override
    public void mouseScrollUpdate(int sx, int sy) {
        Input.updateMouseScroll(sx, sy);
    }

    @Override
    public void mouseButtonUpdate(int btn, InputCollector.MouseAction action) {
        Input.updateButton(btn, action);
    }

    @Override
    public void keyUpdate(int key, InputCollector.KeyAction action, int mods) {
        Input.updateKey(key, action);
        R.sendInputKeyEvent(key, action, mods);
    }
}
