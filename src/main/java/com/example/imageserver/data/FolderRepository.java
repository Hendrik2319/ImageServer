package com.example.imageserver.data;

import org.springframework.stereotype.Repository;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

@Repository
public class FolderRepository {

    public static final Comparator<String> IGNORING_CASE_COMPARATOR = Comparator
            .<String, String>comparing(String::toLowerCase)
            .thenComparing(Comparator.naturalOrder());

    private final Map<String,Folder> folders;

    private FolderRepository() {
        folders = new HashMap<>();
    }

    long getTotalNumberOfThumbnails() {
        return folders.values().stream()
                .mapToLong(Folder::getTotalNumberOfThumbnails)
                .sum();
    }
    long getTotalSizeOfThumbnails() {
        return folders.values().stream()
                .mapToLong(Folder::getTotalSizeOfThumbnails)
                .sum();
    }

    public List<Folder> getAllFolders() {
        return folders.values().stream().sorted(
                Comparator.comparing(Folder::getKey, IGNORING_CASE_COMPARATOR)
        ).toList();
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
