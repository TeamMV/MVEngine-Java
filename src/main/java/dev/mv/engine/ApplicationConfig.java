package dev.mv.engine;

import dev.mv.utils.misc.Version;

public class ApplicationConfig {

    private String name = "";
    private Version version = new Version(1);
    private RenderingAPI renderingApi = RenderingAPI.OPENGL;
    private GameDimension dimension = GameDimension.COMBINED;

    public String getName() {
        return name;
    }

    public ApplicationConfig setName(String name) {
        this.name = name;
        return this;
    }

    public Version getVersion() {
        return version;
    }

    public ApplicationConfig setVersion(Version version) {
        this.version = version;
        return this;
    }

    public RenderingAPI getRenderingApi() {
        return renderingApi;
    }

    public ApplicationConfig setRenderingApi(RenderingAPI renderingApi) {
        this.renderingApi = renderingApi;
        return this;
    }

    public GameDimension getDimension() {
        return dimension;
    }

    public ApplicationConfig setDimension(GameDimension dimension) {
        this.dimension = dimension;
        return this;
    }

    public enum RenderingAPI {
        OPENGL,
        VULKAN
    }

    public enum GameDimension {
        COMBINED,
        ONLY_2D,
        ONLY_3D
    }
}
