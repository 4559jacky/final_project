package com.mjc.groupware.board.service;

import java.io.File;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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

    // 게시글에 속한 첨부파일 목록 조회 (status = 'N'인 파일만)
    public List<BoardAttach> selectAttachList(Long boardNo) {
        Board board = boardRepository.findById(boardNo).orElse(null);
        if (board == null) return List.of();

        Specification<BoardAttach> statusSpec = (root, query, cb) -> cb.equal(root.get("boardAttachStatus"), "N");
        Specification<BoardAttach> spec = Specification.where(statusSpec)
            .and(BoardAttachSpecification.boardEquals(board));

        return attachRepository.findAll(spec);
    }

    // 단일 파일 soft delete (물리 삭제 + 상태 'Y')
    @Transactional
    public boolean deleteFile(Long attachNo) {
        try {
            BoardAttach boardAttach = attachRepository.findById(attachNo).orElse(null);
            if (boardAttach != null && "N".equals(boardAttach.getBoardAttachStatus())) {
                File file = new File(fileDir + boardAttach.getAttachPath());
                if (file.exists()) {
                    file.delete(); // 실제 파일 삭제
                }

                boardAttach.setBoardAttachStatus("Y"); // DB에 남기고 상태만 변경
                boardAttach.setModDate(LocalDateTime.now());
                attachRepository.save(boardAttach);

                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // 여러 파일 soft delete 처리
    @Transactional
    public void deleteFiles(List<Long> fileIds) {
        for (Long fileId : fileIds) {
            deleteFile(fileId); // 내부적으로 soft delete
        }
    }

    // 파일 업로드 처리
    public List<BoardAttach> uploadFiles(List<MultipartFile> files, Long boardNo) {
        List<BoardAttach> savedAttachments = new ArrayList<>();

        String boardDir = fileDir + "/board";
        File uploadDir = new File(boardDir);
        if (!uploadDir.exists()) uploadDir.mkdirs();

        for (MultipartFile file : files) {
            if (file.isEmpty()) continue;

            String oriName = file.getOriginalFilename();
            if (!isValidFileType(oriName)) continue;

            String newName = UUID.randomUUID().toString() + "-" + oriName;
            String relativePath = "/board/" + newName;
            String fullPath = fileDir + relativePath;

            try {
                file.transferTo(new File(fullPath));
            } catch (Exception e) {
                e.printStackTrace();
                continue;
            }

            BoardAttach boardAttach = BoardAttach.builder()
                .oriName(oriName)
                .newName(newName)
                .attachPath(relativePath)
                .regDate(LocalDateTime.now())
                .modDate(LocalDateTime.now())
                .board(Board.builder().boardNo(boardNo).build())
                .boardAttachStatus("N")
                .build();

            savedAttachments.add(attachRepository.save(boardAttach));
        }

        return savedAttachments;
    }

    // 파일 형식 검증
    private boolean isValidFileType(String fileName) {
        String[] validExtensions = {
            "jpg", "jpeg", "png", "gif", "webp",
            "pdf", "doc", "docx", "xls", "xlsx", "ppt", "pptx", "txt", "hwp",
            "csv", "json", "xml", "sql", "zip", "rar", "7z", "mp3", "wav", "mp4", "mov", "css", "jar"
        };
        String fileExtension = getFileExtension(fileName);
        for (String ext : validExtensions) {
            if (fileExtension.equals(ext)) {
                return true;
            }
        }
        return false;
    }

    // 파일 확장자 추출
    private String getFileExtension(String fileName) {
        if (fileName != null && fileName.lastIndexOf(".") > 0) {
            return fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
        }
        return "";
    }
}