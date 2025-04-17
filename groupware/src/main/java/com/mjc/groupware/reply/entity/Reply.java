package com.mjc.groupware.reply.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.mjc.groupware.board.entity.Board;
import com.mjc.groupware.member.entity.Member;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@Table(name = "reply")
public class Reply {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "reply_no")
	private Long replyNo;
	
	@Column(name = "reply_content", columnDefinition = "LONGTEXT", nullable = false)
	private String replyContent;
	
	@Column(name = "reply_status", nullable = false)
	private String replyStatus = "Y"; // 댓글 삭제 여부
	
	@CreationTimestamp
	@Column(updatable = false, name = "reg_date")
	private LocalDateTime regDate;
	
	@UpdateTimestamp
	@Column(insertable = false, name = "mod_date")
	private LocalDateTime modDate;
	
    @ManyToOne
    @JoinColumn(name = "parent_reply_no") // 상위 댓글
    private Reply reply;
	
    @ManyToOne
    @JoinColumn(name = "member_no")
    private Member member;
    
    @ManyToOne
    @JoinColumn(name = "board_no")
    private Board board;
	
	
	
}
