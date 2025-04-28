package com.mjc.groupware.reply.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.mjc.groupware.reply.entity.Reply;

public interface ReplyRepository extends JpaRepository<Reply, Long> {
    
    // 기존 메서드
    List<Reply> findByBoard_BoardNoAndReplyStatus(Long boardNo, String replyStatus);  

    // 새로 추가하는 fetch join 메서드
    @Query("SELECT r FROM Reply r JOIN FETCH r.member WHERE r.board.boardNo = :boardNo AND r.replyStatus = :replyStatus")
    List<Reply> findByBoardNoWithMember(@Param("boardNo") Long boardNo, @Param("replyStatus") String replyStatus);
}