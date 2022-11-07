package dev.mv.engine.render.opengl.shader;

import dev.mv.engine.MVEngine;
import dev.mv.engine.render.light.DirectionalLight;
import dev.mv.engine.render.light.PointLight;
import dev.mv.engine.render.light.SpotLight;
import dev.mv.engine.render.models.Material;
import dev.mv.engine.render.utils.RenderUtils;
import lombok.Getter;
import lombok.Setter;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.BufferUtils;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.FloatBuffer;
import java.util.List;

import static org.lwjgl.opengl.GL20.*;

public class OpenGLShader {
    private String vertexCode;
    private String fragmentCode;
    @Getter
    @Setter
    private int vertexShader;
    @Getter
    @Setter
    private int fragmentShader;
    @Getter
    @Setter
    private int programID;

    public OpenGLShader(String vertexShader, String fragmentShader) {
        this.vertexCode = loadShaderFile(vertexShader);
        this.fragmentCode = loadShaderFile(fragmentShader);
    }

    private static String loadShaderFile(String fileStream) {
        try {
            return new String(new FileInputStream(fileStream).readAllBytes());
        } catch (IOException e) {
            MVEngine.Exceptions.Throw(e);
        }

        return null;
    }

    public void make() {
        this.programID = glCreateProgram();

        vertexShader = glCreateShader(GL_VERTEX_SHADER);
        glShaderSource(vertexShader, this.vertexCode);
        glCompileShader(vertexShader);
        if (glGetShaderi(vertexShader, GL_COMPILE_STATUS) != 1) {
            System.out.println("vertex shader error: " + glGetShaderInfoLog(vertexShader));
            return;
        }

        fragmentShader = glCreateShader(GL_FRAGMENT_SHADER);
        glShaderSource(fragmentShader, this.fragmentCode);
        glCompileShader(fragmentShader);
        if (glGetShaderi(fragmentShader, GL_COMPILE_STATUS) != 1) {
            System.out.println("fragment shader error: " + glGetShaderInfoLog(fragmentShader));
            return;
        }
    }

    public void use() {
        glAttachShader(this.programID, vertexShader);
        glAttachShader(this.programID, fragmentShader);

        glBindAttribLocation(this.programID, 0, "vertices");
        glLinkProgram(this.programID);
        if ((glGetProgrami(this.programID, GL_LINK_STATUS)) != 1) {
            System.out.println("link program error: " + glGetProgramInfoLog(this.programID));
            return;
        }
        glValidateProgram(this.programID);
        if ((glGetProgrami(this.programID, GL_VALIDATE_STATUS)) != 1) {
            System.out.println("link program error: " + glGetProgramInfoLog(this.programID));
            return;
        }

        glUseProgram(this.programID);
    }

    public int getProgramID() {
        return this.programID;
    }

    public void setUniform1f(String name, float value) {
        int location = glGetUniformLocation(this.programID, name);
        if (location != -1) {
            glUniform1f(location, value);
        }
    }

    public void setUniform1i(String name, int value) {
        int location = glGetUniformLocation(this.programID, name);
        if (location != -1) {
            glUniform1i(location, value);
        }
    }

    public void setUniform1iv(String name, int[] value) {
        int location = glGetUniformLocation(this.programID, name);
        if (location != -1) {
            glUniform1iv(location, value);
        }
    }

    public void setUniform2fv(String name, Vector2f value) {
        int location = glGetUniformLocation(this.programID, name);
        if (location != -1) {
            glUniform2fv(location, RenderUtils.array(value));
        }
    }

    public void setUniform3fv(String name, Vector3f value) {
        int location = glGetUniformLocation(this.programID, name);
        if (location != -1) {
            glUniform3fv(location, RenderUtils.array(value));
        }
    }

    public void setUniform4fv(String name, Vector4f value) {
        int location = glGetUniformLocation(this.programID, name);
        if (location != -1) {
            glUniform4fv(location, RenderUtils.array(value));
        }
    }

    public void setMatrix4f(String name, Matrix4f value) {
        int location = glGetUniformLocation(this.programID, name);
        FloatBuffer matBuffer = BufferUtils.createFloatBuffer(16);
        value.get(matBuffer);
        glUniformMatrix4fv(location, false, matBuffer);
    }

    public void setUniform1b(String name, boolean value) {
        setUniform1i(name, value ? 1 : 0);
    }

    public void setMaterial(String name, Material value) {
        setUniform1f(name + ".reflectance", value.getReflectance());
        setUniform4fv(name + ".ambient", value.getAmbientColor());
        setUniform4fv(name + ".diffuse", value.getDiffuseColor());
        setUniform4fv(name + ".specular", value.getSpecularColor());
        setUniform1b(name + ".hasTexture", value.hasTexture());
    }

    public void setDirectionalLight(String name, DirectionalLight value) {
        setUniform3fv(name + ".color", value.getColor());
        setUniform3fv(name + ".direction", value.getDirection());
        setUniform1f(name + ".intensity", value.getIntensity());
    }

    public void setPointLight(String name, PointLight value) {
        setUniform3fv(name + ".color", value.getColor());
        setUniform3fv(name + ".position", value.getPosition());
        setUniform1f(name + ".intensity", value.getIntensity());
        setUniform1f(name + ".constant", value.getConstant());
        setUniform1f(name + ".linear", value.getLinear());
        setUniform1f(name + ".exponent", value.getExponent());
    }

    public void setSpotLight(String name, SpotLight value) {
        setPointLight(name + ".pointLight", value.getPointLight());
        setUniform3fv(name + ".coneDirection", value.getConeDirection());
        setUniform1f(name + ".cutoff", value.getCutoff());
    }

    public void setPointLights(String name, List<PointLight> value) {
        int length = value != null ? value.size() : 0;

        for(int i = 0; i < length; i++) {
            setPointLight(name + "[" + i + "]", value.get(i));
        }
    }

    public void setSpotLights(String name, List<SpotLight> value) {
        int length = value != null ? value.size() : 0;

        for(int i = 0; i < length; i++) {
            setSpotLight(name + "[" + i + "]", value.get(i));
        }
    }
}
