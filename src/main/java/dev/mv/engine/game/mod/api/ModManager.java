package dev.mv.engine.game.mod.api;

import dev.mv.engine.files.Directory;
import dev.mv.engine.game.event.Event;
import dev.mv.engine.game.registry.Registry;
import dev.mv.engine.game.registry.RegistryType;

public interface ModManager {

    void registerListener(Object instance);

    void dispatchEvent(Event event);

    Directory getGameDirectory();

    Directory getModConfigDirectory();

}
