package dev.mv.editor;

import dev.mv.engine.render.shared.Window;

public interface ApplicationLoop {
    void start(Window window) throws Exception;
    void update(Window window) throws Exception;
    void draw(Window window) throws Exception;
}
