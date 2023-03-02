package dev.mv.engine.files;

public interface Saver<T> {

    T load(byte[] data);

    byte[] save(T object);

}
