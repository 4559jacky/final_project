package com.mjc.groupware.board.service;

import java.io.File;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.mjc.groupware.board.dto.BoardAttachDto;
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
    public BoardAttachDto uploadFile(MultipartFile file) {
        BoardAttachDto dto = new BoardAttachDto();
        try {
            if (file == null || file.isEmpty()) {
                throw new Exception("존재하지 않는 파일입니다.");
            }
            long fileSize = file.getSize();
            if (fileSize >= 1048576) {
                throw new Exception("허용 용량을 초과한 파일입니다.");
            }
            String oriName = file.getOriginalFilename();
            dto.setOri_name(oriName);
            String fileExt = oriName.substring(oriName.lastIndexOf("."),oriName.length());
            UUID uuid = UUID.randomUUID();
            String uniqueName = uuid.toString().replaceAll("-", "");
            String newName = uniqueName+fileExt;
            dto.setNew_name(newName);
            String downDir = fileDir + "board/" + newName;
            dto.setAttach_path(downDir);
            File saveFile = new File(downDir);
         
            if(saveFile.exists() == false) {
            	saveFile.mkdirs();
            }
            file.transferTo(saveFile);
        } catch (Exception e) {
            dto = null;
            e.printStackTrace();
        }
        return dto;
    }
    	// 파일 삭제
    public void deleteFiles(List<Long> fileIds) {
        for (Long fileId : fileIds) {
            attachRepository.deleteById(fileId);
        }
    }
    
}