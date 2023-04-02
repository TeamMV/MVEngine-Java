package dev.mv.engine.game.objects;

import dev.mv.engine.ApplicationConfig;

public interface SceneNode {
    SceneNode parentNode();

    ApplicationConfig.GameDimension dimension();
}
