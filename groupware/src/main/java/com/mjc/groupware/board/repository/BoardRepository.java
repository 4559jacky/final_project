package com.mjc.groupware.board.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import com.mjc.groupware.board.entity.Board;
import com.mjc.groupware.member.entity.Member;

public interface BoardRepository extends JpaRepository<Board, Long> {
	Optional<Board> findByBoardNo(Long boardNo);  // board_no 필드 기준
	 // 회원 번호와 게시판 상태(TEMP)로 게시글을 조회하는 쿼리 메소드
    List<Board> findByMemberMemberNoAndBoardStatus(Long memberNo, String boardStatus);
	Page<Board> findAll(Specification<Board> spec, Pageable pageable);
	// 목록 조회시 필터링
	List<Board> findByBoardStatus(String boardStatus);
	
}