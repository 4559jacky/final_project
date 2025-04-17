package com.mjc.groupware.board.service;

import java.io.File;
import java.time.LocalDateTime;
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

    // 파일 단일 조회(O)
    public BoardAttach selectAttachOne(Long id) {
        return attachRepository.findById(id).orElse(null);
    }

    // 게시글에 속한 첨부파일 목록 조회(O)
    public List<BoardAttach> selectAttachList(Long boardNo) {
        Board board = boardRepository.findById(boardNo).orElse(null);
        // Specification 생성(BoardAttach)
        Specification<BoardAttach> spec = (root, query, criteriaBuilder) -> null;
        spec = spec.and(BoardAttachSpecification.boardEquals(board));
        // findAll 메소드에 전달(spec)
        return attachRepository.findAll(spec);
    }

    // 파일 메타데이터 삭제(O)
    public int deleteMetaData(Long attach_no) {
        int result = 0;
        try {
            BoardAttach target = attachRepository.findById(attach_no).orElse(null);
            if (target != null) {
                attachRepository.delete(target);
            }
            result = 1;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    // 실제 파일 삭제(O)
    public int deleteFileData(Long attach_no) {
        int result = 0;
        try {
            BoardAttach boardattach = attachRepository.findById(attach_no).orElse(null);
            if (boardattach != null) {
                File file = new File(boardattach.getAttachPath());
                if (file.exists()) {
                    file.delete();
                }
            }
            result = 1;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    // 파일 업로드 처리
    public BoardAttach uploadFile(MultipartFile file, Long boardNo) {
        if (file.isEmpty()) {
            return null; // 파일이 없으면 null 반환
        }
        
        // 파일 경로 및 이름 생성
        String oriName = file.getOriginalFilename();
        String newName = UUID.randomUUID().toString() + "-" + oriName;
        String filePath = fileDir + "/" + newName;
        
        // 파일 저장
        try {
            file.transferTo(new File(filePath));
        } catch (Exception e) {
            e.printStackTrace();
            return null; // 파일 저장 오류
        }

        // 파일 정보 BoardAttach 객체로 저장
        BoardAttach boardAttach = BoardAttach.builder()
                .oriName(oriName)
                .newName(newName)
                .attachPath(filePath)
                .regDate(LocalDateTime.now())
                .modDate(LocalDateTime.now())
                .board(Board.builder().boardNo(boardNo).build()) // boardNo 설정
                .build();

        // 데이터베이스에 저장
        return attachRepository.save(boardAttach); // 저장된 파일 정보 반환
    }
    
    

    	// 파일 삭제
    public void deleteFiles(List<Long> fileIds) {
        for (Long fileId : fileIds) {
            attachRepository.deleteById(fileId);
        }
    }
    
}