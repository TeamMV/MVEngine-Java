package dev.mv.engine.game.registry;

import dev.mv.engine.exceptions.Exceptions;
import dev.mv.engine.resources.AssetBundle;
import dev.mv.utils.Utils;
import dev.mv.utils.collection.Vec;

import java.lang.reflect.InvocationTargetException;

public class ResourceRegistry<T> implements Registry<T> {

    private final Vec<RegisteredResource<? extends T>> items = new Vec<>();
    private final RegistryType type;

    ResourceRegistry(RegistryType type) {
        this.type = type;
    }

    @Override
    public <R extends T> RegisteredObject<R> register(String id, Class<R> clazz) {
        RegisteredResource<R> item = new RegisteredResource<>(id, clazz);
        if (items.contains(item)) {
            Exceptions.send("REGISTRY_DUPLICATE", item.id, type.getType());
        }
        //TODO: Do all the loading needed, like textures/models for blocks, referencing the assets bytecode
        items.push(item);
        return item;
    }

    @Override
    public <R extends T> R newInstance(Class<R> clazz) {
        return Utils.ifNotNull(items
            .fastIter()
            .filter(item -> item.clazz.equals(clazz))
            .first())
            .thenReturn(RegisteredResource::newInstance)
            .getGenericReturnValue()
            .value();
    }

    @Override
    public <R extends T> R newInstance(String id) {
        return Utils.ifNotNull(items
                .fastIter()
                .filter(item -> item.id.equals(id))
                .first())
            .thenReturn(RegisteredResource::newInstance)
            .getGenericReturnValue()
            .value();
    }

    public <R extends T> RegisteredObject<R> get(Class<R> clazz) {
        return Utils.ifNotNull(items
                .fastIter()
                .filter(item -> item.clazz.equals(clazz))
                .first())
            .thenReturn()
            .getGenericReturnValue()
            .value();
    }

    public <R extends T> RegisteredObject<R> get(String id) {
        return Utils.ifNotNull(items
                .fastIter()
                .filter(item -> item.id.equals(id))
                .first())
            .thenReturn()
            .getGenericReturnValue()
            .value();
    }

    public <R extends T> AssetBundle getAssetBundle(Class<R> clazz) {
        return Utils.ifNotNull(items
                .fastIter()
                .filter(item -> item.clazz.equals(clazz))
                .first())
            .thenReturn(RegisteredResource::getAssetBundle)
            .getGenericReturnValue()
            .value();
    }

    public AssetBundle getAssetBundle(String id) {
        return Utils.ifNotNull(items
                .fastIter()
                .filter(item -> item.id.equals(id))
                .first())
            .thenReturn(RegisteredResource::getAssetBundle)
            .getGenericReturnValue()
            .value();
    }


    private class RegisteredResource<R extends T> implements RegisteredObject<R> {

        private String id;
        private Class<R> clazz;
        private AssetBundle assetBundle;

        private RegisteredResource(String id, Class<R> clazz) {
            this.id = id;
            this.clazz = clazz;
        }

        public R newInstance() {
            try {
                return clazz.getConstructor().newInstance();
            } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
                Exceptions.send("GAME_RESOURCE_CONSTRUCTOR", clazz.getName());
                return null;
            }
        }

        public RegistryType getRegistryType() {
            return type;
        }

        @Override
        public Class<R> getType() {
            return clazz;
        }

        @Override
        public String getId() {
            return id;
        }

        public AssetBundle getAssetBundle() {
            return assetBundle;
        }

        @Override
        public boolean equals(Object other) {
            if (other instanceof RegisteredObject<?> r) {
                return type.equals(r.getRegistryType()) && id.equals(r.getId()) && clazz.equals(r.getType());
            }
            return false;
        }
    }
}
