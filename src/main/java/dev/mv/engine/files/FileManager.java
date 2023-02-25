package dev.mv.engine.files;

import dev.mv.engine.files.os.OsxDirectory;
import dev.mv.engine.files.os.UnixDirectory;
import dev.mv.engine.files.os.WindowsDirectory;

public class FileManager {

    public static Directory getDirectory(String name) {
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("win")) {
            return new WindowsDirectory(name);
        } else if (os.contains("mac") || os.contains("darwin") || os.contains("osx")) {
            return new OsxDirectory(name);
        } else if (os.contains("nix") || os.contains("nux") || os.contains("aix")) {
            return new UnixDirectory(name);
        }
        return null;
    }

}
