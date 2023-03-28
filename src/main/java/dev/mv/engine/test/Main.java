package dev.mv.engine.test;

import dev.mv.engine.ApplicationConfig;
import dev.mv.engine.MVEngine;
import dev.mv.engine.render.WindowCreateInfo;
import dev.mv.engine.render.shared.Window;
import dev.mv.engine.render.utils.ClipBoardListener;
import dev.mv.utils.misc.Version;

public class Main {
    public static void main(String[] args) throws Exception {
        ClipBoardListener.init();
        ApplicationConfig config = new ApplicationConfig();
        config.setName("FactoryIsland").setVersion(Version.parse("v0.0.1")).setRenderingApi(ApplicationConfig.RenderingAPI.OPENGL);
        try (MVEngine engine = MVEngine.init(config)) {
            WindowCreateInfo info = new WindowCreateInfo();
            info.resizeable = true;
            info.appendFpsToTitle = false;
            info.title = "FactoryIsland";
            info.vsync = true;
            info.maxFPS = 60;
            info.maxUPS = 30;
            info.decorated = true;
            info.width = 1000;
            info.height = 800;
            Window window = engine.createWindow(info);
            window.run(Test.INSTANCE);
        }
    }
}
