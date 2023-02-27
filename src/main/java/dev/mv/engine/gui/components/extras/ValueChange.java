package dev.mv.engine.gui.components.extras;

public interface ValueChange {
    void increment(int amount);

    void decrement(int amount);

    void incrementByPercentage(int amount);

    void decrementByPercentage(int amount);
}
