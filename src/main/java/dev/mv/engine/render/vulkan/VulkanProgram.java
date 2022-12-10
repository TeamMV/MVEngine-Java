package dev.mv.engine.render.vulkan;

import java.util.ArrayList;
import java.util.List;

public class VulkanProgram {
    private static int nextShader = 0;
    private static List<VulkanShader> shaders = new ArrayList<>();
    private static int nextPipeline = 0;
    private static List<VulkanPipeline> pipelines = new ArrayList<>();
    private static int nextRenderPass = 0;
    private static List<VulkanRenderPass> renderPasses = new ArrayList<>();

    public static int genShader(VulkanShader shader) {
        shaders.add(nextShader, shader);
        shader.setId(nextShader);
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

    public static int genRenderPass(VulkanRenderPass renderPass) {
        renderPasses.add(nextRenderPass, renderPass);
        return nextRenderPass++;
    }

    public static VulkanRenderPass findRenderPass(int id) {
        return renderPasses.get(id);
    }

    public static void cleanupMess() {
        if (shaders.size() == 0) return;
        if (shaders.size() > pipelines.size()) {
            shaders.remove(--nextShader);
            if (renderPasses.size() > pipelines.size()) {
                renderPasses.remove(--nextRenderPass);
            }
        }
        else {
            shaders.remove(--nextShader);
            renderPasses.remove(--nextRenderPass);
            pipelines.remove(--nextPipeline);
        }
    }

    private int vulkanShader;
    private int vulkanPipeline;
    private int vulkanRenderPass;

    public VulkanProgram(int vulkanShader, int vulkanPipeline, int vulkanRenderPass) {
        this.vulkanShader = vulkanShader;
        this.vulkanPipeline = vulkanPipeline;
        this.vulkanRenderPass = vulkanRenderPass;
    }

    public int getVulkanShader() {
        return vulkanShader;
    }

    public int getVulkanPipeline() {
        return vulkanPipeline;
    }

    public int getVulkanRenderPass() {
        return vulkanRenderPass;
    }
}
