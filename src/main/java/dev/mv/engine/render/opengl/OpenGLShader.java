package dev.mv.engine.render.opengl;

import dev.mv.engine.exceptions.Exceptions;
import dev.mv.engine.render.shared.Window;
import dev.mv.engine.render.shared.shader.Shader;
import dev.mv.engine.render.utils.RenderUtils;
import org.joml.*;
import org.lwjgl.BufferUtils;

import java.io.IOException;
import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL46.*;

public class OpenGLShader implements Shader {
    private String vertexCode;
    private String fragmentCode;
    private int vertexShader;
    private int fragmentShader;
    private int programID;

    public OpenGLShader(String vertexShader, String fragmentShader) {
        this.vertexCode = loadShaderFile(vertexShader);
        this.fragmentCode = loadShaderFile(fragmentShader);
    }

    private static String loadShaderFile(String fileStream) {
        try {
            return new String(OpenGLShader.class.getResourceAsStream(fileStream).readAllBytes());
        } catch (IOException e) {
            Exceptions.send(e);
        }

        return null;
    }

    @Override
    public void make(Window ignore) {
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

    @Override
    public void bind() {
        glAttachShader(this.programID, vertexShader);
        glAttachShader(this.programID, fragmentShader);

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
    }

    @Override
    public void use() {
        glUseProgram(this.programID);
    }

    public int getProgramID() {
        return this.programID;
    }

    public void setProgramID(int programID) {
        this.programID = programID;
    }

    @Override
    public void uniform(String name, float value) {
        int location = glGetUniformLocation(this.programID, name);
        if (location != -1) {
            glUniform1f(location, value);
        }
    }

    @Override
    public void uniform(String name, int value) {
        int location = glGetUniformLocation(this.programID, name);
        if (location != -1) {
            glUniform1i(location, value);
        }
    }

    @Override
    public void uniform(String name, float[] value) {
        int location = glGetUniformLocation(this.programID, name);
        if (location != -1) {
            glUniform1fv(location, value);
        }
    }

    @Override
    public void uniform(String name, int[] value) {
        int location = glGetUniformLocation(this.programID, name);
        if (location != -1) {
            glUniform1iv(location, value);
        }
    }

    @Override
    public void uniform(String name, Vector2f value) {
        int location = glGetUniformLocation(this.programID, name);
        if (location != -1) {
            glUniform2fv(location, RenderUtils.array(value));
        }
    }

    @Override
    public void uniform(String name, Vector3f value) {
        int location = glGetUniformLocation(this.programID, name);
        if (location != -1) {
            glUniform3fv(location, RenderUtils.array(value));
        }
    }

    @Override
    public void uniform(String name, Vector4f value) {
        int location = glGetUniformLocation(this.programID, name);
        if (location != -1) {
            glUniform4fv(location, RenderUtils.array(value));
        }
    }

    @Override
    public void uniform(String name, Matrix2f value) {
        int location = glGetUniformLocation(this.programID, name);
        FloatBuffer matBuffer = BufferUtils.createFloatBuffer(4);
        value.get(matBuffer);
        glUniformMatrix2fv(location, false, matBuffer);
    }

    @Override
    public void uniform(String name, Matrix3f value) {
        int location = glGetUniformLocation(this.programID, name);
        FloatBuffer matBuffer = BufferUtils.createFloatBuffer(9);
        value.get(matBuffer);
        glUniformMatrix3fv(location, false, matBuffer);
    }

    @Override
    public void uniform(String name, Matrix4f value) {
        int location = glGetUniformLocation(this.programID, name);
        FloatBuffer matBuffer = BufferUtils.createFloatBuffer(16);
        value.get(matBuffer);
        glUniformMatrix4fv(location, false, matBuffer);
    }

    @Override
    public void uniform(String name, boolean value) {
        uniform(name, value ? 1 : 0);
    }

    public int getVertexShader() {
        return vertexShader;
    }

    public void setVertexShader(int vertexShader) {
        this.vertexShader = vertexShader;
    }

    public int getFragmentShader() {
        return fragmentShader;
    }

    public void setFragmentShader(int fragmentShader) {
        this.fragmentShader = fragmentShader;
    }
}
