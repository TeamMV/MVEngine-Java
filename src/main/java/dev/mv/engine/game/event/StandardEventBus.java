package dev.mv.engine.game.event;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class StandardEventBus implements EventBus {

    private Bus bus;
    private Map<Class<? extends Event>, MethodList> subscribers = new HashMap<>();

    StandardEventBus(Bus bus) {
        this.bus = bus;
    }

    @Override
    public void dispatch(Event event) {
        MethodList methodList = subscribers.get(event.getClass());
        if (methodList != null) {
            Priority[] priorityOrder = Priority.getOrder();
            for (Priority priority : priorityOrder) {
                for (LinkedMethod method : methodList.getMethods(priority)) {
                    try {
                        if (event instanceof Cancellable cancellable && !method.receiveCancelled) {
                            if (!cancellable.isCancelled()) {
                                method.invoke(event);
                            }
                        } else {
                            method.invoke(event);
                        }
                    } catch (Exception ignore) {
                    }
                }
            }
        }
    }

    @Override
    public void register(Object listener) {
        Class<?> clazz = listener.getClass();
        for (Method method : clazz.getMethods()) {
            if (method.isAnnotationPresent(Listen.class)) {
                Listen eventSubscriber = method.getAnnotation(Listen.class);
                Class<?>[] parameterTypes = method.getParameterTypes();
                if (parameterTypes.length == 1 && Event.class.isAssignableFrom(parameterTypes[0])) {
                    Class<? extends Event> eventClass = (Class<? extends Event>) parameterTypes[0];
                    MethodList methodList = subscribers.get(eventClass);
                    if (methodList == null) {
                        methodList = new MethodList();
                        subscribers.put(eventClass, methodList);
                    }
                    methodList.add(new LinkedMethod(listener, method, eventSubscriber.receiveCancelled()), eventSubscriber.priority());
                }
            }
        }
    }
}
