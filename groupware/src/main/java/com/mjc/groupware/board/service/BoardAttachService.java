package com.mjc.groupware.board.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.mjc.groupware.board.dto.BoardAttachDto;
import com.mjc.groupware.board.entity.BoardAttach;
import com.mjc.groupware.board.repository.BoardAttachRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor 
public class BoardAttachService {

	private final BoardAttachRepository attachRepository; // 첨부파일 리포지토리

    @Value("${ffupload.location}")
    private String uploadDir; // 파일 업로드 디렉토리 경로

    // 첨부파일 저장 메소드
    @Transactional // 트랜잭션 처리를 위한 어노테이션
    public void saveAttachFiles(Long board_no, MultipartFile[] files) {
        if (files == null || files.length == 0) return; // 파일이 없으면 메소드 종료

        // 각 파일에 대해 처리
        for (MultipartFile file : files) {
            if (!file.isEmpty()) { // 파일이 비어있지 않은 경우
                try {
                    String oriName = file.getOriginalFilename(); // 원본 파일 이름 가져오기
                    String newName = UUID.randomUUID().toString() + "_" + oriName; // 새로운 파일 이름 생성 (UUID 사용)

                    Path uploadPath = Paths.get(uploadDir); // 업로드 경로 객체 생성
                    if (!Files.exists(uploadPath)) {
                        Files.createDirectories(uploadPath); // 디렉토리 생성
                    }

                    Path filePath = uploadPath.resolve(newName); 
                    Files.copy(file.getInputStream(), filePath); // 파일 복사

                    // 파일 엔티티 생성 및 등록일/수정일 설정
                    BoardAttach attach = BoardAttach.builder()
                            .oriName(oriName) 
                            .newName(newName)
                            .attachPath(filePath.toString())
                            .boardNo(board_no)
                            .regDate(LocalDateTime.now())
                            .modDate(LocalDateTime.now())
                            .build();

                    attachRepository.save(attach); // 파일 엔티티 저장

                } catch (IOException e) { 
                    System.err.println("[파일 저장 실패] " + e.getMessage()); // 에러 메시지 출력
                    e.printStackTrace();
                }
            }
        }
    }

    // 첨부파일 조회 메소드
    public BoardAttach selectAttachOne(Long id) {
        return attachRepository.findById(id).orElse(null); // 첨부파일 조회
    }
    // 게시글 번호로 첨부 파일 목록 조회
    public List<BoardAttachDto> selectAttachList(Long boardNo) {
        return attachRepository.findByBoardNo(boardNo).stream()
            .map(BoardAttachDto::fromEntity)
            .collect(Collectors.toList());
    }
}