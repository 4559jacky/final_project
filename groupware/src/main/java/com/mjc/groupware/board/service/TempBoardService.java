package com.mjc.groupware.board.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.mjc.groupware.board.dto.TempBoardDto;
import com.mjc.groupware.board.entity.TempBoard;
import com.mjc.groupware.board.repository.TempBoardRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TempBoardService {

    private final TempBoardRepository tempBoardRepository;

    // 임시 저장 게시글 등록
    public Long saveTempBoard(TempBoardDto dto) {
        TempBoard tempBoard = TempBoard.builder()
                .boardTitle(dto.getBoardTitle())
                .boardSaveContent(dto.getBoardSaveContent())
                .memberNo(dto.getMemberNo())
                .regDate(LocalDateTime.now())
                .modDate(LocalDateTime.now())
                .build();
        return tempBoardRepository.save(tempBoard).getBoardNo();
    }

    // 사용자별 임시 저장 게시글 목록 조회
    public List<TempBoardDto> getTempBoardsByMember(Long memberNo) {
        return tempBoardRepository.findByMemberNo(memberNo).stream()
                .map(TempBoardDto::fromEntity)
                .collect(Collectors.toList());
    }

    // 특정 임시 저장 게시글 조회
    public TempBoardDto getTempBoardById(Long boardNo) {
        TempBoard tempBoard = tempBoardRepository.findById(boardNo)
                .orElseThrow(() -> new IllegalArgumentException("임시 저장 게시글을 찾을 수 없습니다."));
        return TempBoardDto.fromEntity(tempBoard);
    }

    // 임시 저장 게시글 수정
    public void updateTempBoard(Long boardNo, TempBoardDto dto) {
        TempBoard tempBoard = tempBoardRepository.findById(boardNo)
                .orElseThrow(() -> new IllegalArgumentException("임시 저장 게시글을 찾을 수 없습니다."));

        tempBoard.setBoardTitle(dto.getBoardTitle());
        tempBoard.setBoardSaveContent(dto.getBoardSaveContent());
        tempBoard.setModDate(LocalDateTime.now());

        tempBoardRepository.save(tempBoard);
    }

    // 임시 저장 게시글 삭제
    public void deleteTempBoard(Long boardNo) {
        tempBoardRepository.deleteById(boardNo);
    }
}