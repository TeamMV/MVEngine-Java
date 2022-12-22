package dev.mv.engine.input;

import dev.mv.engine.render.shared.Window;
import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;
import org.lwjgl.glfw.GLFWCursorPosCallback;
import org.lwjgl.glfw.GLFWMouseButtonCallback;
import org.lwjgl.glfw.GLFWScrollCallbackI;

import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import static org.lwjgl.glfw.GLFW.*;

public class InputCollector implements NativeKeyListener {
    private InputProcessor inputProcessor;
    private Window window;

    public InputCollector(InputProcessor inputProcessor, Window window) {
        this.inputProcessor = inputProcessor;
        this.window = window;
    }

    public void start() {
        glfwSetScrollCallback(window.getGlfwId(), new GLFWScrollCallbackI() {
            @Override
            public void invoke(long win, double xOffset, double yOffset) {
                inputProcessor.mouseScrollUpdate((int) xOffset, (int) yOffset);
            }
        });

        glfwSetMouseButtonCallback(window.getGlfwId(), new GLFWMouseButtonCallback() {
            @Override
            public void invoke(long win, int button, int action, int mods) {
                inputProcessor.mouseButtonUpdate(button, MouseAction.fromGlfwAction(action));
            }
        });

        glfwSetCursorPosCallback(window.getGlfwId(), new GLFWCursorPosCallback() {
            @Override
            public void invoke(long wind, double x, double y) {
                inputProcessor.mousePosUpdate((int) x, window.getHeight() - (int) y);
            }
        });

        //native hook

        try {
            GlobalScreen.registerNativeHook();
        } catch (NativeHookException ex) {
            System.err.println("There was a problem registering the native hook.");
            System.err.println(ex.getMessage());

            System.exit(1);
        }

        LogManager.getLogManager().reset();

        Logger logger = Logger.getLogger(GlobalScreen.class.getPackage().getName());
        logger.setLevel(Level.OFF);

        GlobalScreen.addNativeKeyListener(this);
    }

    @Override
    public void nativeKeyTyped(NativeKeyEvent nativeKeyEvent) {
        inputProcessor.keyUpdate(nativeKeyEvent.getRawCode(), KeyAction.TYPE);
    }

    @Override
    public void nativeKeyPressed(NativeKeyEvent nativeKeyEvent) {
        inputProcessor.keyUpdate(nativeKeyEvent.getRawCode(), KeyAction.PRESS);
    }

    @Override
    public void nativeKeyReleased(NativeKeyEvent nativeKeyEvent) {
        inputProcessor.keyUpdate(nativeKeyEvent.getRawCode(), KeyAction.RELEASE);
    }

    public enum KeyAction{
        PRESS,
        TYPE,
        RELEASE
    }

    public enum MouseAction{
        PRESS(GLFW_PRESS),
        RELEASE(GLFW_RELEASE);

        MouseAction(int action) {
        }

        public static MouseAction fromGlfwAction(int action) {
            return switch (action) {
                case GLFW_PRESS -> PRESS;
                case GLFW_RELEASE -> RELEASE;
                default -> null;
            };
        }
    }
}
