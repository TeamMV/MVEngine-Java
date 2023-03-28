package dev.mv.engine.game.registry;

public interface RegisteredObject<R> {

    R newInstance();

    Class<R> getType();

    String getId();

    RegistryType getRegistryType();

}
