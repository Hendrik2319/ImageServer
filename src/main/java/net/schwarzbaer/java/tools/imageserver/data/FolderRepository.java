package net.schwarzbaer.java.tools.imageserver.data;

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

    public List<Folder> getAllFolders(boolean sorted) {
        if (!sorted)
            return List.copyOf(folders.values());
        return folders.values().stream().sorted(
                Comparator.comparing(Folder::getKey, IGNORING_CASE_COMPARATOR)
        ).toList();
    }

    public boolean containsKey(String key) {
        return folders.containsKey(key);
    }

    public Folder add(String key, File folder) {
        Folder newFolder = new Folder(key, folder);
        folders.put(key, newFolder);
        return newFolder;
    }

    public Folder get(String key) {
        return folders.get(key);
    }

    public void clear() {
        folders.clear();
    }

    @Override
    public String toString() {
        return folders.values().stream()
                .map(Folder::toOutputLine)
                .sorted()
                .collect(Collectors.joining("\r\n"));
    }

}
