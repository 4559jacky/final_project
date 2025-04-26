package com.mjc.groupware.reply.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mjc.groupware.reply.entity.Reply;

public interface ReplyRepository extends JpaRepository<Reply, Long> {
    List<Reply> findByBoard_BoardNoAndReplyStatus(Long boardNo, String replyStatus);
}