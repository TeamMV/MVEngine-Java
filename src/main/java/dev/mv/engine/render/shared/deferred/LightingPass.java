package dev.mv.engine.render.shared.deferred;

public interface LightingPass {
    public void render(int gPosition, int gNormal, int gAlbedoSpec);
}
