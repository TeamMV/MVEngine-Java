package dev.mv.engine.render.shared.models;

import dev.mv.engine.render.shared.texture.Texture;

public class Model {
    private int voaID;
    private int vertexCount;
    private Material material = new Material();
    public Maps maps = new Maps();

    public Model(int voaID, int vertexCount) {
        this.voaID = voaID;
        this.vertexCount = vertexCount;
    }

    public Model(int voaID, int vertexCount, Texture texture) {
        this.voaID = voaID;
        this.vertexCount = vertexCount;
        this.material = new Material(texture);
    }

    public Model(Model model, Texture texure) {
        this.voaID = model.getId();
        this.vertexCount = model.vertexCount();
        this.material = model.getMaterial();
        this.material.setTexture(texure);
    }

    public int getId() {
        return voaID;
    }

    public int vertexCount() {
        return vertexCount;
    }

    public Material getMaterial() {
        return material;
    }

    public void setMaterial(Material material) {
        this.material = material;
    }

    public Texture getTexture() {
        return material.getTexture();
    }

    public void setTexture(Texture texture) {
        material.setTexture(texture);
    }

    public void setTexture(Texture texture, float reflectance) {
        material.setTexture(texture);
        material.setReflectance(reflectance);
    }

    public class Maps {
        private Texture position;
        private Texture normal;
        private Texture specular;

        public Texture getPosition() {
            return position;
        }

        public void setPosition(Texture position) {
            this.position = position;
        }

        public Texture getNormal() {
            return normal;
        }

        public void setNormal(Texture normal) {
            this.normal = normal;
        }

        public Texture getSpecular() {
            return specular;
        }

        public void setSpecular(Texture specular) {
            this.specular = specular;
        }
    }
}
