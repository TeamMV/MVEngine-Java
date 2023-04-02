package dev.mv.engine.game.event;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class LinkedMethod {

    final boolean receiveCancelled;
    private Object instance;
    private Method method;

    LinkedMethod(Object instance, Method method, boolean receiveCancelled) {
        this.instance = instance;
        this.method = method;
        this.receiveCancelled = receiveCancelled;
    }

    public void invoke(Object... args) throws InvocationTargetException, IllegalAccessException {
        method.invoke(instance, args);
    }

}
