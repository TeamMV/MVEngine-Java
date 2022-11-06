package dev.mv.engine.render.light;

import lombok.Getter;
import lombok.Setter;
import org.joml.Vector3f;

public class PointLight {
    @Getter @Setter
    private Vector3f position, color;
    @Getter @Setter
    private float intensity, constant, linear, exponent;

    public PointLight(Vector3f position, Vector3f color, float intensity, float constant, float linear, float exponent) {
        this.position = position;
        this.color = color;
        this.intensity = intensity;
        this.constant = constant;
        this.linear = linear;
        this.exponent = exponent;
    }


}
