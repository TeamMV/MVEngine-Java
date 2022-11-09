package dev.mv.engine.render.light;

import lombok.Getter;
import lombok.Setter;
import org.joml.Vector3f;

public class PointLight {
    @Getter
    @Setter
    private Vector3f position, color;
    @Getter
    @Setter
    private float intensity, constant, linear, exponent;

    public PointLight(Vector3f position, Vector3f color, float intensity, float constant, float linear, float exponent) {
        this.position = position;
        this.color = color;
        this.intensity = intensity;
        this.constant = constant;
        this.linear = linear;
        this.exponent = exponent;
    }

    public PointLight(Vector3f position, Vector3f color, float intensity) {
        this(position, color, intensity, 1, 0, 0);
    }

    public Vector3f getPosition() {
        return position;
    }

    public void setPosition(Vector3f position) {
        this.position = position;
    }

    public Vector3f getColor() {
        return color;
    }

    public void setColor(Vector3f color) {
        this.color = color;
    }

    public float getIntensity() {
        return intensity;
    }

    public void setIntensity(float intensity) {
        this.intensity = intensity;
    }

    public float getConstant() {
        return constant;
    }

    public void setConstant(float constant) {
        this.constant = constant;
    }

    public float getLinear() {
        return linear;
    }

    public void setLinear(float linear) {
        this.linear = linear;
    }

    public float getExponent() {
        return exponent;
    }

    public void setExponent(float exponent) {
        this.exponent = exponent;
    }
}
