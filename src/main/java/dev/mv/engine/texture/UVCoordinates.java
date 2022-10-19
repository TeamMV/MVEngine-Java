package dev.mv.engine.texture;

import org.joml.Vector2f;

public class UVCoordinates {
    private Vector2f[] coords = new Vector2f[4];

    public UVCoordinates() {

    }

    public UVCoordinates(Vector2f BL, Vector2f TL, Vector2f TR, Vector2f BR) {
        coords[0] = BL;
        coords[1] = TL;
        coords[2] = TR;
        coords[3] = BR;
    }

    public Vector2f getBL() {
        return coords[0];
    }

    public void setBL(Vector2f uv) {
        coords[0] = uv;
    }

    public Vector2f getTL() {
        return coords[1];
    }

    public void setTL(Vector2f uv) {
        coords[1] = uv;
    }

    public Vector2f getTR() {
        return coords[2];
    }

    public void setTR(Vector2f uv) {
        coords[2] = uv;
    }

    public Vector2f getBR() {
        return coords[3];
    }

    public void setBR(Vector2f uv) {
        coords[3] = uv;
    }
}