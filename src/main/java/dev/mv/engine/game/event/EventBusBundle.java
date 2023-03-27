package dev.mv.engine.game.event;

public class EventBusBundle implements EventBus {

    private final EventBus[] busses;

    public EventBusBundle(EventBus... busses) {
        this.busses = busses;
    }

    public void dispatch(Event event) {
        for (EventBus bus : busses) {
            bus.dispatch(event);
        }
    }

    public void register(Object listener) {
        for (EventBus bus : busses) {
            bus.register(listener);
        }
    }

}
