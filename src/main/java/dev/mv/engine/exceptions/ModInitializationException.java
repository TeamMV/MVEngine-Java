package dev.mv.engine.exceptions;

public class ModInitializationException extends Exception {

    public ModInitializationException() {
        super();
    }

    public ModInitializationException(String message) {
        super(message);
    }

    public ModInitializationException(Exception e) {
        super(e);
    }
}
