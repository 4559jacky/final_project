package com.mjc.groupware.board.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.mjc.groupware.board.dto.BoardDto;
import com.mjc.groupware.board.entity.Board;
import com.mjc.groupware.board.repository.BoardRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BoardService {

    private final BoardRepository boardRepository;

    // 게시글 작성
    public void createBoard(BoardDto dto) {
        Board board = Board.builder()
                .boardTitle(dto.getBoard_title())
                .boardContent(dto.getBoard_content())
                .views(0)
                .regDate(LocalDateTime.now())
                .modDate(LocalDateTime.now())
                .memberNo(1L) // 추후 로그인 사용자 번호로 대체
                .build();
        boardRepository.save(board);
    }

    // 게시글 목록 조회
    public List<BoardDto> selectBoardList() {
        return boardRepository.findAll().stream()
                .map(board -> BoardDto.builder()
                        .board_no(board.getBoardNo())
                        .board_title(board.getBoardTitle())
                        .board_content(board.getBoardContent())
                        .views(board.getViews())
                        .reg_date(board.getRegDate())
                        .mod_date(board.getModDate())
                        .member_no(board.getMemberNo())
                        .build())
                .collect(Collectors.toList());
    }
}

