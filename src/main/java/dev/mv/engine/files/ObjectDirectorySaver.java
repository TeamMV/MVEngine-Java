package dev.mv.engine.files;

import java.io.File;

public interface ObjectDirectorySaver<T> {

    T load(File directory);

    void save(File directory, T object);

}
