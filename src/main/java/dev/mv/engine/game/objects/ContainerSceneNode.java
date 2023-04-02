package dev.mv.engine.game.objects;

import dev.mv.utils.collection.Vec;

import java.util.function.Consumer;
import java.util.function.Predicate;

public interface ContainerSceneNode extends SceneNode {
    Vec<SceneNode> children = new Vec<>();

    default void addNode(SceneNode node) {
        if (dimension().isCompatible(node.dimension())) {
            children.push(node);
        }
    }

    default void addNodes(Vec<SceneNode> nodes) {
        nodes.retain(node -> dimension().isCompatible(node.dimension()));
        children.append(nodes);
    }

    default void removeNode(SceneNode node) {
        children.remove(node);
    }

    default void removeNodes(Vec<SceneNode> nodes) {
        for (SceneNode node : nodes) {
            removeNode(node);
        }
    }

    default void removeNodes(Predicate<SceneNode> condition) {
        children.retain(n -> !condition.test(n));
    }

    default boolean containsNode(SceneNode node) {
        return children.contains(node);
    }

    default boolean containsAny(Vec<SceneNode> nodes) {
        for (SceneNode node : nodes) {
            if (containsNode(node)) return true;
        }
        return false;
    }

    default boolean containsAll(Vec<SceneNode> nodes) {
        return children.containsAll(nodes);
    }

    default void forEach(Consumer<SceneNode> node) {
        children.forEach(node);
    }

    @Override
    boolean equals(Object other);
}
