package dev.mv.engine.gui.event;

import dev.mv.engine.gui.components.Element;

public interface ProgressListener extends EventListener{
    void onIncrement(Element e, int currentValue, int totalValue, int percentage);
    void onDecrement(Element e, int currentValue, int totalValue, int percentage);
}
