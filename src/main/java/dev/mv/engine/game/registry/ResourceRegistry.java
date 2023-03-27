package dev.mv.engine.game.registry;

import dev.mv.utils.collection.Vec;

import java.util.function.Supplier;

public class ResourceRegistry<T> implements Registry<T> {

    private final Vec<Registered<? extends T>> items = new Vec<>();

    ResourceRegistry(Class<T> parent) {}

    @Override
    public <R extends T> RegisteredObject<R> register(String id, Class<R> clazz, Supplier<R> constructor) {
        Registered<R> item = new Registered<>(id, clazz, constructor);
        if (items.contains(item)) {
            throw new RuntimeException("Duplicate player ids: " + id);
        }
        //Do all the loading needed, like textures/models for blocks, referencing the assets bytecode
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
                .otherwiseReturn(null)
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
                .otherwiseReturn(null)
                .getGenericReturnValue()
                .value();
    }

    private class Registered<R extends T> implements RegisteredObject<R> {

        private String id;
        private Class<R> clazz;
        private Supplier<R> constructor;

        private Registered(String id, Class<R> clazz, Supplier<R> constructor) {
            this.id = id;
            this.clazz = clazz;
            this.constructor = constructor;
        }

        public R newInstance() {
            return constructor.get();
        }

        public Class<R> getType() {
            return clazz;
        }

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
