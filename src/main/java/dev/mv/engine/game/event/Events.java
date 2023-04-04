package dev.mv.engine.game.event;

import dev.mv.engine.exceptions.Exceptions;
import dev.mv.utils.Utils;

public class Events {

    public static final EventBus CORE_BUS = new StandardEventBus(Bus.CORE);

    public static final EventBus MOD_BUS = new StandardEventBus(Bus.MOD);

    public static void init() {
        Utils.getAllClasses(i -> !Utils.containsAny(i, "dev.mv.engine",
                "dev.mv.utils", "org.lwjgl", "de.fabmax.physxjni", "physx.",
                "org.joml", "com.codedisaster.steamworks", "javax.annotation",
                "org.jetbrains.annotations", "org.intellij"))
            .fastIter().forEach(clazz -> {
                if (clazz.isAnnotationPresent(EventBusListener.class)) {
                    EventBusListener listener = clazz.getAnnotation(EventBusListener.class);
                    if (listener.type() == ListenerType.STATIC) {
                        try {
                            Object instance = clazz.getConstructor().newInstance();
                            bus(listener.bus()).register(instance);
                        } catch (Exception ignore) {
                            Exceptions.send("STATIC_LISTENER_INIT", clazz.getName());
                        }
                    }
                }
            });
    }

    public static void register(Object instance) {
        Class<?> clazz = instance.getClass();
        if (clazz.isAnnotationPresent(EventBusListener.class)) {
            EventBusListener listener = clazz.getAnnotation(EventBusListener.class);
            bus(listener.bus()).register(listener);
        }
    }

    public static EventBus bus(Bus... bus) {
        EventBus[] buses = new EventBus[bus.length];
        for (int i = 0; i < bus.length; i++) {
            buses[i] = singleBus(bus[i]);
        }
        return new EventBusBundle(buses);
    }

    private static EventBus singleBus(Bus bus) {
        return switch (bus) {
            case CORE -> CORE_BUS;
            case MOD -> MOD_BUS;
        };
    }

}
