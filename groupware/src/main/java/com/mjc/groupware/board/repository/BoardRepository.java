package com.mjc.groupware.board.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mjc.groupware.board.entity.Board;

@Repository
public interface BoardRepository extends JpaRepository<Board, Long> {
	
	// 회원 닉네임 / 이름 보여주기
//	@Query("SELECT b FROM Board b JOIN FETCH b.member ORDER BY b.boardNo DESC")
//	List<Board> findAllWithMember();
}