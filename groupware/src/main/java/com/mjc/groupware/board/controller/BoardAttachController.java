package com.mjc.groupware.board.controller;

import java.nio.file.Paths;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.mjc.groupware.board.entity.BoardAttach;
import com.mjc.groupware.board.service.BoardAttachService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class BoardAttachController {

    private final BoardAttachService boardAttachService;

    @GetMapping("/download/{id}")
    public ResponseEntity<Object> fileDownload(@PathVariable("id") Long id) {
        try {
            BoardAttach fileData = boardAttachService.selectAttachOne(id);
            if (fileData == null) {
                return ResponseEntity.notFound().build();
            }

            Path filePath = Paths.get(fileData.getAttachPath());
            InputStreamResource resource = new InputStreamResource(Files.newInputStream(filePath));
            String encodedName = URLEncoder.encode(fileData.getOriName(), StandardCharsets.UTF_8);

            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + encodedName);

            return new ResponseEntity<>(resource, headers, HttpStatus.OK);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }
}