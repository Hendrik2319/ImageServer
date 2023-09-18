package com.example.imageserver.data;

import org.springframework.stereotype.Repository;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class FolderRepository {

    private final List<Folder> folders;

    private FolderRepository() {
        folders = new ArrayList<>();
    }

    public List<Folder> getAllFolders() {
        return folders;
    }

    public void add(File folder) {
        folders.add(new Folder(folder));
    }

    public Folder get(String folder) {
        for (Folder folder_ : folders) {
            if (folder.equals(folder_.getName()))
                return folder_;
        }
        return null;
    }

    @Override
    public String toString() {
        return folders.stream()
                .map(Folder::toString)
                .sorted()
                .collect(Collectors.joining("\r\n"));
    }
}
