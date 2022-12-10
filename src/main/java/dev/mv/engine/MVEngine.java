package dev.mv.engine;

import dev.mv.engine.render.WindowCreateInfo;
import dev.mv.engine.render.opengl.OpenGLObjectLoader;
import dev.mv.engine.render.opengl.OpenGLShader;
import dev.mv.engine.render.opengl.OpenGLWindow;
import dev.mv.engine.render.shared.Window;
import dev.mv.engine.render.shared.font.BitmapFont;
import dev.mv.engine.render.shared.models.ObjectLoader;
import dev.mv.engine.render.vulkan.Vulkan;
import dev.mv.engine.render.vulkan.VulkanWindow;
import dev.mv.utils.misc.Version;
import imgui.ImGui;
import lombok.Getter;
import org.lwjgl.glfw.GLFWErrorCallback;

import static org.lwjgl.glfw.GLFW.glfwInit;
import static org.lwjgl.glfw.GLFW.glfwTerminate;

public class MVEngine {
    public static String VERSION_STR = "v0.1.0";
    public static Version VERSION = Version.parse(VERSION_STR);

    private static ApplicationConfig.RenderingAPI renderingApi = ApplicationConfig.RenderingAPI.OPENGL;

    @Getter
    private static ApplicationConfig applicationConfig;

    public static ApplicationConfig.RenderingAPI getRenderingApi() {
        return renderingApi;
    }

    public static void init() {
        init(new ApplicationConfig());
    }

    public static void init(ApplicationConfig config) {
        applicationConfig = config;
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
            renderingApi = ApplicationConfig.RenderingAPI.VULKAN;
        } else {
            renderingApi = ApplicationConfig.RenderingAPI.OPENGL;
        }
    }

    public static void terminate() {
        ImGui.destroyContext();
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
            return new VulkanWindow(info);
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
