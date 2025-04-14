package com.mjc.groupware.board.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mjc.groupware.board.entity.BoardAttach;

// 첨부파일(BoardAttach) 엔티티를 위한 JPA Repository 인터페이스
public interface BoardAttachRepository extends JpaRepository<BoardAttach, Long> {
	
}