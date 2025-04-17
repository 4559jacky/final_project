package com.mjc.groupware.board.service;

import java.io.File;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.mjc.groupware.board.entity.Board;
import com.mjc.groupware.board.entity.BoardAttach;
import com.mjc.groupware.board.repository.BoardAttachRepository;
import com.mjc.groupware.board.repository.BoardRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BoardAttachService {

    @Value("${ffupload.location}")
    private String fileDir;

    private final BoardRepository boardRepository;
    private final BoardAttachRepository attachRepository;

    // 파일 단일 조회(O)
    public BoardAttach selectAttachOne(Long id) {
        return attachRepository.findById(id).orElse(null);
    }

    // 게시글에 속한 첨부파일 목록 조회(O)
    public List<BoardAttach> selectAttachList(Long boardNo) {
        Board board = boardRepository.findById(boardNo).orElse(null);
        return board != null ? attachRepository.findByBoard(board) : null;
    }

    // 파일 메타데이터 및 실제 파일 삭제(O)
    public boolean deleteFile(Long attachNo) {
        try {
            BoardAttach boardAttach = attachRepository.findById(attachNo).orElse(null);
            if (boardAttach != null) {
                // 파일 삭제
                File file = new File(boardAttach.getAttachPath());
                if (file.exists()) {
                    boolean deleted = file.delete();  // 파일 삭제
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
    public BoardAttach uploadFile(MultipartFile file, Long boardNo) {
        if (file.isEmpty()) {
            return null;
        }

        String oriName = file.getOriginalFilename();
        String newName = UUID.randomUUID().toString() + "-" + oriName;
        String filePath = fileDir + "/" + newName;

        if (!isValidFileType(oriName)) {
            return null;
        }

        try {
            file.transferTo(new File(filePath));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        BoardAttach boardAttach = BoardAttach.builder()
                .oriName(oriName)
                .newName(newName)
                .attachPath(filePath)
                .regDate(LocalDateTime.now())
                .modDate(LocalDateTime.now())
                .board(Board.builder().boardNo(boardNo).build())
                .build();

        return attachRepository.save(boardAttach);
    }
    
    
    // 파일 형식 검증 (이미지 파일만 허용)
    private boolean isValidFileType(String fileName) {
        String[] validExtensions = { "jpg", "jpeg", "png", "gif", "bmp" };
        String fileExtension = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
        for (String ext : validExtensions) {
            if (fileExtension.equals(ext)) {
                return true;
            }
        }
        return false;
    }

    // 파일 삭제 (여러 개의 파일을 처리)
    public void deleteFiles(List<Long> fileIds) {
        for (Long fileId : fileIds) {
            deleteFile(fileId);
        }
    }
}