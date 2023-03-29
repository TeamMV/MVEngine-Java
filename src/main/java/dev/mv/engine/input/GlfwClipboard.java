package dev.mv.engine.input;

import dev.mv.engine.render.shared.Window;

import static org.lwjgl.glfw.GLFW.glfwGetClipboardString;
import static org.lwjgl.glfw.GLFW.glfwSetClipboardString;

public class GlfwClipboard {

    private long id;

    public GlfwClipboard(Window window) {
        id = window.getGlfwId();
    }

    public boolean hasContents() {
        String contents = getContents();
        return contents != null && !contents.isEmpty();
    }

    public String getContents() {
        return glfwGetClipboardString(id);
    }

    public void setContents(String content) {
        glfwSetClipboardString(id, content);
    }

}
