package dev.mv.engine.utils;

import dev.mv.engine.window.Window;

public interface Loopable {
    void loop(Window w);

    void tick(Window w);
}