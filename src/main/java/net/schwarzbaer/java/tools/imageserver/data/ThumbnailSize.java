package net.schwarzbaer.java.tools.imageserver.data;

import org.springframework.lang.Nullable;

public enum ThumbnailSize {
    _100(100),
    _150(150),
    _200(200),
    _250(250),
    _300(300),
    _400(400),
    ;
    public final int size;

    ThumbnailSize(int size) {
        this.size = size;
    }

    @SuppressWarnings("unused")
    public static ThumbnailSize get(String thumbnailSizeStr) {
        if (thumbnailSizeStr!=null)
            for (ThumbnailSize size : values())
                if (thumbnailSizeStr.equals(Integer.toString(size.size)))
                    return size;
        return null;
    }

    public static @Nullable ThumbnailSize get(Integer size) {
        if (size!=null)
            for (ThumbnailSize ts : values())
                if (ts.size == size)
                    return ts;
        return null;
    }

    public String getLabel() {
        return "%d px".formatted(size);
    }

}
