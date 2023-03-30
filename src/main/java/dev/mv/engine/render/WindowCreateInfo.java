package dev.mv.engine.render;

public class WindowCreateInfo {

    public int width = 800, height = 600, maxFPS = 60, maxUPS = 20;
    public boolean fullscreen = false, resizeable = true, vsync = true, appendFpsToTitle = false, decorated = true;
    public FPSAppendInfo fpsAppendConfiguration = new FPSAppendInfo();
    public String title = "";

    public WindowCreateInfo() {
    }

    public static class FPSAppendInfo {
        public String betweenTitleAndValue = " - ";
        public String afterValue = "fps";
    }
}