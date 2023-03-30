package dev.mv.engine.files.os;

import dev.mv.engine.files.Directory;
import dev.mv.utils.Utils;

import java.io.File;

public class UnixDirectory extends Directory {

    public UnixDirectory(String name) {
        super(name);
    }

    @Override
    protected File getFolder() {
        File dir = new File(Utils.getPath(System.getProperty("user.home"), ".local/share/" + getName()));
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
