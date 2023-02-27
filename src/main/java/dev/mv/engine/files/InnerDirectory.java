package dev.mv.engine.files;

import java.io.File;

public class InnerDirectory extends Directory {

    InnerDirectory(String name, File folder) {
        super(name, folder);
    }

    @Override
    protected File getFolder() {
        return null;
    }
}
