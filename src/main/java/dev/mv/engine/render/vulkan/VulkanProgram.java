package dev.mv.engine.render.vulkan;

import java.util.ArrayList;
import java.util.List;

public class VulkanProgram {
    private static int nextShader = 1;
    private static List<VulkanShader> shaders = new ArrayList<>();
    private static int nextPipeline = 1;
    private static List<VulkanPipeline> pipelines = new ArrayList<>();

    public static int genShader(VulkanShader shader) {
        shaders.add(nextShader, shader);
        return nextShader++;
    }

    public static VulkanShader findShader(int id) {
        return shaders.get(id);
    }

    public static int genPipeline(VulkanPipeline pipeline) {
        pipelines.add(nextPipeline, pipeline);
        return nextPipeline++;
    }

    public static VulkanPipeline findPipeline(int id) {
        return pipelines.get(id);
    }

    private int vulkanShader;
    private int vulkanPipeline;

    public VulkanProgram(int vulkanShader, int vulkanPipeline) {
        this.vulkanShader = vulkanShader;
        this.vulkanPipeline = vulkanPipeline;
    }
}
