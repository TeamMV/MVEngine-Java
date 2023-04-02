package dev.mv.engine.game.event;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface EventBusListener {

    Bus[] bus() default Bus.MOD;

    ListenerType type() default ListenerType.STATIC;

}
