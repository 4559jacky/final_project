package com.mjc.groupware.reply.dto;

import java.time.LocalDateTime;

import com.mjc.groupware.board.entity.Board;
import com.mjc.groupware.member.entity.Member;
import com.mjc.groupware.reply.entity.Reply;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class ReplyDto {
	
	private Long reply_no;
	private Long member_no;
	private Long board_no;
	private Long parent_reply_no;
	private String reply_content;
	private String reply_status;
	private LocalDateTime reg_date;
	private LocalDateTime mod_date;
	
	public Reply toEntity() {
		return Reply.builder()
				.replyContent(reply_content)
				.replyNo(reply_no)
				.member(Member.builder().memberNo(member_no).build())
				.board(Board.builder().boardNo(board_no).build())
				.build();
	}
	public ReplyDto toDto(Reply reply) {
		return ReplyDto.builder()
				.reply_content(reply.getReplyContent())
				.reply_no(reply.getReplyNo())
				.reg_date(reply.getRegDate())
				.mod_date(reply.getModDate())
				.build();
	}
	
}
