package com.example.imageserver.web;

import com.example.imageserver.data.Folder;
import com.example.imageserver.data.FolderRepository;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.File;

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
        return getFolderPage_(model, folderKey);
    }

    @PostMapping("/{folderKey}")
    public String getFolderPage(
            Model model, @PathVariable String folderKey,
            @RequestParam @Nullable String page_button,
            @RequestParam @Nullable Integer page_start,
            @RequestParam @Nullable Integer page_size
    ) {
        System.out.printf("[%s] page_button: %s / page_start: %s / page_size: %s%n", folderKey, page_button, page_start, page_size);
        return getFolderPage_(model, folderKey);
    }

    private String getFolderPage_(Model model, String folderKey) {
        Folder folder = folderRepository.get(folderKey);
        model.addAttribute("error", folder==null ? "Folder: \"%s\" is unknown".formatted(folderKey) : null);
        model.addAttribute("folder", folder);
        model.addAttribute("files", folder==null ? null : folder.getFiles(0,10));
        model.addAttribute("pages", new Page[] {
                new Page(0, "0..29"),
                new Page(30, "30..59"),
                new Page(60, "60..72")
        });
        model.addAttribute("pageSizes", new int[] { 10, 20, 30, 50, 70, 100 });
        return "folderView";
    }

    public record Page(int pageStart, String text) {}

    @GetMapping("/{folderKey}/{fileName}")
    public @ResponseBody String getFile(@PathVariable String folderKey, @PathVariable String fileName) {
        Folder folder = folderRepository.get(folderKey);
        if (folder==null)
            return "Folder: \"%s\" is unknown".formatted(folderKey);

        File file = folder.getFile(fileName);
        if (file==null)
            return "Folder: \"%s\"%n -> %s%nFile: \"%s\" is unknown".formatted(folderKey, folder.getPath(), fileName);

        return "Folder: \"%s\"%n -> %s%nFile: \"%s\"%n -> %s".formatted(folderKey, folder.getPath(), fileName, file.getPath());
    }

}
