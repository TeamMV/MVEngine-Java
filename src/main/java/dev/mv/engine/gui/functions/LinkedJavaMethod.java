package dev.mv.engine.gui.functions;

import lombok.SneakyThrows;

import java.lang.reflect.Method;

public class LinkedJavaMethod implements GuiMethod {

    private Method method;
    private Object instance;

    public LinkedJavaMethod(Method method, Object instance) {
        this.method = method;
        this.instance = instance;
    }

    @Override
    @SneakyThrows
    public void invoke(Object[] params) {
        method.invoke(instance, params);
    }
}
