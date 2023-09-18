package com.example.imageserver.data;

import org.springframework.stereotype.Repository;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
public class FolderRepository {

    private final Map<String,Folder> folders;

    private FolderRepository() {
        folders = new HashMap<>();
    }

    public Collection<Folder> getAllFolders() {
        return folders.values();
    }

    public boolean containsKey(String key) {
        return folders.containsKey(key);
    }

    public void add(String key, File folder) {
        folders.put(key, new Folder(key, folder));
    }

    public Folder get(String key) {
        return folders.get(key);
    }

    @Override
    public String toString() {
        return folders.values().stream()
                .map(Folder::toOutputLine)
                .sorted()
                .collect(Collectors.joining("\r\n"));
    }
}
