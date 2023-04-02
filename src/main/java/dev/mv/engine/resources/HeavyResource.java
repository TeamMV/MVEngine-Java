package dev.mv.engine.resources;

public interface HeavyResource extends Resource {

    void load();

    void unload();

}
