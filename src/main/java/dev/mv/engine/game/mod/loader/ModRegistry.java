package dev.mv.engine.game.mod.loader;

import dev.mv.engine.game.registry.RegisteredObject;
import dev.mv.engine.game.registry.Registries;
import dev.mv.engine.game.registry.Registry;
import dev.mv.engine.game.registry.RegistryType;

import java.util.function.Supplier;

public class ModRegistry<T> implements Registry<T> {

    private final Mod mod;
    private final RegistryType type;
    private final Registry<T> registry;

    ModRegistry(Mod mod, RegistryType type) {
        this.mod = mod;
        this.type = type;
        this.registry = Registries.registry(type);
    }

    @Override
    public <R extends T> RegisteredObject<R> register(String id, Class<R> clazz, Supplier<R> constructor) {
        return id.contains(":") ? registry.register(id, clazz, constructor) : registry.register(mod.id + ":" + id, clazz, constructor);
    }

    @Override
    public <R extends T> R newInstance(Class<R> clazz) {
        return registry.newInstance(clazz.getName());
    }

    @Override
    public <R extends T> R newInstance(String id) {
        return id.contains(":") ? registry.newInstance(id) : registry.newInstance(mod.id + ":" + id);
    }
}
