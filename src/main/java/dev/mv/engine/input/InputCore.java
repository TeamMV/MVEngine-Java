package dev.mv.engine.input;

import dev.mv.engine.window.Window;
import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;
import org.joml.Vector2i;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFWCursorPosCallback;
import org.lwjgl.glfw.GLFWMouseButtonCallback;
import org.lwjgl.glfw.GLFWScrollCallbackI;

import java.nio.DoubleBuffer;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.GL_TRUE;

public class InputCore implements NativeKeyListener {

    private Window window;

    private DoubleBuffer mxPos = BufferUtils.createDoubleBuffer(1), myPos = BufferUtils.createDoubleBuffer(1);
    private int xScroll = 0, yScroll = 0;

    public InputCore(Window window) {
        this.window = window;

        glfwSetScrollCallback(window.getWindow(), new GLFWScrollCallbackI() {
            @Override
            public void invoke(long win, double xOffset, double yOffset) {
                xScroll = (int) xOffset;
                yScroll = (int) yOffset;
                window.onScroll(xScroll, yScroll);
            }
        });

        glfwSetMouseButtonCallback(window.getWindow(), new GLFWMouseButtonCallback() {
            @Override
            public void invoke(long win, int button, int action, int mods) {
                window.onMouseAction(button, action, mods);
            }
        });

        glfwSetCursorPosCallback(window.getWindow(), new GLFWCursorPosCallback() {
            @Override
            public void invoke(long wind, double x, double y) {
                window.onMouseMove((int) x, window.getHeight() - (int) y);
            }
        });

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

    public boolean keyDown(int key) {
        return glfwGetKey(window.getWindow(), key) == GL_TRUE;
    }

    public boolean mouseClick(int button) {
        return glfwGetMouseButton(window.getWindow(), button - 1) == GL_TRUE;
    }

    public Vector2i mousePosition() {
        glfwGetCursorPos(window.getWindow(), mxPos, myPos);
        return new Vector2i((int) mxPos.get(0), window.getHeight() - (int) myPos.get(0));
    }

    public boolean mouseInside(int x, int y, int x2, int y2) {
        glfwGetCursorPos(window.getWindow(), mxPos, myPos);
        int mx = (int) mxPos.get(0), my = window.getHeight() - (int) myPos.get(0);
        return (mx >= x && mx <= x2 &&
            my >= y && my <= y2);
    }

    public boolean scrollUp() {
        if (yScroll == 1) {
            yScroll = 0;
            return true;
        }
        return false;
    }

    public boolean scrollDown() {
        if (yScroll == -1) {
            yScroll = 0;
            return true;
        }
        return false;
    }

    @Override
    public void nativeKeyPressed(NativeKeyEvent nativeKeyEvent) {
        if (nativeKeyEvent.getRawCode() == 65288) {
            window.onKeyTyped('\b');
        }
        window.onKeyDown(nativeKeyEvent.getRawCode());
    }

    @Override
    public void nativeKeyReleased(NativeKeyEvent nativeKeyEvent) {
        window.onKeyUp(nativeKeyEvent.getRawCode());
    }

    @Override
    public void nativeKeyTyped(NativeKeyEvent nativeKeyEvent) {
        window.onKeyTyped((char) nativeKeyEvent.getRawCode());
    }
}
