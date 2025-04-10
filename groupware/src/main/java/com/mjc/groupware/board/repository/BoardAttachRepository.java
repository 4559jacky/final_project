package com.mjc.groupware.board.repository;

import com.mjc.groupware.board.entity.BoardAttach;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

//첨부파일(BoardAttach) 엔티티를 위한 JPA Repository 인터페이스
@Repository
public interface BoardAttachRepository extends JpaRepository<BoardAttach, Long> {

 // 특정 게시글 번호(boardNo)에 해당하는 첨부파일 목록을 조회
 List<BoardAttach> findByBoardNo(Long boardNo);

}