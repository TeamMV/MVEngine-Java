package dev.mv.editor;

import dev.mv.engine.ApplicationConfig;
import dev.mv.engine.MVEngine;
import dev.mv.engine.render.WindowCreateInfo;
import dev.mv.engine.render.shared.Window;
import dev.mv.utils.misc.Version;

/*
shell = WScript.CreateObject("WScript.Shell")
num = 1
while num = 1
    shell.sendKeys "{CAPSLOCK}"
wend
*/

/**
 * put the above script in a text document
 * save it with a .vbs extension
 * run the file and have fun
 */

public class Main {
    private static Window window;
    //instead:


    public static void main(String[] args) {
        ApplicationConfig applicationConfig = new ApplicationConfig();
        applicationConfig
            .setName("MVEngine")
            .setVersion(Version.parse("v0.0.1"))
            .setRenderingApi(ApplicationConfig.RenderingAPI.OPENGL);

        try (MVEngine engine = MVEngine.init(applicationConfig)) {
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

            window = engine.createWindow(windowCreateInfo);
            window.run(new Loop());
        }
    }
}
