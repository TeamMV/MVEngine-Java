package dev.mv.engine.gui.event;

import dev.mv.engine.gui.components.Element;

public interface ScrollListener extends EventListener {
    void onScrollX(Element element, int amount);

    void onScrollY(Element element, int amount);
}
