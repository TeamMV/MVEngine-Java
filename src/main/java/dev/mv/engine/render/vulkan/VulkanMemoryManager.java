package dev.mv.engine.render.vulkan;

import dev.mv.utils.generic.Pair;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.util.vma.VmaAllocationCreateInfo;
import org.lwjgl.vulkan.VkBufferCreateInfo;
import org.lwjgl.vulkan.VkImageCreateInfo;
import org.lwjgl.vulkan.VkPhysicalDeviceMemoryProperties;

import java.nio.LongBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.util.vma.Vma.*;
import static org.lwjgl.vulkan.VK10.*;

public class VulkanMemoryManager {
    private VulkanWindow window;
    private List<Pair<Pair<Long, Long>, Integer>> freeableBuffers = new ArrayList<>();
    private List<Pair<VulkanBuffer.BufferInfo, Integer>> freeableBuffers2 = new ArrayList<>();

    private LongOpenHashSet buffers = new LongOpenHashSet();

    private int deviceMemory = 0;
    private int nativeMemory = 0;

    VulkanMemoryManager(VulkanWindow window) {
        this.window = window;
    }

    public void createBuffer(long size, int usage, int properties, LongBuffer pBuffer, PointerBuffer pBufferMemory) {
        try (MemoryStack stack = stackPush()) {
            VkBufferCreateInfo bufferInfo = VkBufferCreateInfo.calloc(stack);
            bufferInfo.sType(VK_STRUCTURE_TYPE_BUFFER_CREATE_INFO);
            bufferInfo.size(size);
            bufferInfo.usage(usage);
            //bufferInfo.sharingMode(VK_SHARING_MODE_EXCLUSIVE);

            VmaAllocationCreateInfo allocationInfo = VmaAllocationCreateInfo.calloc(stack);
            //allocationInfo.usage(VMA_MEMORY_USAGE_CPU_ONLY);
            allocationInfo.requiredFlags(properties);

            int result = vmaCreateBuffer(window.vulkan.allocator, bufferInfo, allocationInfo, pBuffer, pBufferMemory, null);
            if (result != VK_SUCCESS) {
                throw new RuntimeException("Failed to create buffer:" + result);
            }

//            LongBuffer pBufferMem = MemoryUtil.memLongBuffer(MemoryUtil.memAddressSafe(pBufferMemory), 1);
//
//            if(vkCreateBuffer(device, bufferInfo, null, pBuffer) != VK_SUCCESS) {
//                throw new RuntimeException("Failed to create vertex buffer");
//            }
//
//            VkMemoryRequirements memRequirements = VkMemoryRequirements.mallocStack(stack);
//            vkGetBufferMemoryRequirements(device, pBuffer.get(0), memRequirements);
//
//            VkMemoryAllocateInfo allocInfo = VkMemoryAllocateInfo.callocStack(stack);
//            allocInfo.sType(VK_STRUCTURE_TYPE_MEMORY_ALLOCATE_INFO);
//            allocInfo.allocationSize(memRequirements.size());
//            allocInfo.memoryTypeIndex(findMemoryType(memRequirements.memoryTypeBits(), properties));
//
//            if(vkAllocateMemory(device, allocInfo, null, pBufferMem) != VK_SUCCESS) {
//                throw new RuntimeException("Failed to allocate vertex buffer memory");
//            }
//
//            vkBindBufferMemory(device, pBuffer.get(0), pBufferMem.get(0), 0);

            if ((properties & VK_MEMORY_PROPERTY_DEVICE_LOCAL_BIT) > 0) {
                deviceMemory += size;
            } else {
                nativeMemory += size;
            }

            buffers.add(pBuffer.get(0));
        }
    }

    public void createImage(int width, int height, int mipLevel, int format, int tiling, int usage, int memProperties, LongBuffer pTextureImage, PointerBuffer pTextureImageMemory) {
        try (MemoryStack stack = stackPush()) {
            VkImageCreateInfo imageInfo = VkImageCreateInfo.calloc(stack);
            imageInfo.sType(VK_STRUCTURE_TYPE_IMAGE_CREATE_INFO);
            imageInfo.imageType(VK_IMAGE_TYPE_2D);
            imageInfo.extent().width(width);
            imageInfo.extent().height(height);
            imageInfo.extent().depth(1);
            imageInfo.mipLevels(mipLevel);
            imageInfo.arrayLayers(1);
            imageInfo.format(format);
            imageInfo.tiling(tiling);
            imageInfo.initialLayout(VK_IMAGE_LAYOUT_UNDEFINED);
            imageInfo.usage(usage);
            imageInfo.samples(VK_SAMPLE_COUNT_1_BIT);
            imageInfo.sharingMode(VK_SHARING_MODE_CONCURRENT);
            imageInfo.pQueueFamilyIndices(stack.ints(0, 1));

            VmaAllocationCreateInfo allocationInfo = VmaAllocationCreateInfo.callocStack(stack);
            //allocationInfo.usage(VMA_MEMORY_USAGE_CPU_ONLY);
            allocationInfo.requiredFlags(memProperties);

            vmaCreateImage(window.vulkan.allocator, imageInfo, allocationInfo, pTextureImage, pTextureImageMemory, null);

//            if(vkCreateImage(device, imageInfo, null, pTextureImage) != VK_SUCCESS) {
//                throw new RuntimeException("Failed to create image");
//            }
//
//            VkMemoryRequirements memRequirements = VkMemoryRequirements.mallocStack(stack);
//            vkGetImageMemoryRequirements(device, pTextureImage.get(0), memRequirements);
//
//            VkMemoryAllocateInfo allocInfo = VkMemoryAllocateInfo.callocStack(stack);
//            allocInfo.sType(VK_STRUCTURE_TYPE_MEMORY_ALLOCATE_INFO);
//            allocInfo.allocationSize(memRequirements.size());
//            allocInfo.memoryTypeIndex(findMemoryType(memRequirements.memoryTypeBits(), memProperties));
//
//            if(vkAllocateMemory(device, allocInfo, null, pTextureImageMemory) != VK_SUCCESS) {
//                throw new RuntimeException("Failed to allocate image memory");
//            }
//
//            vkBindImageMemory(device, pTextureImage.get(0), pTextureImageMemory.get(0), 0);
        }
    }

