package dev.mv.engine.exceptions;

public class InvalidUnitException extends Exception {
    public InvalidUnitException() {
        super();
    }

    public InvalidUnitException(String msg) {
        super(msg);
    }

    public InvalidUnitException(Throwable cause) {
        super(cause);
    }
}
