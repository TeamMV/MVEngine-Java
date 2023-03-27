package dev.mv.engine.game.registry;

import java.util.function.Supplier;

public interface Registry<T> {

    <R extends T> RegisteredObject<R> register(String id, Class<R> clazz);

    <R extends T> R newInstance(Class<R> clazz);

    <R extends T> R newInstance(String id);

}
