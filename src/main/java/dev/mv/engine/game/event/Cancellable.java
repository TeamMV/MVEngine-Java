package dev.mv.engine.game.event;

public interface Cancellable {

    void setCancelled(boolean cancelled);

    boolean isCancelled();

}
