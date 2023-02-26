package dev.mv.test;

import java.io.File;

public class LogFileEliminator {
    static {
        File layoutDir = new File("src/..");
        if (!layoutDir.exists()) {
            layoutDir.mkdirs();
        }
        File[] files = layoutDir.listFiles((file, name) -> name.endsWith(".log"));
        for(File file : files) {
            if(file.exists()) {
                file.delete();
            }
        }
    }

    public static void __void__() {}
}
