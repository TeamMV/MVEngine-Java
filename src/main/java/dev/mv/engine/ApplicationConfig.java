package dev.mv.engine;

import dev.mv.utils.misc.Version;
import lombok.Getter;

public class ApplicationConfig {

    @Getter
    private String name = "";
    @Getter
    private Version version = new Version(1);
    @Getter
    private boolean vulkan = true;

    public ApplicationConfig setName(String name) {
        this.name = name;
        return this;
    }

    public ApplicationConfig setVersion(Version version) {
        this.version = version;
        return this;
    }

    public ApplicationConfig setVulkan(boolean vulkan) {
        this.vulkan = vulkan;
        return this;
    }
}
