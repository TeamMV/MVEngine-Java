package dev.mv.engine.oldRender.window;

public interface Renderer {
    /**
     * gets called once when the method Window.run() is executed and if the interface Renderer is implemented in the main class
     */
    void start(Window w);

    /**
     * gets called every frame since the method Window.run() is executed and if the interface Renderer is implemented in the mian class
     */
    void render(Window w);

    void onClose(Window w);

    void renderAfter(Window w);

    void update(Window w);

    void resize(Window w, int width, int height);
}
