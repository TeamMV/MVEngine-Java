package dev.mv.engine.game.objects;

import dev.mv.utils.collection.Vec;

public interface SceneNode {
    SceneNode parent();
    Vec<SceneNode> children();
}
