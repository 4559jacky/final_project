package com.mjc.groupware.board.repository;

import com.mjc.groupware.board.entity.Board;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BoardRepository extends JpaRepository<Board, Long> {

 // 게시글 제목에 특정 키워드가 포함된 게시글 리스트 조회
 List<Board> findByBoardTitleContaining(String keyword);

 // 게시글 내용에 특정 키워드가 포함된 게시글 리스트 조회
 List<Board> findByBoardContentContaining(String keyword);

 // '삭제(D)' 상태가 아닌(즉, 활성화된) 게시글의 수를 카운트
 @Query("SELECT COUNT(b) FROM Board b WHERE b.boardStatus <> 'D'")
 int countActiveBoards();
 
 // 소프트 삭제
// @Query("SELECT b FROM Board b WHERE b.deletedYn = 'N'")
// List<Board> findAllNotDeleted();
}
