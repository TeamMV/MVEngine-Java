package dev.mv.engine.render.models;

import dev.mv.engine.render.drawables.Texture;
import org.joml.Vector4f;

public class Material {
    private Vector4f ambientColor, diffuseColor, specularColor;
    private float reflectance;
    private Texture texture;

    public static final Vector4f DEFAULT_COLOR = new Vector4f(1.0f, 1.0f, 1.0f, 1.0f);

    public Material() {
        this.ambientColor = DEFAULT_COLOR;
        this.diffuseColor = DEFAULT_COLOR;
        this.specularColor = DEFAULT_COLOR;
        this.texture = null;
        this.reflectance = 0;
    }

    public Material(Vector4f color, float reflectance) {
        this(color, color, color, reflectance, null);
    }

    public Material(Texture texture) {
        this(DEFAULT_COLOR, DEFAULT_COLOR, DEFAULT_COLOR, 0, texture);
    }

    public Material(Vector4f ambientColor, Vector4f diffuseColor, Vector4f specularColor, float reflectance, Texture texture) {
        this.ambientColor = ambientColor;
        this.diffuseColor = diffuseColor;
        this.specularColor = specularColor;
        this.reflectance = reflectance;
        this.texture = texture;
    }
}
