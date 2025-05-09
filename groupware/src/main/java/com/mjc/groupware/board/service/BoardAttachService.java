package com.mjc.groupware.board.service;

import java.io.File;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.mjc.groupware.board.entity.Board;
import com.mjc.groupware.board.entity.BoardAttach;
import com.mjc.groupware.board.repository.BoardAttachRepository;
import com.mjc.groupware.board.repository.BoardRepository;
import com.mjc.groupware.board.specification.BoardAttachSpecification;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BoardAttachService {

    @Value("${ffupload.location}")
    private String fileDir;

    private final BoardRepository boardRepository;
    private final BoardAttachRepository attachRepository;

    // 파일 단일 조회
    public BoardAttach selectAttachOne(Long id) {
        return attachRepository.findById(id).orElse(null);
    }

    // 게시글에 속한 첨부파일 목록 조회
    public List<BoardAttach> selectAttachList(Long boardNo) {
        Board board = boardRepository.findById(boardNo).orElse(null);
        Specification<BoardAttach> spec = (root, query, criteriaBuilder) -> null;
        spec = spec.and(BoardAttachSpecification.boardEquals(board));
        return attachRepository.findAll(spec);
    }

    // 파일 메타데이터 및 실제 파일 삭제
    public boolean deleteFile(Long attachNo) {
        try {
            BoardAttach boardAttach = attachRepository.findById(attachNo).orElse(null);
            if (boardAttach != null) {
                // 파일 삭제
                File file = new File(boardAttach.getAttachPath());
                if (file.exists()) {
                    boolean deleted = file.delete();
                    if (!deleted) {
                        return false;  // 파일 삭제 실패
                    }
                }

                // 메타데이터 삭제
                attachRepository.delete(boardAttach);
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // 파일 업로드 처리
    public List<BoardAttach> uploadFiles(List<MultipartFile> files, Long boardNo) {
        List<BoardAttach> savedAttachments = new ArrayList<>();
        
        for (MultipartFile file : files) {
            if (file.isEmpty()) {
                continue;
            }

            String oriName = file.getOriginalFilename();
            if (!isValidFileType(oriName)) {
                continue;
            }

            String newName = UUID.randomUUID().toString() + "-" + oriName;
            String filePath = fileDir + "/" + newName;

            try {
                File destinationFile = new File(filePath);
                file.transferTo(destinationFile);
            } catch (Exception e) {
                e.printStackTrace();
                continue;
            }

            BoardAttach boardAttach = BoardAttach.builder()
                .oriName(oriName)
                .newName(newName)
                .attachPath(fileDir + "/" + newName)  // 경로를 상대경로로 변경
                .regDate(LocalDateTime.now())
                .modDate(LocalDateTime.now())
                .board(Board.builder().boardNo(boardNo).build())
                .build();

            savedAttachments.add(attachRepository.save(boardAttach));
        }

        return savedAttachments;
    }

    // 파일 형식 검증 (파일 형식 종류를 15개 이상 더 추가함) - 첨부파일 잘 됨(용량은 5MB)
    private boolean isValidFileType(String fileName) {
        String[] validExtensions = { "jpg", "jpeg", "png", "gif", "webp",
        		"pdf", "doc", "docx", "xls", "xlsx", "ppt", "pptx", "txt", "hwp",
        		"csv", "json", "xml", "sql", "zip", "rar", "7z", "mp3", "wav", "mp4", "mov", "css", "jar" };
        String fileExtension = getFileExtension(fileName);
        
        for (String ext : validExtensions) {
            if (fileExtension.equals(ext)) {
                return true;
            }
        }
        return false;
    }

    // 파일 확장자 추출 (안전하게 처리)
    private String getFileExtension(String fileName) {
        if (fileName != null && fileName.lastIndexOf(".") > 0) {
            return fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
        }
        return "";
    }

    // 파일 삭제 (여러 개의 파일을 처리)
    public void deleteFiles(List<Long> fileIds) {
        for (Long fileId : fileIds) {
            deleteFile(fileId);
        }
    }
}