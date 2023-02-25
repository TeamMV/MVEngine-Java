package dev.mv.engine.files;

public interface ObjectSaver<T> {

    T load(byte[] data);

    byte[] save(T object);

}
