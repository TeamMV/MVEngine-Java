package dev.mv.engine.render.shared.batch;

public enum BatchType {

    TRIANGLES,
    TRIANGLE_STRIP,
    QUADS,
    QUAD_STRIP;


    public static BatchType from(int vertCount) {
        return from(vertCount, false);
    }

    public static BatchType from(int vertCount, boolean strip) {
        return switch (vertCount) {
            case 3 -> strip ? TRIANGLE_STRIP : TRIANGLES;
            case 4 -> strip ? QUAD_STRIP : QUADS;
            default -> TRIANGLES;
        };
    }

}
