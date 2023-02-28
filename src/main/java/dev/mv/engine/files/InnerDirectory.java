package dev.mv.engine.files;

import java.io.File;

public class InnerDirectory extends Directory {

    InnerDirectory(String name, File folder) {
        super(name, folder);
        if (!folder.exists()) {
            folder.mkdirs();
        }
    }

    @Override
    protected File getFolder() {
        return folder;
    }
}
