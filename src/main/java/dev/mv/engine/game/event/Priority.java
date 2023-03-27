package dev.mv.engine.game.event;

public enum Priority {

    LOWEST,
    LOW,
    NORMAL,
    HIGH,
    HIGHEST;

    public static Priority[] getOrder() {
        return new Priority[]{HIGHEST, HIGH, NORMAL, LOW, LOWEST};
    }

}
