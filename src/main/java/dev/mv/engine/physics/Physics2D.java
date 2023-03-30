package dev.mv.engine.physics;

public class Physics2D {

    private static Physics2D instance = null;

    private Physics2D() {}

    public static Physics2D init() {
        if (instance != null) return instance;
        instance = new Physics2D();
        return instance;
    }

    public void terminate() {
        instance = null;
    }

}
