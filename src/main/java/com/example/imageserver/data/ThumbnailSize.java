package com.example.imageserver.data;

public enum ThumbnailSize {
    _50(50),
    _100(100),
    _150(150),
    _200(200),
    _250(250),
    ;
    private final int size;

    ThumbnailSize(int size) {
        this.size = size;
    }

    public String getLabel() {
        return "%d px".formatted(size);
    }

}
