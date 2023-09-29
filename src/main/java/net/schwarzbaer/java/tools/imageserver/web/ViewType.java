package net.schwarzbaer.java.tools.imageserver.web;

import org.springframework.lang.Nullable;

public enum ViewType {
    Table("Table", "table"),
    Thumbnails("Thumbnails", "thumbnails");

    public final String label;
    public final String value;

    ViewType(String label, String value) {
        this.label = label;
        this.value = value;
    }

    public static @Nullable ViewType parse(String viewTypeStr) {
        if (viewTypeStr==null)
            return null;

        viewTypeStr = viewTypeStr.toLowerCase();
        for (ViewType value : values())
            if (value.value.equals(viewTypeStr))
                return value;

        return null;
    }
}
