package com.mjc.groupware.reply.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.mjc.groupware.board.entity.Board;
import com.mjc.groupware.member.entity.Member;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "reply")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Reply {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reply_no")
    private Long replyNo;

    @Column(name = "reply_content", columnDefinition = "LONGTEXT", nullable = false)
    private String replyContent;

    @Column(name = "reply_status", nullable = false, columnDefinition = "CHAR(1) DEFAULT 'N'")
    private String replyStatus;  // 'N' = 정상, 'Y' = 삭제됨

    @CreationTimestamp
    @Column(name = "reg_date", updatable = false)
    private LocalDateTime regDate;

    @UpdateTimestamp
    @Column(name = "mod_date", insertable = false)
    private LocalDateTime modDate;

    // 부모 댓글 (자기 참조)
    @ManyToOne
    @JoinColumn(name = "parent_reply_no")
    private Reply parentReply;

    // 작성자
    @ManyToOne
    @JoinColumn(name = "member_no")
    private Member member;

    // 게시글
    @ManyToOne
    @JoinColumn(name = "board_no")
    private Board board;
}