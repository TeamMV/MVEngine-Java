package dev.mv.engine.resources;

@FunctionalInterface
public interface ProgressAction {
    void update(int total, int current, int percentage);
}