    public void MapAndCopy(long allocation, long bufferSize, Consumer<PointerBuffer> consumer) {

        try (MemoryStack stack = stackPush()) {
            PointerBuffer data = stack.mallocPointer(1);

//            vkMapMemory(Vulkan.getDevice(), allocation, 0, bufferSize, 0, data);
//            consumer.accept(data);
//            vkUnmapMemory(Vulkan.getDevice(), allocation);

            vmaMapMemory(window.vulkan.allocator, allocation, data);
            consumer.accept(data);
            vmaUnmapMemory(window.vulkan.allocator, allocation);
        }

    }

    public PointerBuffer Map(long allocation) {
        PointerBuffer data = MemoryUtil.memAllocPointer(1);

        vmaMapMemory(window.vulkan.allocator, allocation, data);

        return data;
    }

    public void Copy(PointerBuffer data, Consumer<PointerBuffer> consumer) {
        consumer.accept(data);
    }

    public void freeBuffer(long buffer, long allocation) {
//            vkFreeMemory(device, allocation, null);
//            vkDestroyBuffer(device, buffer, null);

        vmaDestroyBuffer(window.vulkan.allocator, buffer, allocation);

    }

    public void freeImage(long image, long allocation) {
//        vkFreeMemory(device, allocation, null);
//        vkDestroyBuffer(device, buffer, null);

        vmaDestroyImage(window.vulkan.allocator, image, allocation);
    }

    public synchronized void addToFreeable(long buffer, long allocation) {
        freeableBuffers.add(new Pair<>(new Pair<>(buffer, allocation), 0));
    }

    public synchronized void addToFreeable(VulkanBuffer buffer) {
        VulkanBuffer.BufferInfo bufferInfo = buffer.getBufferInfo();

        if (buffers.contains(bufferInfo.id())) {
            freeableBuffers2.add(new Pair<>(buffer.getBufferInfo(), 0));

            buffers.remove(bufferInfo.id());
        } else {
            System.err.println("trying to free not present buffer");
//            Thread.dumpStack();
        }

    }

    public synchronized void freeBuffers() {
        List<Pair<Pair<Long, Long>, Integer>> newList = new ArrayList<>();

        int waitCount = window.swapChain.framebuffers.size() + 1;
        for (Pair<Pair<Long, Long>, Integer> pair : freeableBuffers) {

            Integer count = pair.b;
            if (count >= waitCount) {
                freeBuffer(pair.a.a, pair.a.b);
            } else {
                pair.b = count + 1;
                newList.add(pair);
            }
        }

        freeableBuffers = newList;

        List<Pair<VulkanBuffer.BufferInfo, Integer>> newList2 = new ArrayList<>();

        waitCount = window.swapChain.framebuffers.size() + 1;
        for (Pair<VulkanBuffer.BufferInfo, Integer> pair : freeableBuffers2) {

            Integer count = pair.b;
            if (count >= waitCount) {
                freeBuffer(pair.a.id(), pair.a.allocation());

                if (pair.a.type() == VulkanMemoryType.Type.DEVICE_LOCAL) {
                    deviceMemory -= pair.a.bufferSize();
                } else {
                    nativeMemory -= pair.a.bufferSize();
                }
            } else {
                pair.b = count + 1;
                newList2.add(pair);
            }
        }

        freeableBuffers2 = newList2;
    }

    public int findMemoryType(int typeFilter, int properties) {
        VkPhysicalDeviceMemoryProperties memProperties = VkPhysicalDeviceMemoryProperties.mallocStack();
        vkGetPhysicalDeviceMemoryProperties(window.GPUWrapper.getPhysicalDevice(), memProperties);

        for (int i = 0; i < memProperties.memoryTypeCount(); i++) {
            if ((typeFilter & (1 << i)) != 0 && (memProperties.memoryTypes(i).propertyFlags() & properties) == properties) {
                return i;
            }
        }

        throw new RuntimeException("Failed to find suitable memory type");
    }

    public int getNativeMemory() {
        return nativeMemory;
    }

    public int getDeviceMemory() {
        return deviceMemory;
    }
}
