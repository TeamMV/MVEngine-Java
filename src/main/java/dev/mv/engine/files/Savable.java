package dev.mv.engine.files;

public interface Savable<T> {

    Saver<T> getSaver();

}
