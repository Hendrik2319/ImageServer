package com.example.imageserver.data;

import lombok.Getter;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.EnumMap;
import java.util.Objects;

public class FileData {

    @Getter
    private final File file;
    private BufferedImage fullImage;
    private final EnumMap<ThumbnailSize,BufferedImage> thumbnails;

    public FileData(File file) {
        this.file = Objects.requireNonNull(file);
        fullImage = null;
        thumbnails = new EnumMap<>(ThumbnailSize.class);
    }

    public String getName() {
        return file.getName();
    }
}
