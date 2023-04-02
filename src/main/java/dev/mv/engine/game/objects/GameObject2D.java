package dev.mv.engine.game.objects;

import dev.mv.engine.ApplicationConfig;

public abstract class GameObject2D implements SceneNode {
    private SceneNode parent;

    @Override
    public SceneNode parentNode() {
        return parent;
    }

    @Override
    public final ApplicationConfig.GameDimension dimension() {
        return ApplicationConfig.GameDimension.ONLY_2D;
    }

}
