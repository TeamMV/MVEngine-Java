package dev.mv.engine.gui.components.extras;

public interface ValueChange {
    void increment(float amount);

    void decrement(float amount);

    void incrementByPercentage(int amount);

    void decrementByPercentage(int amount);
}
