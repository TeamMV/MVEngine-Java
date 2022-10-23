package dev.mv.engine.oldRender.utils;

import dev.mv.engine.oldRender.window.Window;

public interface Loopable {
    void loop(Window w);

    void tick(Window w);
}