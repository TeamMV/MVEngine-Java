package dev.mv.engine.game.registry;

import dev.mv.engine.game.registry.api.GameResource;
import dev.mv.engine.game.registry.api.GameResourceType;
import dev.mv.utils.collection.Vec;

public class RegistryLoader {

    static void createResourceRegistries(Vec<Class<?>> resourceClasses) {
        resourceClasses.forEach(clazz -> {
            GameResourceType type = clazz.getAnnotation(GameResourceType.class);
            RegistryType registryType = RegistryType.newType(type.value(), clazz);
            ResourceRegistry<?> registry = new ResourceRegistry<>(registryType);
            Registries.add(registryType, registry);
        });
    }

    public static <T> void registerResources(Vec<Class<?>> classes, Class<T> baseClass, String originId) {
        registerResources(classes, baseClass, originId, Registries.registry(baseClass));
    }

    public static <T> void registerResources(Vec<Class<?>> classes, Class<T> baseClass, String originId, Registry<T> registry) {
        classes.fastIter().filter(Registries::isGameResource).filter(baseClass::isAssignableFrom).forEach((Class<?> clazz) -> {
            GameResource resource = clazz.getAnnotation(GameResource.class);
            String id = resource.value();
            registry.register(originId + ":" + id, (Class<? extends T>) clazz);
        });
    }

}
