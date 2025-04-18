package com.mjc.groupware.notice.controller;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mjc.groupware.notice.entity.Attach;
import com.mjc.groupware.notice.repository.AttachRepository;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class AttachController {

    private final AttachRepository attachRepository;

    @GetMapping("/notice/download")
    public ResponseEntity<Resource> downloadFile(@RequestParam("fileNo") Long fileNo) throws IOException {
        Attach attach = attachRepository.findById(fileNo)
                .orElseThrow(() -> new IllegalArgumentException("파일이 존재하지 않습니다."));

        Path path = Paths.get(attach.getAttachPath());
        if (!Files.exists(path)) {
            throw new IllegalArgumentException("파일을 찾을 수 없습니다: " + attach.getOriName());
        }

        Resource resource = new InputStreamResource(Files.newInputStream(path));
        String contentType = Files.probeContentType(path);
        if (contentType == null) {
            contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE;
        }

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + URLEncoder.encode(attach.getOriName(), "UTF-8") + "\"")
                .contentType(MediaType.parseMediaType(contentType))
                .body(resource);
    }
}