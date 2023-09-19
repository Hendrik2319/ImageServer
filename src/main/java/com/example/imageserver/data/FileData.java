package com.example.imageserver.data;

import lombok.NonNull;
import org.springframework.http.MediaType;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.EnumMap;
import java.util.Objects;

@SuppressWarnings({"FieldCanBeLocal", "FieldMayBeFinal", "unused"})
public class FileData {

    @NonNull public final File file;
    @NonNull public final ImageFormat imageFormat;
    private BufferedImage fullImage;
    private final EnumMap<ThumbnailSize,BufferedImage> thumbnails;

    public FileData(File file) {
        this.file = Objects.requireNonNull(file);
        imageFormat = Objects.requireNonNull(ImageFormat.getImageFormat(file));
        fullImage = null;
        thumbnails = new EnumMap<>(ThumbnailSize.class);
    }

    public String getName() {
        return file.getName();
    }

    public enum ImageFormat {
        JPG(MediaType.IMAGE_JPEG_VALUE, ".jpeg",".jpg"),
        PNG(MediaType.IMAGE_PNG_VALUE,".png"),
        GIF(MediaType.IMAGE_GIF_VALUE,".gif"),
        WEBP("image/webp",".webp"),
        ;
        public final String mediaType;
        public final String[] extensions;
        ImageFormat(String mediaType, String... extensions) {
            this.mediaType = mediaType;
            this.extensions = extensions;
        }

        static ImageFormat getImageFormat(File file) {
            if (!file.isFile()) return null;
            String fileName = file.getName().toLowerCase();
            for (ImageFormat format : values())
                for (String extension : format.extensions)
                    if (fileName.endsWith(extension))
                        return format;
            return null;
        }
    }
}
