package dev.mv.engine.input;

import dev.mv.engine.render.shared.Window;
import org.lwjgl.glfw.*;

import static org.lwjgl.glfw.GLFW.*;

public class InputCollector {
    private InputProcessor inputProcessor;
    private Window window;

    public InputCollector(InputProcessor inputProcessor, Window window) {
        this.inputProcessor = inputProcessor;
        this.window = window;
    }

    public void start() {
        glfwSetInputMode(window.getGlfwId(), GLFW_LOCK_KEY_MODS, GLFW_TRUE);

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

        glfwSetKeyCallback(window.getGlfwId(), new GLFWKeyCallback() {
            @Override
            public void invoke(long window, int key, int scancode, int action, int mods) {
                if (action == GLFW_PRESS) {
                    inputProcessor.keyUpdate(key, KeyAction.TYPE, mods);
                } else if (action == GLFW_RELEASE) {
                    inputProcessor.keyUpdate(key, KeyAction.RELEASE, mods);
                } else if (action == GLFW_REPEAT) {
                    inputProcessor.keyUpdate(key, KeyAction.REPEAT, mods);
                }
            }
        });

        glfwSetCharCallback(window.getGlfwId(), new GLFWCharCallback() {
            @Override
            public void invoke(long window, int codepoint) {
                inputProcessor.charTyped(codepoint);
            }
        });
    }

    public void setInputProcessor(InputProcessor inputProcessor) {
        this.inputProcessor = inputProcessor;
    }

    public enum KeyAction {
        TYPE,
        REPEAT,
        RELEASE
    }

    public enum MouseAction {
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
