package com.example.imageserver.data;

import java.io.File;

public class Folder {

    private final String key;
    private final File folder;

    public Folder(String key, File folder) {
        this.key = key;
        if (folder==null) throw new IllegalArgumentException();
        if (!folder.isDirectory()) throw new IllegalArgumentException();
        this.folder = folder.getAbsoluteFile();
    }

    public File getFile(String file) {
        File file_ = new File(folder, file);
        return file_.isFile() ? file_ : null;
    }

    String toOutputLine() {
        return "%s: %s".formatted( key, folder );
    }

    @Override
    public String toString() {
        return "Folder{" +
                "folder=" + folder +
                '}';
    }

    public String getName() {
        return folder.getName();
    }

    public String getPath() {
        return folder.getPath();
    }
}
