package com.mjc.groupware.board.controller;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
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

	private final BoardAttachService service; // 게시글 첨부파일 서비스 주입

    @GetMapping("/download/{id}") // 특정 ID를 가진 파일 다운로드 요청을 처리
    public ResponseEntity<Object> fileDownload(@PathVariable("id") Long id) {
        try {
            // 첨부파일 정보를 가져옴
            BoardAttach fileData = service.selectAttachOne(id);
            if (fileData == null) { // 파일이 존재하지 X
                return ResponseEntity.notFound().build(); // 404 Not Found 응답 반환
            }

            // 파일 경로를 가져옴
            Path filePath = Paths.get(fileData.getAttachPath());
            // 파일 리소스를 생성
            Resource resource = new InputStreamResource(Files.newInputStream(filePath));

            // 원본 파일 이름을 가져옴
            String oriFileName = fileData.getOriName();
            // 파일 이름을 URL 인코딩
            String encodedName = URLEncoder.encode(oriFileName, StandardCharsets.UTF_8);

            // HTTP 헤더 설정
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + encodedName); // 다운로드 파일 처리

            // 파일 리소스와 헤더와 함께 200 OK 응답 반환
            return new ResponseEntity<>(resource, headers, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build(); // 400 Bad Request 응답 반환
        }
    }
}