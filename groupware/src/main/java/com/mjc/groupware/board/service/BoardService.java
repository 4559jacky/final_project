package com.mjc.groupware.board.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mjc.groupware.board.dto.BoardDto;
import com.mjc.groupware.board.entity.Board;
import com.mjc.groupware.board.repository.BoardRepository;
import com.mjc.groupware.member.entity.Member;
import com.mjc.groupware.member.repository.MemberRepository;

@Service
public class BoardService {

    private final BoardRepository boardRepository;
    private final MemberRepository memberRepository;

    @Autowired
    public BoardService(BoardRepository boardRepository, MemberRepository memberRepository) {
        this.boardRepository = boardRepository;
        this.memberRepository = memberRepository;
    }

    // 게시글 등록
    @Transactional
    public Long createBoard(BoardDto dto) {
        // 멤버 정보 조회
        Member member = memberRepository.findById(dto.getMember_no())
                .orElseThrow(() -> new RuntimeException("회원 정보를 찾을 수 없습니다."));

        Board board = Board.builder()
                .boardTitle(dto.getBoard_title())
                .boardContent(dto.getBoard_content())
                .views(0)
                .boardStatus("N")
                .isDeleted(false)
                .regDate(LocalDateTime.now())
                .member(member)  // 조회된 Member를 Board에 설정
                .build();

        boardRepository.save(board);
        return board.getBoardNo();
    }

    // 게시글 조회
    public BoardDto getBoard(Long boardNo) {
        Board board = boardRepository.findById(boardNo)
                .orElseThrow(() -> new RuntimeException("해당 게시글을 찾을 수 없습니다."));

        return BoardDto.builder()
                .board_no(board.getBoardNo())
                .board_title(board.getBoardTitle())
                .board_content(board.getBoardContent())
                .views(board.getViews())
                .board_status(board.getBoardStatus())
                .is_deleted(board.getIsDeleted())
                .reg_date(board.getRegDate())
                .mod_date(board.getModDate())
                .member_no(board.getMember().getMemberNo())
                .build();
    }

    // 게시글 수정
    @Transactional
    public void updateBoard(Long boardNo, BoardDto boardDto) {
        Board board = boardRepository.findById(boardNo)
                .orElseThrow(() -> new RuntimeException("해당 게시글을 찾을 수 없습니다."));

        board.setBoardTitle(boardDto.getBoard_title());
        board.setBoardContent(boardDto.getBoard_content());
        board.setModDate(LocalDateTime.now());
    }

    // 게시글 삭제 (Soft Delete)
    @Transactional
    public void deleteBoard(Long boardNo) {
        Board board = boardRepository.findById(boardNo)
                .orElseThrow(() -> new RuntimeException("해당 게시글을 찾을 수 없습니다."));

        board.setIsDeleted(true);
        board.setBoardStatus("Y");
        board.setModDate(LocalDateTime.now());
    }

 // 회원별 임시 저장 게시글 조회
    public List<BoardDto> getTempBoardsByMember(Long memberNo) {
        return boardRepository.findByMemberMemberNoAndBoardStatus(memberNo, "TEMP").stream()
                .filter(board -> !board.getIsDeleted()) // 소프트 삭제된 게시글 제외
                .map(board -> BoardDto.builder()
                        .board_no(board.getBoardNo())
                        .board_title(board.getBoardTitle())
                        .board_content(board.getBoardContent())
                        .views(board.getViews())
                        .board_status(board.getBoardStatus())
                        .is_deleted(board.getIsDeleted())
                        .reg_date(board.getRegDate())
                        .mod_date(board.getModDate())
                        .member_no(board.getMember().getMemberNo())
                        .build())
                .toList();
    }

	// 게시판 전체 조회 (Soft Delete 제외)
	public List<BoardDto> getAllBoards() {
	    return boardRepository.findAll().stream()
	            .filter(board -> !board.getIsDeleted()) // Soft Delete된 항목 제외
	            .map(board -> BoardDto.builder()
	                    .board_no(board.getBoardNo())
	                    .board_title(board.getBoardTitle())
	                    .board_content(board.getBoardContent())
	                    .views(board.getViews())
	                    .board_status(board.getBoardStatus())
	                    .is_deleted(board.getIsDeleted())
	                    .reg_date(board.getRegDate())
	                    .mod_date(board.getModDate())
	                    .member_no(board.getMember().getMemberNo())
	                    .build())
	            .toList();
	}

	// 임시 저장 게시글 삭제 (Soft Delete)
	@Transactional
	public void deleteTempBoard(Long boardNo) {
	    Board board = boardRepository.findById(boardNo)
	            .orElseThrow(() -> new RuntimeException("해당 임시 게시글을 찾을 수 없습니다."));

	    board.setIsDeleted(true);
	    board.setBoardStatus("Y");
	    board.setModDate(LocalDateTime.now());
	}

}