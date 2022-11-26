package dev.mv.engine.render.shared.shader;

import dev.mv.engine.render.shared.models.Material;

public class Uniforms3D {
    public static void material(Shader shader, Material material, String materialName) {
        shader.uniform(materialName + ".reflectance", material.getReflectance());
        shader.uniform(materialName + ".ambient", material.getAmbientColor());
        shader.uniform(materialName + ".diffuse", material.getDiffuseColor());
        shader.uniform(materialName + ".specular", material.getSpecularColor());
        shader.uniform(materialName + ".hasTexture", material.hasTexture());
    }
}
