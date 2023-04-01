package dev.mv.engine;

import dev.mv.engine.audio.Audio;
import dev.mv.engine.exceptions.Exceptions;
import dev.mv.engine.exceptions.handle.ExceptionHandler;
import dev.mv.engine.game.Game;
import dev.mv.engine.input.Input;
import dev.mv.engine.input.InputCollector;
import dev.mv.engine.input.InputProcessor;
import dev.mv.engine.physics.Physics2D;
import dev.mv.engine.physics.Physics3D;
import dev.mv.engine.render.WindowCreateInfo;
import dev.mv.engine.render.opengl.OpenGLObjectLoader;
import dev.mv.engine.render.opengl.OpenGLWindow;
import dev.mv.engine.render.shared.Window;
import dev.mv.engine.render.shared.models.ObjectLoader;
import dev.mv.engine.render.vulkan.VulkanWindow;
import dev.mv.engine.resources.ResourceLoader;
import dev.mv.utils.misc.Version;
import org.lwjgl.glfw.GLFWErrorCallback;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.glfw.GLFW.glfwInit;
import static org.lwjgl.glfw.GLFW.glfwTerminate;

public class MVEngine implements AutoCloseable {

    private static volatile MVEngine instance;

    public String VERSION_STR = "v0.1.0";
    public Version VERSION = Version.parse(VERSION_STR);
    private ApplicationConfig.RenderingAPI renderingApi = ApplicationConfig.RenderingAPI.OPENGL;
    private ApplicationConfig applicationConfig;
    private InputCollector inputCollector;
    private ExceptionHandler exceptionHandler;
    private Game game;
    private List<Looper> loopers;
    private Physics2D physics2D;
    private Physics3D physics3D;
    private Audio audio;

    private MVEngine() {
        exceptionHandler = ExceptionHandler.Default.INSTANCE;
        loopers = new ArrayList<>();
    }

    public static MVEngine instance() {
        MVEngine result = instance;
        if (result != null) {
            return result;
        }
        synchronized(MVEngine.class) {
            if (instance == null) {
                throw new IllegalStateException("MVEngine not initialised");
            }
            return instance;
        }
    }

    public static synchronized MVEngine init() {
        return init(new ApplicationConfig());
    }

    public static synchronized MVEngine init(ApplicationConfig config) {
        if (instance != null) {
            throw new IllegalStateException("MVEngine already initialised");
        }

        if (config == null) {
            config = new ApplicationConfig();
        }
        instance = new MVEngine();
        Exceptions.readExceptionINI(MVEngine.class.getResourceAsStream("/assets/mvengine/exceptions.ini"));
        Input.init();
        instance.audio = Audio.init(config.getSimultaneousAudioSources());
        boolean _2d = config.getDimension() == ApplicationConfig.GameDimension.ONLY_2D || config.getDimension() == ApplicationConfig.GameDimension.COMBINED;
        boolean _3d = config.getDimension() == ApplicationConfig.GameDimension.ONLY_3D || config.getDimension() == ApplicationConfig.GameDimension.COMBINED;
        if (_2d) {
            instance.physics2D = Physics2D.init();
        }
        if (_3d) {
            instance.physics3D = Physics3D.init();
            if (instance.physics3D == null) {
                Exceptions.send("PHYSX_INIT");
            }
        }

        instance.applicationConfig = config;
        GLFWErrorCallback.createPrint(System.err).set();

        if (!glfwInit()) {
            Exceptions.send("GLFW_INIT");
        }

        if (config.getRenderingApi() == ApplicationConfig.RenderingAPI.VULKAN) {
            instance.renderingApi = ApplicationConfig.RenderingAPI.VULKAN;
        } else {
            instance.renderingApi = ApplicationConfig.RenderingAPI.OPENGL;
        }

        ResourceLoader.markTexture("mqxf", "/assets/mvengine/textures/mqxf.png");
        ResourceLoader.markTexture("mqxfMuscle", "/assets/mvengine/textures/mqxf-muscle.png");
        ResourceLoader.markTexture("inflatableGuy", "/assets/mvengine/textures/inflatableGuy.png");

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
    public synchronized void close() {
        audio.terminate();
        if (physics2D != null) physics2D.terminate();
        if (physics3D != null) physics3D.terminate();
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

    public void setGame(Game game) {
        this.game = game;
    }

    public Game getGame() {
        return game;
    }

    public void registerLooper(Looper loopable) {
        loopers.add(loopable);
    }

    public List<Looper> getLoopers() {
        return loopers;
    }

    public Physics2D getPhysics2D() {
        return physics2D;
    }

    public Physics3D getPhysics3D() {
        return physics3D;
    }

    public Audio getAudio() {
        return audio;
    }

}
