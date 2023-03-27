package dev.mv.engine.game.registry;

import dev.mv.engine.game.registry.api.GameResourceType;
import dev.mv.utils.collection.Vec;

public class RegistryLoader {

    static void createResourceRegistries(Vec<Class<?>> resourceClasses) {
        resourceClasses.forEach(clazz -> {
            GameResourceType type = clazz.getAnnotation(GameResourceType.class);
            RegistryType registryType = RegistryType.newType(type.value(), clazz);
            ResourceRegistry<?> registry = new ResourceRegistry<>(clazz);
            Registries.add(registryType, registry);
        });
    }

}
