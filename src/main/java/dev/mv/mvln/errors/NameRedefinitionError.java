package dev.mv.mvln.errors;

public class NameRedefinitionError extends Exception {

    public NameRedefinitionError() {
        super();
    }

    public NameRedefinitionError(String message) {
        super(message);
    }

}