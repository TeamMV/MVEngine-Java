package dev.mv.engine;

import dev.mv.utils.misc.Version;

public class ApplicationConfig {

    private String name = "";
    private Version version = new Version(1);
    private RenderingAPI renderingApi = RenderingAPI.OPENGL;
    private GameDimension dimension = GameDimension.COMBINED;
    private int simultaneousAudioSources = 256;

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

    public int getSimultaneousAudioSources() {
        return simultaneousAudioSources;
    }

    public ApplicationConfig setSimultaneousAudioSources(int simultaneousAudioSources) {
        this.simultaneousAudioSources = simultaneousAudioSources;
        return this;
    }

    public enum RenderingAPI {
        OPENGL,
        VULKAN
    }

    public enum GameDimension {
        COMBINED,
        ONLY_2D,
        ONLY_3D;

        public boolean isValid() {
            if (this == COMBINED) return true;
            if (MVEngine.instance().getApplicationConfig().getDimension() == COMBINED) return true;
            return this == MVEngine.instance().getApplicationConfig().getDimension();
        }

        public boolean isCompatible(GameDimension other) {
            if (this == COMBINED || other == COMBINED) return true;
            return this == other;
        }
    }
}
