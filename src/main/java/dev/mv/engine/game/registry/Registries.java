package dev.mv.engine.game.registry;

import dev.mv.engine.MVEngine;
import dev.mv.engine.game.mod.loader.ModIntegration;
import dev.mv.engine.game.registry.api.GameResource;
import dev.mv.engine.game.registry.api.GameResourceType;
import dev.mv.engine.resources.AssetBundle;
import dev.mv.utils.collection.Vec;

import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

public class Registries {

    private static final Map<RegistryType, Registry<?>> registries = new HashMap<>();
    private static Vec<Class<?>> resourceTypes = new Vec<>();

    public static void init() {
        resourceTypes = ModIntegration.getClasses().fastIter().filter(Registries::isGameResourceType).collect();
        RegistryLoader.createResourceRegistries(resourceTypes);
        resourceTypes.forEach(clazz -> RegistryLoader.registerResources(ModIntegration.getBaseClasses(), clazz, MVEngine.instance().getGame().getGameId()));
    }

    public static <T> Registry<T> registry(String type) {
        return registry(RegistryType.getRegistryType(type));
    }

    public static <T> Registry<T> registry(Class<? extends T> type) {
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

    public static <T> AssetBundle getLinkedAssetBundle(Class<? extends T> type) {
        Registry<T> registry = registry(type);
        if (registry instanceof ResourceRegistry<T> resourceRegistry) {
            return resourceRegistry.getAssetBundle(type);
        }
        return null;
    }

    public static <T, R extends T> RegisteredObject<R> get(Class<R> type) {
        Registry<T> registry = registry(type);
        if (registry != null) {
            return registry.get(type);
        }
        return null;
    }

    static boolean isGameResourceType(Class<?> clazz) {
        return Modifier.isAbstract(clazz.getModifiers()) && !Modifier.isInterface(clazz.getModifiers()) && clazz.isAnnotationPresent(GameResourceType.class);
    }

    static boolean isGameResource(Class<?> clazz) {
        return !Modifier.isAbstract(clazz.getModifiers()) && clazz.isAnnotationPresent(GameResource.class);
    }

}
