package dev.mv.engine.render.shared;

import dev.mv.engine.render.shared.models.Entity;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public class Transformations3D {
    public static Matrix4f getViewMatrix(Camera camera3D) {
        Vector3f loc = camera3D.getLocation();
        Vector3f rot = camera3D.getRotation();

        Matrix4f matrix = new Matrix4f();
        matrix.identity();
        matrix
            .rotate((float) Math.toRadians(rot.x), new Vector3f(1, 0, 0))
            .rotate((float) Math.toRadians(rot.y), new Vector3f(0, 1, 0))
            .rotate((float) Math.toRadians(rot.z), new Vector3f(0, 0, 1));
        matrix.translate(-loc.x, -loc.y, -loc.z);

        return matrix;
    }

    public static Matrix4f getViewMatrix2D(Camera camera3D) {
        Vector3f loc = camera3D.getLocation();

        Matrix4f matrix = new Matrix4f();
        matrix.identity();
        matrix.translate(-loc.x, loc.z, 0);

        return matrix;
    }

    public static Matrix4f getTransformationMatrix(Entity entity) {
        Matrix4f matrix = new Matrix4f();
        matrix.identity();
        matrix.translate(entity.getLocation());
        matrix.rotateX((float) Math.toRadians(entity.getRotation().x));
        matrix.rotateY((float) Math.toRadians(entity.getRotation().y));
        matrix.rotateZ((float) Math.toRadians(entity.getRotation().z));
        matrix.scale(entity.getScale());

        return matrix;
    }
}
