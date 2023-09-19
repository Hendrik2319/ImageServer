package com.example.imageserver.web;

import com.example.imageserver.data.FileData;
import com.example.imageserver.data.Folder;
import com.example.imageserver.data.FolderRepository;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

@Controller
public class WebController {

    private final FolderRepository folderRepository;

    public WebController(FolderRepository folderRepository) {
        this.folderRepository = folderRepository;
    }

    @GetMapping
    public String showMainPageshowAll(Model model) {
        model.addAttribute("folders", folderRepository.getAllFolders());
        return "mainView";
    }

    @GetMapping("/{folderKey}")
    public String getFolderInit(Model model, @PathVariable String folderKey) {
        return getFolderPage_(model, folderKey, null, 0, 20);
    }

    @PostMapping("/{folderKey}")
    public String getFolderPage(
            Model model, @PathVariable String folderKey,
            @RequestParam(name="page_button") @Nullable String pageButton,
            @RequestParam(name="page_start" , defaultValue =  "0") int pageStart,
            @RequestParam(name="page_size"  , defaultValue = "20") int pageSize
    ) {
        //System.out.printf("[%s] page_button: %s / page_start: %s / page_size: %s%n", folderKey, pageButton, pageStart, pageSize);
        return getFolderPage_(model, folderKey, pageButton, pageStart, pageSize);
    }

    private String getFolderPage_(Model model, String folderKey, String pageButton, int pageStart, int pageSize) {
        Folder folder = folderRepository.get(folderKey);

        String error = null;
        List<String> files = null;
        List<Page> pages = null;
        List<PageSize> pageSizes = null;

        if (folder == null) {
            error = "Folder: \"%s\" is unknown".formatted(folderKey);
        } else {
            int n = folder.getFileCount();

            int finalPageSize = pageSize;
            pageSizes = IntStream.of(10, 20, 30, 50, 70, 100)
                    .filter( i -> i<n )
                    .mapToObj( i -> new PageSize(i, i== finalPageSize))
                    .toList();

            int pageEnd;
            if (pageSize<=0)
            {
                pageSize = -1;
                pageStart = 0;
                pageEnd = n;
            }
            else
            {
                if ("up"  .equals(pageButton)) pageStart += pageSize;
                if ("down".equals(pageButton)) pageStart -= pageSize;
                pageStart = Math.min(Math.max(0, pageStart), n-1);
                pageStart = (pageStart/pageSize)*pageSize;
                pageEnd = pageStart + pageSize;

                pages = new ArrayList<>();
                for (int i=0; i<n; i+=pageSize)
                    pages.add(new Page(
                            i,
                            "%d..%d".formatted(i+1, Math.min(i + pageSize, n)),
                            i == pageStart
                    ));
            }

            files = n==0 ? List.of() : folder.getFiles(pageStart, pageEnd);
        }

        model.addAttribute("error", error);
        model.addAttribute("folder", folder);
        model.addAttribute("files", files);
        model.addAttribute("pages", pages);
        model.addAttribute("pageSize", pageSize);
        model.addAttribute("pageSizes", pageSizes);
        return "folderView";
    }

    public record Page(int pageStart, String text, boolean isSelected) {}
    public record PageSize(int pageSize, boolean isSelected) {}

    @GetMapping("/{folderKey}/{fileName}")
    public void getFile(HttpServletResponse response, @PathVariable String folderKey, @PathVariable String fileName) {
        Folder folder = folderRepository.get(folderKey);
        if (folder==null) {
            returnError(response, "Folder: \"%s\" is unknown", folderKey);
            return;
        }

        FileData file = folder.getFileData(fileName);
        if (file==null) {
            returnError(response, "Folder: \"%s\"%n -> %s%nFile: \"%s\" is unknown", folderKey, folder.getPath(), fileName);
            return;
        }

        returnImage(response, file);
    }

    private void returnImage(HttpServletResponse response, @NonNull FileData file) {
        response.setContentType(file.imageFormat.mediaType);
        try {
            Files.copy(file.file.toPath(), response.getOutputStream());
        } catch (IOException ignored) {
        }
    }

    private void returnError(HttpServletResponse response, String format, Object... args) {
        response.setContentType(MediaType.TEXT_PLAIN_VALUE);
        try {
            response.getWriter().printf(format, args);
        } catch (IOException ignored) {
        }
    }

}
