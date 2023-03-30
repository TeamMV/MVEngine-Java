package dev.mv.engine.game;

import dev.mv.engine.MVEngine;
import dev.mv.engine.render.WindowCreateInfo;
import dev.mv.engine.render.shared.Window;

import java.util.HashMap;
import java.util.Map;

public class GameManager {

    private Game game;
    private Window mainWindow;
    private Map<String, Window> windows = new HashMap<>();

    GameManager(Game game) {
        this.game = game;
    }

    public void mainWindow(WindowStyle... styles) {
        mainWindow = MVEngine.instance().createWindow(parse(styles));
        mainWindow.run(new WindowManager(game));
    }

    //public void extraWindow(String id, WindowStyle... styles) {
    //    if (mainWindow == null) {
    //        //mainWindow(styles);
    //        Exceptions.send("Main window needs to be present when making extra windows!");
    //        return;
    //    }
    //    Window window = MVEngine.instance().createWindow(parse(styles));
    //    windows.put(id, window);
    //    Utils.async(() -> window.run(new WindowManager(game)));
    //}

    private WindowCreateInfo parse(WindowStyle... styles) {
        WindowCreateInfo info = new WindowCreateInfo();
        info.title = game.getName();
        info.maxUPS = 20;
        info.decorated = true;
        info.fullscreen = false;
        info.resizeable = true;
        info.height = 600;
        info.width = 800;
        info.appendFpsToTitle = false;
        info.maxFPS = game.config().getInt("fps");
        info.vsync = game.config().getBoolean("vsync");
        for (WindowStyle style : styles) {
            switch (style.type) {
                case WIDTH -> info.width = (int) style.data;
                case HEIGHT -> info.height = (int) style.data;
                case DECORATED -> info.decorated = (boolean) style.data;
                case FULLSCREEN -> info.fullscreen = (boolean) style.data;
                case RESIZEABLE -> info.resizeable = (boolean) style.data;
                case UPS -> info.maxUPS = (int) style.data;
                case TITLE -> info.title = (String) style.data;
                case FPS_TITLE -> {
                    info.appendFpsToTitle = true;
                    info.fpsAppendConfiguration = (WindowCreateInfo.FPSAppendInfo) style.data;
                }
            }
        }
        return info;
    }

    public void setFPS(int fps) {
        game.config().setInt("fps", fps);
        if (mainWindow != null) {
            mainWindow.setFPSCap(fps);
        }
        //windows.forEach((s, w) -> w.setFPSCap(fps));
    }

    public void setVsync(boolean vsync) {
        game.config().setBoolean("vsync", vsync);
        if (mainWindow != null) {
            mainWindow.setVsync(vsync);
        }
        //windows.forEach((s, w) -> w.setVsync(vsync));
    }

    public Window getMainWindow() {
        return mainWindow;
    }

    public Window getExtraWindow(String id) {
        return windows.get(id);
    }

}
