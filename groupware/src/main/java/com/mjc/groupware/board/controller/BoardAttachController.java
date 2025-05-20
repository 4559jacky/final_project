package com.mjc.groupware.board.controller;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.server.ResponseStatusException;

import com.mjc.groupware.board.entity.BoardAttach;
import com.mjc.groupware.board.service.BoardAttachService;
import com.mjc.groupware.common.annotation.CheckPermission;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class BoardAttachController {

    private final BoardAttachService boardAttachService;
    
    @Value("${ffupload.location}")
    private String uploadDir;

    @CheckPermission("BOARD_R")
    @GetMapping("/download/{id}")
    public ResponseEntity<Resource> fileDownload(@PathVariable("id") Long id) throws IOException {
    	
        BoardAttach fileData = boardAttachService.selectAttachOne(id);
        if (fileData == null) {
            return ResponseEntity.notFound().build();
        }

        Path filePath = Paths.get(uploadDir, fileData.getAttachPath()); // <-- 여기 수정

        System.out.println("📂 경로 확인: " + filePath.toAbsolutePath());

        if (!Files.exists(filePath)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        Resource resource = new InputStreamResource(Files.newInputStream(filePath));
        String contentType = Files.probeContentType(filePath);
        if (contentType == null) {
            contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE;
        }

        String encodedFileName = URLEncoder.encode(fileData.getOriName(), StandardCharsets.UTF_8);
        String contentDisposition = "attachment; filename*=UTF-8''" + encodedFileName;

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition)
                .contentType(MediaType.parseMediaType(contentType))
                .body(resource);
    }
}