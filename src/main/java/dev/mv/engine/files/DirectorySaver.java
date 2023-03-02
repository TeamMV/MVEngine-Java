package dev.mv.engine.files;

public interface DirectorySaver<T> {

    T load(Directory directory);

    void save(Directory directory, T object);

}
