package com.mjc.groupware.reply.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.mjc.groupware.board.entity.Board;
import com.mjc.groupware.member.entity.Member;
import com.mjc.groupware.reply.entity.Reply;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReplyDto {

    private Long reply_no;
    private Long member_no;
    private String memberName;  // 작성자 이름 추가
    private Long board_no;
    private Long parent_reply_no; // 대댓글
    private String reply_content;
    private String reply_status;
    private LocalDateTime reg_date;
    private LocalDateTime mod_date;
    
    private String newContent;
    
    private String memberPhoto; // 작성자 프로필 이미지 URL(member 엔티티에도 있어서 가져와서 쓰는중)
    
    private String timeAgo; // 댓글 등록(작성자 => 몇시간전 등록)
    
    private List<ReplyDto> subReplies = new ArrayList<>();  // 대댓글 목록
    
    /**
     * DTO -> Entity 변환
     */
    public Reply toEntity() {
        return Reply.builder()
                .replyNo(reply_no)
                .replyContent(reply_content)
                .replyStatus(reply_status != null ? reply_status : "N")
                .member(Member.builder().memberNo(member_no).build())
                .board(Board.builder().boardNo(board_no).build())
                .parentReply(parent_reply_no != null ? Reply.builder().replyNo(parent_reply_no).build() : null)
                .build();
    }
    /**
     * Entity -> DTO 변환
     */
    public static ReplyDto toDto(Reply reply) {
        return ReplyDto.builder()
                .reply_no(reply.getReplyNo())
                .member_no(reply.getMember() != null ? reply.getMember().getMemberNo() : null)
                .memberName(reply.getMember() != null ? reply.getMember().getMemberName() : null)  // 작성자 이름 추가
                .board_no(reply.getBoard() != null ? reply.getBoard().getBoardNo() : null)
                .parent_reply_no(reply.getParentReply() != null ? reply.getParentReply().getReplyNo() : null)
                .reply_content(reply.getReplyContent())
                .reply_status(reply.getReplyStatus())
                .reg_date(reply.getRegDate())
                .mod_date(reply.getModDate())
                .build();
    }

	public ReplyDto(Reply reply) {
	}
}