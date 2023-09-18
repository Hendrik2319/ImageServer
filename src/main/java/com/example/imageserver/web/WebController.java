package com.example.imageserver.web;

import com.example.imageserver.data.Folder;
import com.example.imageserver.data.FolderRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

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

    @GetMapping("/{folder}")
    public @ResponseBody String getFolder(@PathVariable String folder) {
        Folder folder_ = folderRepository.get(folder);
        if (folder_==null)
            return "Folder: \"%s\" is unknown".formatted(folder);
        return "Folder: \"%s\"%n -> %s".formatted(folder, folder_.getPath());
    }

    @GetMapping("/{folder}/{file}")
    public @ResponseBody String getFile(@PathVariable String folder, @PathVariable String file) {
        Folder folder_ = folderRepository.get(folder);
        if (folder_==null)
            return "Folder: \"%s\" is unknown".formatted(folder);

        File file_ = folder_.getFile(file);
        if (file_==null)
            return "Folder: \"%s\"%n -> %s%nFile: \"%s\" is unknown".formatted(folder, folder_.getPath(), file);

        return "Folder: \"%s\"%n -> %s%nFile: \"%s\"%n -> %s".formatted(folder, folder_.getPath(), file, file_.getPath());
    }

}
