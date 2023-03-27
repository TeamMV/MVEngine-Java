package dev.mv.engine.exceptions;

public class ModLoadingException extends Exception {

    public ModLoadingException() {
        super();
    }

    public ModLoadingException(String message) {
        super(message);
    }

    public ModLoadingException(Exception e) {
        super(e);
    }
}
