package com.example.imageserver.web;

import org.springframework.lang.NonNull;

public enum ViewType {
    Table("Table", "table"),
    Thumbnails("Thumbnails", "thumbnails");

    public final String label;
    public final String value;

    ViewType(String label, String value) {
        this.label = label;
        this.value = value;
    }

    public static @NonNull ViewType parse(String viewTypeStr, @NonNull ViewType defaultValue) {
        if (viewTypeStr==null)
            return defaultValue;

        viewTypeStr = viewTypeStr.toLowerCase();
        for (ViewType value : values())
            if (value.value.equals(viewTypeStr))
                return value;

        return defaultValue;
    }
}
