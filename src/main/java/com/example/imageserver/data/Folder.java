package com.example.imageserver.data;

import lombok.Getter;
import net.schwarzbaer.java.lib.copyimages.ImageComments;

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
    @Getter
    private long totalSizeOfThumbnails;
    @Getter
    private long totalNumberOfThumbnails;
    private ImageComments comments;
    private String whyNoComments;

    Folder(String key, File folder) {
        this.key = key;
        if (folder==null) throw new IllegalArgumentException();
        if (!folder.isDirectory()) throw new IllegalArgumentException();
        this.folder = folder.getAbsoluteFile();
        this.filesMap = new HashMap<>();
        this.filesList = new ArrayList<>();
        comments = null;
        whyNoComments = null;
        scanFolder();
    }

    public String getMetaDataFolderPath() {
        if (comments!=null)
            return comments.getDataFileFolderPath();
        if (whyNoComments!=null)
            return whyNoComments;
        return null;
    }

    public void setCommentsStorage(ImageComments imageComments) {
        this.comments = imageComments;
        whyNoComments = null;
    }
    public void clearCommentsStorage(String reason) {
        this.comments = null;
        whyNoComments = reason;
    }

    void scanFolder() {
        filesMap.clear();
        filesList.clear();
        totalSizeOfThumbnails = 0;
        totalNumberOfThumbnails = 0;
        try (Stream<Path> stream = Files.list(folder.toPath())) {
            stream.forEach(path -> {
                File file = path.toFile();
                if (isImage(file)) {
                    FileData fileData = new FileData(file, (size, bytes)->{
                        totalSizeOfThumbnails += bytes;
                        totalNumberOfThumbnails++;
                    });
                    filesMap.put(file.getName(), fileData);
                    filesList.add(fileData);
                }
            });
        } catch (IOException ignored) {}
    }

    public static boolean isImage(File file) {
        if (!file.isFile()) return false;
        return FileData.ImageFormat.getImageFormat(file)!=null;
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
