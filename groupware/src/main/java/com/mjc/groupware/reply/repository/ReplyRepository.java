package com.mjc.groupware.reply.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


import com.mjc.groupware.reply.entity.Reply;

public interface ReplyRepository extends JpaRepository<Reply, Long> {
    
    // 특정 게시글의 댓글/대댓글 조회 (ReplyStatus 기준)
    List<Reply> findByBoard_BoardNoAndReplyStatus(Long boardNo, String replyStatus);  

    // fetch join으로 회원 정보와 함께 댓글 조회
    @Query("SELECT r FROM Reply r JOIN FETCH r.member WHERE r.board.boardNo = :boardNo AND r.replyStatus = :replyStatus")
    List<Reply> findByBoardNoWithMember(@Param("boardNo") Long boardNo, @Param("replyStatus") String replyStatus);
    
    // 특정 댓글의 대댓글 조회
    List<Reply> findByParentReply_ReplyNoAndReplyStatus(Long parentReplyNo, String replyStatus);
    
    // 댓글을 부모 댓글 기준으로 계층 조회
    @Query("SELECT r FROM Reply r WHERE r.board.boardNo = :boardNo AND r.replyStatus = :replyStatus ORDER BY r.regDate ASC")
    List<Reply> findByBoardNoAndReplyStatusOrderByRegDate(@Param("boardNo") Long boardNo, @Param("replyStatus") String replyStatus);

    // 댓글 +더보기 버튼 추가 코드
//	Page<Reply> findByBoard_BoardNoAndParentReplyIsNullAndReplyStatus(Long boardNo, String string, Pageable pageable);

}
