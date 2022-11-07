package dev.mv.engine.render.opengl._3d.object;

import dev.mv.engine.render.drawables.Texture;
import dev.mv.engine.render.models.Material;
import dev.mv.engine.render.models.Model;

public class OpenGLModel implements Model {
    private int voaID;
    private int vertexCount;
    private Material material = new Material();

    public OpenGLModel(int voaID, int vertexCount) {
        this.voaID = voaID;
        this.vertexCount = vertexCount;
    }

    public OpenGLModel(int voaID, int vertexCount, Texture texture) {
        this.voaID = voaID;
        this.vertexCount = vertexCount;
        this.material = new Material(texture);
    }

    public OpenGLModel(Model model, Texture texure) {
        this.voaID = model.getId();
        this.vertexCount = model.vertexCount();
        this.material = model.getMaterial();
        this.material.setTexture(texure);
    }

    @Override
    public int getId() {
        return voaID;
    }

    @Override
    public int vertexCount() {
        return vertexCount;
    }

    @Override
    public Material getMaterial() {
        return material;
    }

    @Override
    public void setMaterial(Material material) {
        this.material = material;
    }

    @Override
    public Texture getTexture() {
        return material.getTexture();
    }

    @Override
    public void setTexture(Texture texture) {
        material.setTexture(texture);
    }

    @Override
    public void setTexture(Texture texture, float reflectance) {
        material.setTexture(texture);
        material.setReflectance(reflectance);
    }
}
