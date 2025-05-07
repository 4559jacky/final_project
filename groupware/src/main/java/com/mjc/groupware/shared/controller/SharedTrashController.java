package com.mjc.groupware.shared.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mjc.groupware.shared.dto.SharedFileDto;
import com.mjc.groupware.shared.dto.SharedFolderDto;
import com.mjc.groupware.shared.service.SharedFileService;
import com.mjc.groupware.shared.service.SharedFolderService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/shared/trash")
@RequiredArgsConstructor
public class SharedTrashController {

    private final SharedFolderService sharedFolderService;
    private final SharedFileService sharedFileService;

    @GetMapping
    public Map<String, Object> getTrashList() {
        List<SharedFolderDto> deletedFolders = sharedFolderService.getDeletedFolders();
        List<SharedFileDto> deletedFiles = sharedFileService.getDeletedFiles();

        Map<String, Object> result = new HashMap<>();
        result.put("folders", deletedFolders);
        result.put("files", deletedFiles);

        return result;
    }
}