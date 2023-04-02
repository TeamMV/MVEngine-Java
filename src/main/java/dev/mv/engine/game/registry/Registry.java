package dev.mv.engine.game.registry;

public interface Registry<T> {

    <R extends T> RegisteredObject<R> register(String id, Class<R> clazz);

    <R extends T> R newInstance(Class<R> clazz);

    <R extends T> R newInstance(String id);

    <R extends T> RegisteredObject<R> get(Class<R> clazz);

    <R extends T> RegisteredObject<R> get(String id);

}
