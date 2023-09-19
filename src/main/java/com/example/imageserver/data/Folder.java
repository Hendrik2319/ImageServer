package com.example.imageserver.data;

import lombok.Getter;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Stream;

public class Folder {

    @Getter
    private final String key;
    private final File folder;
    private final Map<String,FileData> filesMap;
    private final List<FileData> filesList;

    public Folder(String key, File folder) {
        this.key = key;
        if (folder==null) throw new IllegalArgumentException();
        if (!folder.isDirectory()) throw new IllegalArgumentException();
        this.folder = folder.getAbsoluteFile();
        this.filesMap = new HashMap<>();
        this.filesList = new ArrayList<>();
        scanFolder();
    }

    private void scanFolder() {
        filesMap.clear();
        filesList.clear();
        try (Stream<Path> stream = Files.list(folder.toPath())) {
            stream.forEach(path -> {
                File file = path.toFile();
                if (isImage(file)) {
                    FileData fileData = new FileData(file);
                    filesMap.put(file.getName(), fileData);
                    filesList.add(fileData);
                }
            });
        } catch (IOException ignored) {}
    }

    private boolean isImage(File file) {
        if (!file.isFile()) return false;
        String fileName = file.getName().toLowerCase();
        return fileName.endsWith(".jpg")
                || fileName.endsWith(".jpeg")
                || fileName.endsWith(".png")
                || fileName.endsWith(".webp")
                || fileName.endsWith(".gif");
    }

    @SuppressWarnings("unused")
    public File getFile(String file) {
        FileData fd = getFileData(file);
        return fd == null ? null : fd.getFile();
    }

    public FileData getFileData(String file) {
        return filesMap.get(file);
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
        if (filesList.size()<=i0 || i1<=0 || i1<=i0)
            return List.of();
        i1 = Math.min(i1, filesList.size());
        i0 = Math.max(0, i0);
        return filesList.subList(i0,i1).stream().map(FileData::getName).toList();
    }

    public int getFileCount() {
        return filesMap.size();
    }
}
