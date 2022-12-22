package dev.mv.engine.gui.input;

public interface Draggable {
    void dragBegin(int x, int y, int btn);
    void drag(int x, int y, int btn);
    void dragLeave(int x, int y, int btn);
}
