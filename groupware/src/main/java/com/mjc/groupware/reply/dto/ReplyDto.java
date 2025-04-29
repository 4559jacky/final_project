package com.mjc.groupware.reply.dto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
    private String memberName;
    private String deptName;
    private Long board_no;
    private Long parent_reply_no;
    private String reply_content;
    private String reply_status;
    private LocalDateTime reg_date;
    private LocalDateTime mod_date;
    
    private String newContent;
    
    private String memberPhoto;
    private String regDateFormatted;
    private String modDateFormatted;
    private List<ReplyDto> subReplies = new ArrayList<>();
    
    private Member member;
    
    private int subReplyCount;

    public String getFormattedMemberInfo() {
        return String.format("[%s]%s", deptName != null ? deptName : "부서 미정", memberName);
    }

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
        ReplyDto dto = ReplyDto.builder()
                .reply_no(reply.getReplyNo())
                .member_no(reply.getMember() != null ? reply.getMember().getMemberNo() : null)
                .memberName(reply.getMember() != null ? reply.getMember().getMemberName() : null)
                .deptName(reply.getMember() != null && reply.getMember().getDept() != null ? reply.getMember().getDept().getDeptName() : null)
                .board_no(reply.getBoard() != null ? reply.getBoard().getBoardNo() : null)
                .parent_reply_no(reply.getParentReply() != null ? reply.getParentReply().getReplyNo() : null)
                .reply_content(reply.getReplyContent())
                .reply_status(reply.getReplyStatus())
                .reg_date(reply.getRegDate())
                .mod_date(reply.getModDate())
                .regDateFormatted(reply.getRegDate() != null ? formatDate(reply.getRegDate()) : null)
                .modDateFormatted(reply.getModDate() != null ? formatDate(reply.getModDate()) : null)
                .member(reply.getMember())
                .memberPhoto(reply.getMember() != null && reply.getMember().getMemberAttachs() != null && !reply.getMember().getMemberAttachs().isEmpty() ? reply.getMember().getMemberAttachs().get(0).getAttachPath() : null)
                .build();
        
        dto.setSubReplyCount(0);
        
        return dto;
    }

    // 대댓글 수 증가시키는 메소드
    public void incrementSubReplyCount() {
        this.subReplyCount++;
    }

    // 년월일시간 추가
    private static String formatDate(LocalDateTime dateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        return dateTime.format(formatter);
    }

    // 수정된 시간을 실시간으로 반영하는 메소드
    public void updateModDate(LocalDateTime modDate) {
        this.mod_date = modDate;
        this.modDateFormatted = formatDate(modDate);
    }

	public Object isModified() {
		return null;
	}
}