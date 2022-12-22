package dev.mv.engine.gui.event;

import dev.mv.engine.gui.components.Element;

public interface KeyListener extends EventListener {
    void onPress(Element element, int keyCode, char keyChar);
    void onType(Element element, int keyCode, char keyChar);
    void onRelease(Element element, int keyCode, char keyChar);
}
