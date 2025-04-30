package com.mjc.groupware.reply.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.mjc.groupware.board.entity.Board;
import com.mjc.groupware.member.entity.Member;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
    
    // 댓글 -> 대댓글 같이 삭제될수있게 하는 코드 생성
    @OneToMany(mappedBy = "parentReply", cascade = CascadeType.ALL)
    private List<Reply> childReplies = new ArrayList<>();
    
}