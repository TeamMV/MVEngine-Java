package dev.mv.engine.game.registry;

import dev.mv.engine.exceptions.Exceptions;
import dev.mv.utils.collection.Vec;

public class RegistryType {

    private static Vec<RegistryType> types = new Vec<>();

    private String type;
    private Class<?> clazz;

    private RegistryType(String type, Class<?> clazz) {
        this.type = type;
        this.clazz = clazz;
    }

    public static RegistryType getRegistryType(String type) {
        for (RegistryType registryType : types) {
            if (registryType.getType().equals(type)) {
                return registryType;
            }
        }
        return null;
    }

    public static RegistryType getRegistryType(Class<?> clazz) {
        for (RegistryType registryType : types) {
            if (registryType.getClazz().isAssignableFrom(clazz)) {
                return registryType;
            }
        }
        return null;
    }

    public static RegistryType newType(String type, Class<?> clazz) {
        if (getRegistryType(type) != null || getRegistryType(clazz) != null) {
            Exceptions.send("DUPLICATE_REGISTRY_TYPE", type, clazz.getName());
        }
        RegistryType registryType = new RegistryType(type, clazz);
        types.push(registryType);
        return registryType;
    }

    public String getType() {
        return type;
    }

    public Class<?> getClazz() {
        return clazz;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof RegistryType other) {
            return type.equals(other.type);
        }
        return false;
    }

    @Override
    public String toString() {
        return "Registry type: {id: \"" + type + "\", class: " + clazz + "}";
    }
}
