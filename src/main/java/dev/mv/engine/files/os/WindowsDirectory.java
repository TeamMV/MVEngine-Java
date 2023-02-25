package dev.mv.engine.files.os;

import dev.mv.engine.files.Directory;
import dev.mv.utils.Utils;

import java.io.File;

public class WindowsDirectory extends Directory {

    public WindowsDirectory(String name) {
        super(name);
    }

    @Override
    protected File getFolder() {
        File dir = new File(Utils.getPath(System.getenv("APPDATA"), "." + getName()));
        if (!dir.exists()) {
            dir.mkdirs();
        }
        if (!dir.isDirectory()) {
            dir.delete();
            dir.mkdirs();
        }
        return dir;
    }
}
