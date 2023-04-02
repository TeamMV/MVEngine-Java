package dev.mv.engine.exceptions;

public class DuplicateRegistryTypeException extends Exception {
    public DuplicateRegistryTypeException() {
        super();
    }

    public DuplicateRegistryTypeException(String msg) {
        super(msg);
    }

    public DuplicateRegistryTypeException(Throwable cause) {
        super(cause);
    }
}
