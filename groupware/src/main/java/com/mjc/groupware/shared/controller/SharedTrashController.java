package com.mjc.groupware.shared.controller;

import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mjc.groupware.shared.dto.SharedRestoreRequestDto;
import com.mjc.groupware.shared.service.SharedTrashService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/shared")
public class SharedTrashController {

    private final SharedTrashService trashService;

    @GetMapping("/trash/list")
    public Map<String, Object> getTrashItems() {
        return trashService.loadTrashItems();
    }
    
    @PostMapping("/restore")
    public String restoreItems(@RequestBody SharedRestoreRequestDto dto) {
        trashService.restoreFolders(dto.getFolderIds());
        trashService.restoreFiles(dto.getFileIds());
        return "복구 완료";
    }

    @PostMapping("/delete/permanent")
    public String deleteItems(@RequestBody SharedRestoreRequestDto dto) {
        trashService.deleteFoldersPermanently(dto.getFolderIds());
        trashService.deleteFilesPermanently(dto.getFileIds());
        return "삭제 완료";
    }
}