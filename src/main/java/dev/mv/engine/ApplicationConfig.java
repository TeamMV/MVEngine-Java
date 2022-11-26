package dev.mv.engine;

import dev.mv.utils.misc.Version;
import lombok.Getter;

public class ApplicationConfig {

    @Getter
    private String name = "";
    @Getter
    private Version version = new Version(1);
    @Getter
    private RenderingAPI renderingApi = RenderingAPI.OPENGL;

    public ApplicationConfig setName(String name) {
        this.name = name;
        return this;
    }

    public ApplicationConfig setVersion(Version version) {
        this.version = version;
        return this;
    }

    public ApplicationConfig setRenderingApi(RenderingAPI renderingApi) {
        this.renderingApi = renderingApi;
        return this;
    }

    public static enum RenderingAPI {
        OPENGL,
        VULKAN
    }
}
