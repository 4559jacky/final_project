package com.mjc.groupware.shared.controller;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.mjc.groupware.common.annotation.CheckPermission;
import com.mjc.groupware.member.entity.Member;
import com.mjc.groupware.member.security.MemberDetails;
import com.mjc.groupware.shared.dto.SharedRestoreRequestDto;
import com.mjc.groupware.shared.service.SharedTrashService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/shared")
public class SharedTrashController {
	
    private final SharedTrashService trashService;
    
    @CheckPermission("SHARED_USER")
    @GetMapping("/trash/list")
    public Map<String, Object> getTrashItems(@RequestParam("type") String type, Authentication auth) {
    	Member member = ((MemberDetails) auth.getPrincipal()).getMember();
    	return trashService.loadTrashItems(type, member);
    }
    
    @CheckPermission("SHARED_USER")
    @PostMapping("/restore")
    public String restoreItems(@RequestBody SharedRestoreRequestDto dto) {
        trashService.restoreFolders(dto.getFolderIds());
        trashService.restoreFiles(dto.getFileIds());
        return "복구 완료";
    }
    
    @CheckPermission("SHARED_USER")
    @PostMapping("/delete/permanent")
    public String deleteItems(@RequestBody SharedRestoreRequestDto dto) {
        trashService.deleteFoldersPermanently(dto.getFolderIds());
        trashService.deleteFilesPermanently(dto.getFileIds());
        return "삭제 완료";
    }
}