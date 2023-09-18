package com.example.imageserver.data;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class FolderList {
    public static final FolderList instance = new FolderList();
    private final List<Folder> folderList;

    private FolderList() {
        folderList = new ArrayList<>();
    }

    public List<Folder> getAllFolders() {
        return folderList;
    }

    public void add(File folder) {
        folderList.add(new Folder(folder));
    }

    public Folder get(String folder) {
        for (Folder folder_ : folderList) {
            if (folder.equals(folder_.getName()))
                return folder_;
        }
        return null;
    }

    @Override
    public String toString() {
        return folderList.stream()
                .map(Folder::toString)
                .sorted()
                .collect(Collectors.joining("\r\n"));
    }
}
