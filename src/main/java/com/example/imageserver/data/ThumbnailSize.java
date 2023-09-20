package com.example.imageserver.data;

public enum ThumbnailSize {
    _50(50),
    _100(100),
    _150(150),
    _200(200),
    _250(250),
    ;
    public final int size;

    ThumbnailSize(int size) {
        this.size = size;
    }

    public static ThumbnailSize get(String thumbnailSizeStr) {
        if (thumbnailSizeStr!=null)
            for (ThumbnailSize size : values())
                if (thumbnailSizeStr.equals(Integer.toString(size.size)))
                    return size;
        return null;
    }

    public String getLabel() {
        return "%d px".formatted(size);
    }

}
