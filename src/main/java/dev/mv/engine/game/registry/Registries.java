package dev.mv.engine.game.registry;

import dev.mv.engine.MVEngine;
import dev.mv.engine.game.mod.loader.ModIntegration;
import dev.mv.engine.game.registry.api.GameResourceType;
import dev.mv.utils.collection.Vec;

import java.util.HashMap;
import java.util.Map;

public class Registries {

    private static final Map<RegistryType, Registry<?>> registries = new HashMap<>();
    private static Vec<Class<?>> resourceTypes = new Vec<>();

    public static void init() {
        resourceTypes = ModIntegration.getClasses().iter().filter(clazz -> clazz.isAnnotationPresent(GameResourceType.class)).collect();
        RegistryLoader.createResourceRegistries(resourceTypes);
        resourceTypes.forEach(clazz -> RegistryLoader.registerResources(ModIntegration.getBaseClasses(), clazz, MVEngine.instance().getGame().getGameId(), registry(clazz)));
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

    public static Vec<Class<?>> getResourceTypes() {
        return resourceTypes;
    }

}
