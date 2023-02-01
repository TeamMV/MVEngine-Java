package dev.mv.editor;

import dev.mv.engine.render.shared.Window;

public interface ApplicationLoop {
    void start(Window window);
    void update(Window window);
    void draw(Window window);
}
