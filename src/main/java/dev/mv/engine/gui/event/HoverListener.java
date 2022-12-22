package dev.mv.engine.gui.event;

import dev.mv.engine.gui.components.Element;

public interface HoverListener {
    void onEnter(Element element, int mx, int my);
    void hover(Element element, int mx, int my);
    void onLeave(Element element, int mx, int my);
}
