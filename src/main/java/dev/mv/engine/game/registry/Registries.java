package dev.mv.engine.game.registry;

import dev.mv.engine.game.mod.loader.ModIntegration;
import dev.mv.engine.game.registry.api.GameResourceType;
import dev.mv.utils.collection.Vec;

import java.util.HashMap;

public class Registries {

    private static final HashMap<RegistryType, Registry<?>> registries = new HashMap<>();

    public static void init() {
        Vec<Class<?>> resourceTypes = ModIntegration.getClasses().iter().filter(clazz -> clazz.isAnnotationPresent(GameResourceType.class)).collect();
        RegistryLoader.createResourceRegistries(resourceTypes);
    }

    public static <T> Registry<T> registry(String type) {
        return registry(RegistryType.getRegistryType(type));
    }

    public static <T> Registry<T> registry(Class<?> type) {
        return registry(RegistryType.getRegistryType(type));
    }

    public static <T> Registry<T> registry(RegistryType type) {
        return (Registry<T>) registries.get(type);
    }

    public static <T> void add(RegistryType type, Registry<T> registry) {
        registries.put(type, registry);
    }

}
