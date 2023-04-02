package dev.mv.engine.exceptions;

public class ResourceInitializationException extends Exception {
    public ResourceInitializationException() {
    }

    public ResourceInitializationException(String message) {
        super(message);
    }

    public ResourceInitializationException(String message, Throwable cause) {
        super(message, cause);
    }

    public ResourceInitializationException(Throwable cause) {
        super(cause);
    }

    public ResourceInitializationException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
