package dev.mv.engine.game.objects;

import dev.mv.utils.collection.Vec;

public class Scene {
    private Vec<SceneNode> nodes;

    public Scene() {
        nodes = new Vec<>();
    }

    public void update() {
        for (SceneNode node : nodes) {
            if (node instanceof Actor a) {
                a.update();
            }
            if (node instanceof ContainerSceneNode c) {
                c.forEach(this::update);
            }
        }
    }

    private void update(SceneNode node) {
        if (node instanceof Actor a) {
            a.update();
        }
        if (node instanceof ContainerSceneNode c) {
            c.forEach(this::update);
        }
    }
}
