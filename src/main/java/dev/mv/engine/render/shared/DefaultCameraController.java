package dev.mv.engine.render.shared;

import dev.mv.engine.input.Input;

public class DefaultCameraController {
    private Camera camera;

    public DefaultCameraController(Camera camera) {
        this.camera = camera;
    }

    public void update() {
        if (Input.isKeyPressed(Input.KEY_W))            camera.move(0, 0, -1);
        if (Input.isKeyPressed(Input.KEY_A))            camera.move(-1, 0, 0);
        if (Input.isKeyPressed(Input.KEY_S))            camera.move(0, 0, 1);
        if (Input.isKeyPressed(Input.KEY_D))            camera.move(1, 0, 0);
        if (Input.isKeyPressed(Input.KEY_ARROW_LEFT))   camera.rotate(0, -1, 0);
        if (Input.isKeyPressed(Input.KEY_ARROW_RIGHT))  camera.rotate(0, 1, 0);
        if (Input.isKeyPressed(Input.KEY_ARROW_UP))     camera.rotate(-1, 0, 0);
        if (Input.isKeyPressed(Input.KEY_ARROW_DOWN))   camera.rotate(1, 0, 0);
        if (Input.isKeyPressed(Input.KEY_SPACE))        camera.move(0, 1, 0);
        if (Input.isKeyPressed(Input.KEY_SHIFT_LEFT))   camera.move(0, -1, 0);
    }
}
