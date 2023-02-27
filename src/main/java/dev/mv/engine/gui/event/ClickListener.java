package dev.mv.engine.gui.event;

import dev.mv.engine.gui.components.Element;

public interface ClickListener extends EventListener {
    void onCLick(Element element, int button);

    void onRelease(Element element, int button);
}
