package dev.mv.engine.test;

import dev.mv.engine.ApplicationConfig;
import dev.mv.engine.MVEngine;
import dev.mv.engine.files.ConfigFile;
import dev.mv.engine.files.Directory;
import dev.mv.engine.files.FileManager;
import dev.mv.engine.render.WindowCreateInfo;
import dev.mv.engine.render.shared.Window;
import dev.mv.utils.misc.Version;

import java.util.Arrays;

public class Main {
    public static void main(String[] args) throws Exception {
        Directory directory = FileManager.getDirectory("MVEngine");
        ConfigFile configFile = directory.getConfigFile("config.mvc");
        //configFile.setString("hello", "world");
        //configFile.setBoolean("modded", true);
        //configFile.setInt("version", Version.parse("v1.0.0").toVulkanVersion());
        //configFile.setFloat("pi", 3.14159f);
        //configFile.setBytes("bytes", new byte[]{1, 2, 3, 4, 5});
        //configFile.save();
        System.out.println(configFile.getString("hello"));
        System.out.println(configFile.getBoolean("modded"));
        System.out.println(Version.parse(configFile.getInt("version")));
        System.out.println(configFile.getFloat("pi"));
        System.out.println(Arrays.toString(configFile.getBytes("bytes")));
        configFile.save();

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