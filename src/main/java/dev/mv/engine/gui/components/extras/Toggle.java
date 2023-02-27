package dev.mv.engine.gui.components.extras;

public interface Toggle {
    void disable();

    void enable();

    void toggle();

    boolean isEnabled();
}
