package com.mjc.groupware.board.repository;

import java.util.List;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;

import com.mjc.groupware.board.entity.Board;
import com.mjc.groupware.board.entity.BoardAttach;

// 첨부파일(BoardAttach) 엔티티를 위한 JPA Repository 인터페이스
public interface BoardAttachRepository extends JpaRepository<BoardAttach, Long> {

	List<BoardAttach> findAll(Specification<BoardAttach> spec);

	List<BoardAttach> findByBoard(Board board);
	
}