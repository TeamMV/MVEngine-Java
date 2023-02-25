package dev.mv.engine.files;

public interface ObjectDirectorySaver<T> {

    T load(Directory directory);

    void save(Directory directory, T object);

}
