package dev.mv.engine.render.vulkan.shader.layouts;

import lombok.Getter;
import org.lwjgl.vulkan.VkVertexInputAttributeDescription;
import org.lwjgl.vulkan.VkVertexInputBindingDescription;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.lang.ClassLoader.getSystemClassLoader;
import static org.lwjgl.vulkan.VK10.VK_VERTEX_INPUT_RATE_VERTEX;

public class VulkanShaderLayoutInformation {
    @Getter
    private int size = 0;
    @Getter
    private List<VulkanShaderLayout> layouts;

    private VulkanShaderLayoutInformation() {
        layouts = new ArrayList<>();
    }

    public VkVertexInputAttributeDescription.Buffer getAttributeDescription() {
        VkVertexInputAttributeDescription.Buffer descriptions = VkVertexInputAttributeDescription.calloc(layouts.size());

        for (int i = 0; i < layouts.size(); i++) {
            descriptions.put(i, layouts.get(i).getDescription());
        }

        return descriptions.rewind();
    }

    public VkVertexInputBindingDescription.Buffer getBindingDescription() {
        VkVertexInputBindingDescription.Buffer descriptions = VkVertexInputBindingDescription.calloc(1);

        descriptions.get(0).binding(0);
        descriptions.get(0).stride(size);
        descriptions.get(0).inputRate(VK_VERTEX_INPUT_RATE_VERTEX);

        return descriptions;
    }

    public static VulkanShaderLayoutInformation retrieveInformation(String shaderFile) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(getSystemClassLoader().getResource(shaderFile).toExternalForm()));
        String line;
        int offset = 0;

        VulkanShaderLayoutInformation info = new VulkanShaderLayoutInformation();

        while((line = reader.readLine()) != null) {
            if(line.startsWith("layout")) {
                if(line.contains(" in ")) {
                    String binding = "0", location = "0", type = "undef";

                    String firstPiece = line.substring(0, line.indexOf(" in")).trim();
                    String lastPiece = line.substring(line.indexOf("in ")).trim();

                    String[] layoutAttribs = firstPiece.substring(firstPiece.indexOf("("), firstPiece.indexOf(")")).split("=");
                    List<String> pairs = new ArrayList<>();
                    for (String layoutAttrib : layoutAttribs) {
                        layoutAttrib = layoutAttrib.trim();
                        pairs.addAll(Arrays.asList(layoutAttrib.split(" ")));
                    }
                    int i = 0;
                    for(String pair : pairs) {
                        if(pair.equals("binding")) {
                            binding = pairs.get(i + 1);
                        }
                        if(pair.equals("location")) {
                            location = pairs.get(i + 1);
                        }

                        i++;
                    }

                    if(lastPiece.contains("(")) {
                        type = lastPiece.substring(0, lastPiece.indexOf("("));
                    } else {
                        type = lastPiece.substring(0, lastPiece.indexOf(" "));
                    }

                    VulkanShaderLayout layout = new VulkanShaderLayout(Integer.parseInt(location), Integer.parseInt(binding), offset, VulkanShaderLayout.ShaderDataType.get(type));
                    info.layouts.add(layout);
                    offset += layout.getSize();
                }
            }
        }
        info.size = offset;
        return info;
    }
}
