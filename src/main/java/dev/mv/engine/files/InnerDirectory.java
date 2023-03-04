package dev.mv.engine.files;

import java.io.File;

public class InnerDirectory extends Directory {

    InnerDirectory(String name, String relativePath, File folder) {
        super(name, folder);
        this.relativePath = relativePath;
        if (!folder.exists()) {
            folder.mkdirs();
        }
    }

    @Override
    protected File getFolder() {
        return folder;
    }
}
