package dev.mv.engine;

import dev.mv.engine.render.WindowCreateInfo;
import dev.mv.engine.render.opengl.OpenGLObjectLoader;
import dev.mv.engine.render.opengl.OpenGLShader;
import dev.mv.engine.render.opengl.OpenGLWindow;
import dev.mv.engine.render.shared.Window;
import dev.mv.engine.render.shared.font.BitmapFont;
import dev.mv.engine.render.shared.models.ObjectLoader;
import imgui.ImGui;
import org.lwjgl.glfw.GLFWErrorCallback;

import static org.lwjgl.glfw.GLFW.glfwInit;
import static org.lwjgl.glfw.GLFW.glfwTerminate;

public class MVEngine {
    private static ApplicationConfig.RenderingAPI renderingApi = ApplicationConfig.RenderingAPI.OPENGL;

    public static ApplicationConfig.RenderingAPI getRenderingApi() {
        return renderingApi;
    }

    public static void init() {
        init(new ApplicationConfig());
    }

    public static void init(ApplicationConfig config) {
        if (config == null) {
            config = new ApplicationConfig();
        }
        GLFWErrorCallback.createPrint(System.err).set();

        if (!glfwInit()) {
            throw new RuntimeException("Could not initialise GLFW!");
        }

        ImGui.createContext();
        ImGui.styleColorsDark();

        if (config.getRenderingApi() == ApplicationConfig.RenderingAPI.VULKAN) {
            /*if (Vulkan.init(config.getName(), config.getVersion())) {
                renderingApi = ApplicationConfig.RenderingAPI.VULKAN;
            }*/
        } else {
            renderingApi = ApplicationConfig.RenderingAPI.OPENGL;
        }
    }

    public static void terminate() {
        ImGui.destroyContext();
        if (renderingApi == ApplicationConfig.RenderingAPI.OPENGL) {
            //Vulkan.terminate();
        }
        glfwTerminate();
    }

    public static void rollbackRenderingApi() {
        if (renderingApi != ApplicationConfig.RenderingAPI.OPENGL) {
            renderingApi = ApplicationConfig.RenderingAPI.OPENGL;
        }
    }

    public static Window createWindow(WindowCreateInfo info) {
        if (info == null) {
            info = new WindowCreateInfo();
        }

        if (renderingApi == ApplicationConfig.RenderingAPI.VULKAN) {
            return null;
        } else {
            return new OpenGLWindow(info);
        }
    }


    public static ObjectLoader getObjectLoader() {
        if (renderingApi == ApplicationConfig.RenderingAPI.VULKAN) {
            return null;
        } else {
            return OpenGLObjectLoader.instance();
        }
    }

    public static class Exceptions {
        public static void Throw(Throwable throwable) {
            throw new RuntimeException(throwable);
        }
    }
}
