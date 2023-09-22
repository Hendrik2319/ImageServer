package com.example.imageserver.data;

import org.springframework.lang.Nullable;

public record GeneralInfos (
        long totalSizeOfThumbnails,
        long totalNumberOfThumbnails,
        long maxMem  ,
        long totalMem,
        long freeMem
) {
    public static GeneralInfos create(@Nullable FolderRepository folderRepo) {
        return create(
                folderRepo==null ? 0 : folderRepo.getTotalSizeOfThumbnails(),
                folderRepo==null ? 0 : folderRepo.getTotalNumberOfThumbnails()
        );
    }

    public static GeneralInfos create(@Nullable Folder folder) {
        return create(
                folder==null ? 0 : folder.getTotalSizeOfThumbnails(),
                folder==null ? 0 : folder.getTotalNumberOfThumbnails()
        );
    }

    public static GeneralInfos create(
            long totalSizeOfThumbnails,
            long totalNumberOfThumbnails
    ) {
        Runtime runtime = Runtime.getRuntime();
        long maxMem = runtime.maxMemory();
        long totalMem = runtime.totalMemory();
        long freeMem = runtime.freeMemory();
        return new GeneralInfos(
                totalSizeOfThumbnails,
                totalNumberOfThumbnails,
                totalMem,
                maxMem,
                freeMem
        );
    }

    String getTotalNumberOfThumbnails() { return "%d thumbnails".formatted(totalNumberOfThumbnails); }
    String getTotalSizeOfThumbnails   () { return getSizeStr(totalSizeOfThumbnails); }
    String getMaxMem  () { return getSizeStr(maxMem  ); }
    String getTotalMem() { return getSizeStr(totalMem); }
    String getFreeMem () { return getSizeStr(freeMem ); }

    private static String getSizeStr(long size) {
        float size_d = size;
        if (size_d<1500) return "%d bytes".formatted(size  ); size_d /= 1024;
        if (size_d<1500) return "%f1.2 kB".formatted(size_d); size_d /= 1024;
        if (size_d<1500) return "%f1.2 MB".formatted(size_d); size_d /= 1024;
        if (size_d<1500) return "%f1.2 GB".formatted(size_d); size_d /= 1024;
                         return "%f1.2 TB".formatted(size_d);
    }
}
