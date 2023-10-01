package net.schwarzbaer.java.tools.imageserver.data;

import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

public class FileData {

    public static final String THUMBNAIL_IMAGE_FORMAT = "png";
    public static final String THUMBNAIL_IMAGE_MEDIATYPE = MediaType.IMAGE_PNG_VALUE;

    interface ThumbnailCreationListener {
        void thumbnailWasCreated(@SuppressWarnings("unused") int thumbnailSize_px, int numberOfBytes);
    }

    @NonNull public  final File file;
    @NonNull private final Supplier<String> getComment;
    @NonNull public  final ImageFormat imageFormat;
    private final Map<Integer,byte[]> thumbnails;
    private final ThumbnailCreationListener thumbnailCreationListener;

    FileData(@NonNull File file, @NonNull Supplier<String> getComment, @NonNull ThumbnailCreationListener thumbnailCreationListener) {
        this.file = Objects.requireNonNull(file);
        this.getComment = getComment;
        this.thumbnailCreationListener = thumbnailCreationListener;
        imageFormat = Objects.requireNonNull(ImageFormat.getImageFormat(file));
        thumbnails = new HashMap<>();
    }

    public FileOutput createOutput() {
        return new FileOutput( getName(), getComment.get(), imageFormat );
    }

    public String getName() {
        return file.getName();
    }

    public byte[] getThumbnail(int thumbnailSize) {
        byte[] bytes = thumbnails.get(thumbnailSize);
        if (bytes==null) {
            BufferedImage image = null;
            try {
                image = ImageIO.read(file);
            } catch (IOException ex) {
                System.err.printf("IOException while reading image file \"%s\": %s%n", file, ex.getMessage());
            }
            int maxSize = image!=null ? Math.max(image.getWidth(), image.getHeight()) : 0;
            if (maxSize!=0) {
                int newWidth  = (int) ((image.getWidth () * (long)thumbnailSize) / maxSize);
                int newHeight = (int) ((image.getHeight() * (long)thumbnailSize) / maxSize);
                BufferedImage thumbImage = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_ARGB);
                Graphics2D g2 = thumbImage.createGraphics();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.drawImage(image, 0,0, newWidth, newHeight, null);
                try {
                    ByteArrayOutputStream output = new ByteArrayOutputStream();
                    ImageIO.write(thumbImage, THUMBNAIL_IMAGE_FORMAT, output);
                    bytes = output.toByteArray();
                    thumbnails.put(thumbnailSize, bytes);
                    thumbnailCreationListener.thumbnailWasCreated(thumbnailSize, bytes.length);
                } catch (IOException ex) {
                    System.err.printf("IOException while writing image data to byte array: %s%n", ex.getMessage());
                }
            }
        }
        return bytes;
    }


    public enum ImageFormat {
        JPG(MediaType.IMAGE_JPEG_VALUE, ".jpeg",".jpg"),
        PNG(MediaType.IMAGE_PNG_VALUE,".png"),
        GIF(MediaType.IMAGE_GIF_VALUE,".gif"),
        WEBP("image/webp",".webp"),
        ;
        public final String mediaType;
        private final String[] extensions;
        ImageFormat(String mediaType, String... extensions) {
            this.mediaType = mediaType;
            this.extensions = extensions;
        }

        public static ImageFormat getImageFormat(File file) {
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
