package dev.mv.engine.files;

import dev.mv.engine.exceptions.Exceptions;

import java.io.File;

public class InnerDirectory extends Directory {

    InnerDirectory(String name, String relativePath, File folder) {
        super(name, folder);
        this.relativePath = relativePath;
        if (!folder.exists()) {
            if (!folder.mkdirs()) {
                Exceptions.send("DIRECTORY_CREATE", folder.getAbsolutePath());
            }
        }
    }

    @Override
    protected File getFolder() {
        return folder;
    }
}
