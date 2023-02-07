package dev.mv.editor;

import dev.mv.engine.ApplicationConfig;
import dev.mv.engine.MVEngine;
import dev.mv.engine.render.WindowCreateInfo;
import dev.mv.engine.render.shared.DrawContext3D;
import dev.mv.engine.render.shared.Window;
import dev.mv.utils.misc.Version;

public class Main {
    private static Window window;

    public static void main(String[] args) {
        ApplicationConfig applicationConfig = new ApplicationConfig();
        applicationConfig
            .setName("MVEngine")
            .setVersion(Version.parse("v0.0.1"))
            .setRenderingApi(ApplicationConfig.RenderingAPI.OPENGL);

        MVEngine.init(applicationConfig);

        WindowCreateInfo windowCreateInfo = new WindowCreateInfo();
        windowCreateInfo.title = "MVEngine";
        windowCreateInfo.width = 1200;
        windowCreateInfo.height = 1000;
        windowCreateInfo.maxFPS = 60;
        windowCreateInfo.maxUPS = 20;
        windowCreateInfo.appendFpsToTitle = true;
        windowCreateInfo.fpsAppendConfiguration.afterValue = " fps";
        windowCreateInfo.fpsAppendConfiguration.betweenTitleAndValue = " - ";
        windowCreateInfo.resizeable = true;
        windowCreateInfo.decorated = true;

        window = MVEngine.createWindow(windowCreateInfo);
        window.run(new Loop());
    }
}
