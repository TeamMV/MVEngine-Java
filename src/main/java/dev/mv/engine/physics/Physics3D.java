package dev.mv.engine.physics;

import dev.mv.engine.exceptions.Exceptions;

public class Physics3D {

    private static Physics3D instance = null;

    private Physics3D() {
    }

    public static Physics3D init() {
        if (instance != null) Exceptions.send(new IllegalStateException("Physics3D already initialized"));
        if (!PhysX.init()) return null;
        instance = new Physics3D();
        return instance;
    }

    public void terminate() {
        PhysX.terminate();
        instance = null;
    }

}
