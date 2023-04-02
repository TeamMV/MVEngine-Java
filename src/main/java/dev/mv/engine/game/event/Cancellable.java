package dev.mv.engine.game.event;

public interface Cancellable {

    boolean isCancelled();

    void setCancelled(boolean cancelled);

}
