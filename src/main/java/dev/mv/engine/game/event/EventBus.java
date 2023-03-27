package dev.mv.engine.game.event;

public interface EventBus {

    void dispatch(Event event);

    void register(Object listener);

}
