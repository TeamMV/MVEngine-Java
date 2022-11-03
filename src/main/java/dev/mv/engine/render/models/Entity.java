package dev.mv.engine.render.models;

import org.joml.Vector3f;

public class Entity {
    private Model model;
    private Vector3f location, rotation;
    private float scale;

    public Entity(Model model, Vector3f location, Vector3f rotation, float scale) {
        this.model = model;
        this.location = location;
        this.rotation = rotation;
        this.scale = scale;
    }

    public void incrementPosition(float x, float y, float z) {
        location.x += x;
        location.y += y;
        location.z += z;
    }

    public void setPosition(float x, float y, float z) {
        location.x = x;
        location.y = y;
        location.z = z;
    }

    public void incrementRotation(float x, float y, float z) {
        rotation.x += x;
        rotation.y += y;
        rotation.z += z;
    }

    public void setRotation(float x, float y, float z) {
        rotation.x = x;
        rotation.y = y;
        rotation.z = z;
    }

    public void incrementScale(float factor) {
        scale += factor;
    }

    public void setScale(float value) {
        scale = value;
    }

    public Model getModel() {
        return model;
    }

    public Vector3f getLocation() {
        return location;
    }

    public Vector3f getRotation() {
        return rotation;
    }

    public float getScale() {
        return scale;
    }
}
