package dev.mv.engine.exceptions;

public class MalformedExceptionsFileException extends Exception {
    public MalformedExceptionsFileException() {
        super();
    }

    public MalformedExceptionsFileException(String msg) {
        super(msg);
    }

    public MalformedExceptionsFileException(Throwable cause) {
        super(cause);
    }
}
