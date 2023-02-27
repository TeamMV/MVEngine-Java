package dev.mv.engine.gui.functions;

import java.lang.reflect.Method;

public class LinkedJavaMethod implements GuiMethod {

    private Method method;
    private Object instance;

    public LinkedJavaMethod(Method method, Object instance) {
        this.method = method;
        this.instance = instance;
    }

    @Override
    public void invoke(Object... params) {
        try {
            method.invoke(instance, params);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
