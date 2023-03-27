package dev.mv.engine;

import dev.mv.engine.exceptions.Exceptions;
import dev.mv.engine.exceptions.handle.ExceptionHandler;
import dev.mv.engine.input.Input;
import dev.mv.engine.input.InputCollector;
import dev.mv.engine.input.InputProcessor;
import dev.mv.engine.physics.Physics;
import dev.mv.engine.render.WindowCreateInfo;
import dev.mv.engine.render.opengl.OpenGLObjectLoader;
import dev.mv.engine.render.opengl.OpenGLWindow;
import dev.mv.engine.render.shared.Window;
import dev.mv.engine.render.shared.models.ObjectLoader;
import dev.mv.engine.render.vulkan.VulkanWindow;
import dev.mv.utils.misc.Version;
import org.lwjgl.glfw.GLFWErrorCallback;

import static org.lwjgl.glfw.GLFW.glfwInit;
import static org.lwjgl.glfw.GLFW.glfwTerminate;

public class MVEngine implements AutoCloseable {
    private static MVEngine instance;
    public String VERSION_STR = "v0.1.0";
    public Version VERSION = Version.parse(VERSION_STR);
    private ApplicationConfig.RenderingAPI renderingApi = ApplicationConfig.RenderingAPI.OPENGL;
    private ApplicationConfig applicationConfig;
    private InputCollector inputCollector;
    private ExceptionHandler exceptionHandler;

    private MVEngine() {
        exceptionHandler = ExceptionHandler.Default.INSTANCE;
    }

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
        instance = new MVEngine();
        Exceptions.readExceptionINI(MVEngine.class.getResourceAsStream("/assets/mvengine/exceptions.ini"));
        Input.init();
        if (Physics.init()) {
            throw new RuntimeException("Could not initialise NVIDIA Physx!");
        }

        instance.applicationConfig = config;
        if (config == null) {
            config = new ApplicationConfig();
        }
        GLFWErrorCallback.createPrint(System.err).set();

        if (!glfwInit()) {
            throw new RuntimeException("Could not initialise GLFW!");
        }

        if (config.getRenderingApi() == ApplicationConfig.RenderingAPI.VULKAN) {
            instance.renderingApi = ApplicationConfig.RenderingAPI.VULKAN;
        } else {
            instance.renderingApi = ApplicationConfig.RenderingAPI.OPENGL;
        }
        return instance;
    }

    public static void terminate() {
        instance().close();
    }

    public ApplicationConfig.RenderingAPI getRenderingApi() {
        return renderingApi;
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

    public void handleInputs(Window window) {
        InputProcessor inputProcessor = InputProcessor.defaultProcessor();
        inputCollector = new InputCollector(inputProcessor, window);
        inputCollector.start();
        Input.init();
    }

    public void setInputProcessor(InputProcessor inputProcessor) {
        inputCollector.setInputProcessor(inputProcessor);
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

    public ApplicationConfig getApplicationConfig() {
        return applicationConfig;
    }

    public ExceptionHandler getExceptionHandler() {
        return exceptionHandler;
    }

    public void setExceptionHandler(ExceptionHandler handler) {
        exceptionHandler = handler;
    }
}
