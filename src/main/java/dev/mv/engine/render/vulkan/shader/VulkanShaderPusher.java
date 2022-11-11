package dev.mv.engine.render.vulkan.shader;

import dev.mv.engine.render.vulkan.Vulkan;
import dev.mv.engine.render.vulkan.shader.buffer.VulkanVAO;
import dev.mv.engine.render.vulkan.shader.buffer.VulkanVBO;
import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.util.vma.VmaAllocationCreateInfo;
import org.lwjgl.vulkan.*;

import java.nio.ByteBuffer;
import java.nio.LongBuffer;
import java.util.List;

import static org.lwjgl.util.vma.Vma.VMA_MEMORY_USAGE_CPU_TO_GPU;
import static org.lwjgl.vulkan.VK10.*;

public class VulkanShaderPusher {
    private static long vertexBuffer;

    public static void createBuffer(VulkanVAO vao) {
        List<VulkanVBO> vbos = vao.getVbos();
        ByteBuffer container = vao.genContainer();

        try (MemoryStack stack = MemoryStack.stackPush()){
            VkBufferCreateInfo bufferCreateInfo = VkBufferCreateInfo.calloc(stack);
            bufferCreateInfo.sType(VK_STRUCTURE_TYPE_BUFFER_CREATE_INFO);
            bufferCreateInfo.size(container.limit());
            bufferCreateInfo.usage(VK_BUFFER_USAGE_VERTEX_BUFFER_BIT);
            bufferCreateInfo.sharingMode(VK_SHARING_MODE_EXCLUSIVE);

            LongBuffer pVertexBuffer = stack.mallocLong(1);

            VmaAllocationCreateInfo vmaAllocInfo = VmaAllocationCreateInfo.calloc(stack);
            vmaAllocInfo.usage(VMA_MEMORY_USAGE_CPU_TO_GPU);

            if(vkCreateBuffer(Vulkan.getLogicalDevice(), bufferCreateInfo, null, pVertexBuffer) != VK_SUCCESS) {
                throw new RuntimeException("Failed to create vertex buffer");
            }

            vertexBuffer = pVertexBuffer.get(0);

            VkMemoryRequirements memRequirements = VkMemoryRequirements.calloc(stack);
            vkGetBufferMemoryRequirements(Vulkan.getLogicalDevice(), vertexBuffer, memRequirements);


        }
    }

    public static void destroyBuffer() {
        vkDestroyBuffer(Vulkan.getLogicalDevice(), vertexBuffer, null);
    }

    public static void push(VulkanVAO vao, VkDevice device) {
        List<VulkanVBO> vbos = vao.getVbos();
        ByteBuffer container = vao.genContainer();


    }

    private static void memcpy(ByteBuffer buffer, List<VulkanVBO> data) {
        data.forEach(vbo -> buffer.put(vbo.getData()));
    }

}
