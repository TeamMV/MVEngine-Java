package dev.mv.engine.input;

import dev.mv.engine.resources.R;

public class InputProcessor {
    public void mousePosUpdate(int x, int y) {
        Input.updateMouse(x, y);
    }

    public void mouseScrollUpdate(int sx, int sy) {
        Input.updateMouseScroll(sx, sy);
    }

    public void mouseButtonUpdate(int btn, InputCollector.MouseAction action) {
        Input.updateButton(btn, action);
    }

    public void keyUpdate(int key, InputCollector.KeyAction action) {
        Input.updateKey(key, action);
        R.sendInputKeyEvent(key, action);
    }
}
