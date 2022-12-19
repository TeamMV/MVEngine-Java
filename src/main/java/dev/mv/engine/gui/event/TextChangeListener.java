package dev.mv.engine.gui.event;

import dev.mv.engine.gui.components.Element;

public interface TextChangeListener extends EventListener {
    void onChange(Element element, CharSequence before, CharSequence after);
    void onSpaceFull(Element element, int charAmount, char lastChar);
}
