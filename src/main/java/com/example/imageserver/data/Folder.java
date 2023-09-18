package com.example.imageserver.data;

import lombok.Getter;

import java.io.File;
import java.util.Arrays;
import java.util.List;

public class Folder {

    @Getter
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

    @SuppressWarnings("unused")
    public String getName() {
        return folder.getName();
    }

    public String getPath() {
        return folder.getPath();
    }

    public List<String> getFiles(int i0, int i1) {
        File[] files = folder.listFiles(File::isFile);
        if (files==null || files.length<=i0 || i1<=0 || i1<=i0)
            return List.of();
        i1 = Math.min(i1, files.length);
        i0 = Math.max(0, i0);
        files = Arrays.copyOfRange(files, i0, i1);
        return Arrays.stream(files).map(File::getName).toList();
    }
}
