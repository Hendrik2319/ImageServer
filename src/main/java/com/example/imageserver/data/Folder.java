package com.example.imageserver.data;

import java.io.File;

public class Folder {

    private final File folder;

    public Folder(File folder) {
        if (folder==null) throw new IllegalArgumentException();
        if (!folder.isDirectory()) throw new IllegalArgumentException();
        this.folder = folder.getAbsoluteFile();
    }

    public File getFile(String file) {
        File file_ = new File(folder, file);
        return file_.isFile() ? file_ : null;
    }

    @Override
    public String toString() {
        return folder.toString();
    }

    public String getName() {
        return folder.getName();
    }
}
