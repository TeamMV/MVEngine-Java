package dev.mv.engine;

import dev.mv.engine.physics.Physics;
import dev.mv.engine.render.WindowCreateInfo;
import dev.mv.engine.render.opengl.OpenGLObjectLoader;
import dev.mv.engine.render.opengl.OpenGLWindow;
import dev.mv.engine.render.shared.Window;
import dev.mv.engine.render.shared.models.ObjectLoader;
import dev.mv.engine.render.vulkan.VulkanWindow;
import dev.mv.utils.misc.Version;
import lombok.Getter;
import org.lwjgl.glfw.GLFWErrorCallback;

import static org.lwjgl.glfw.GLFW.glfwInit;
import static org.lwjgl.glfw.GLFW.glfwTerminate;

public class MVEngine implements AutoCloseable {
    public static String VERSION_STR = "v0.1.0";
    public static Version VERSION = Version.parse(VERSION_STR);

    private static ApplicationConfig.RenderingAPI renderingApi = ApplicationConfig.RenderingAPI.OPENGL;

    @Getter
    private static ApplicationConfig applicationConfig;

    public static ApplicationConfig.RenderingAPI getRenderingApi() {
        return renderingApi;
    }

    private static MVEngine instance;

    private MVEngine() {}

    public static MVEngine instance() {
        if (instance == null) {
            throw new IllegalStateException("MVEngine not initialised");
        }
        return instance;
    }

    public static MVEngine init() {
        return init(new ApplicationConfig());
    }

    public static MVEngine init(ApplicationConfig config) {
        if (Physics.init()) {
            throw new RuntimeException("Could not initialise NVIDIA Physx!");
        }

        applicationConfig = config;
        if (config == null) {
            config = new ApplicationConfig();
        }
        GLFWErrorCallback.createPrint(System.err).set();

        if (!glfwInit()) {
            throw new RuntimeException("Could not initialise GLFW!");
        }

        if (config.getRenderingApi() == ApplicationConfig.RenderingAPI.VULKAN) {
            renderingApi = ApplicationConfig.RenderingAPI.VULKAN;
        } else {
            renderingApi = ApplicationConfig.RenderingAPI.OPENGL;
        }
        instance = new MVEngine();
        return instance;
    }

    public void rollbackRenderingApi() {
        if (renderingApi != ApplicationConfig.RenderingAPI.OPENGL) {
            renderingApi = ApplicationConfig.RenderingAPI.OPENGL;
        }
    }

    public Window createWindow(WindowCreateInfo info) {
        if (info == null) {
            info = new WindowCreateInfo();
        }

        if (renderingApi == ApplicationConfig.RenderingAPI.VULKAN) {
            return new VulkanWindow(info);
        } else {
            return new OpenGLWindow(info);
        }
    }


    public ObjectLoader getObjectLoader() {
        if (renderingApi == ApplicationConfig.RenderingAPI.VULKAN) {
            return null;
        } else {
            return OpenGLObjectLoader.instance();
        }
    }

    @Override
    public void close() {
        Physics.terminate();
        glfwTerminate();
        instance = null;
    }

    public static void terminate() {
        instance().close();
    }

    public static class Exceptions {
        public static void __throw__(Throwable throwable) {
            throw new RuntimeException(throwable);
        }
    }
}
