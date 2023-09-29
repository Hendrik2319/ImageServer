package net.schwarzbaer.java.tools.imageserver.data;

import org.springframework.lang.Nullable;

import java.util.Locale;

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

    public String getTotalNumberOfThumbnails() { return "%d thumbnails".formatted(totalNumberOfThumbnails); }
    public String getTotalSizeOfThumbnails   () { return getSizeStr(totalSizeOfThumbnails); }
    public String getMaxMem  () { return getSizeStr(maxMem  ); }
    public String getTotalMem() { return getSizeStr(totalMem); }
    public String getFreeMem () { return getSizeStr(freeMem ); }

    private static String getSizeStr(long size) {
        float size_d = size;
        if (size_d<1500) return String.format(Locale.ENGLISH,"%d bytes",size  ); size_d /= 1024;
        if (size_d<1500) return String.format(Locale.ENGLISH,"%1.2f kB",size_d); size_d /= 1024;
        if (size_d<1500) return String.format(Locale.ENGLISH,"%1.2f MB",size_d); size_d /= 1024;
        if (size_d<1500) return String.format(Locale.ENGLISH,"%1.2f GB",size_d); size_d /= 1024;
                         return String.format(Locale.ENGLISH,"%1.2f TB",size_d);
    }
}
