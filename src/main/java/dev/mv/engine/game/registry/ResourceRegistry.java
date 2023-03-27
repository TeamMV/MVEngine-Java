package dev.mv.engine.game.registry;

import dev.mv.engine.exceptions.Exceptions;
import dev.mv.utils.collection.Vec;

import java.util.function.Supplier;

public class ResourceRegistry<T> implements Registry<T> {

    private final Vec<Registered<? extends T>> items = new Vec<>();
    private final String resourceId;

    ResourceRegistry(Class<T> parent, String resourceId) {
        this.resourceId = resourceId;
    }

    @Override
    public <R extends T> RegisteredObject<R> register(String id, Class<R> clazz) {
        Registered<R> item = new Registered<>(id, clazz);
        if (items.contains(item)) {
            throw new RuntimeException("Duplicate player ids: " + id);
        }
        //TODO: Do all the loading needed, like textures/models for blocks, referencing the assets bytecode
        items.push(item);
        return item;
    }

    @Override
    public <R extends T> R newInstance(Class<R> clazz) {
        return items
                .iter()
                .filter(item -> item.clazz.equals(clazz))
                .first()
                .asNullHandler()
                .thenReturn(Registered::newInstance)
                .getGenericReturnValue()
                .value();
    }

    @Override
    public <R extends T> R newInstance(String id) {
        return items
                .iter()
                .filter(item -> item.id.equals(id))
                .first()
                .asNullHandler()
                .thenReturn(Registered::newInstance)
                .getGenericReturnValue()
                .value();
    }



    private class Registered<R extends T> implements RegisteredObject<R> {

        private String id;
        private Class<R> clazz;

        private Registered(String id, Class<R> clazz) {
            this.id = id;
            this.clazz = clazz;
        }

        public R newInstance() {
            try {
                return clazz.newInstance();
            } catch (InstantiationException | IllegalAccessException e) {
                Exceptions.send("GAME_RESOURCE_CONSTRUCTOR", clazz.getName());
                return null;
            }
        }

        @Override
        public Class<R> getType() {
            return clazz;
        }

        @Override
        public String getId() {
            return id;
        }

        @Override
        public boolean equals(Object other) {
            if (other instanceof RegisteredObject<?> r) {
                return id.equals(r.getId()) && clazz.equals(r.getType());
            }
            return false;
        }
    }
}
