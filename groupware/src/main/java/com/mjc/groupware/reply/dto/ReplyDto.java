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
    private String memberName;  // 작성자 이름 추가
    private String deptName; // 부서명 추가
    private Long board_no;
    private Long parent_reply_no; // 대댓글
    private String reply_content;
    private String reply_status;
    private LocalDateTime reg_date;
    private LocalDateTime mod_date;
    
    private String newContent;
    
    private String memberPhoto; // 작성자 프로필 이미지 URL(member 엔티티에도 있어서 가져와서 쓰는중)
    private String regDateFormatted; // 추가
    private List<ReplyDto> subReplies = new ArrayList<>();  // 대댓글 목록

    private Member member; // Member 객체 추가

    // [부서명]성명 형식으로 작성자 정보를 반환하는 메소드 추가
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
        return ReplyDto.builder()
                .reply_no(reply.getReplyNo())
                .member_no(reply.getMember() != null ? reply.getMember().getMemberNo() : null)
                .memberName(reply.getMember() != null ? reply.getMember().getMemberName() : null)
                .deptName(reply.getMember() != null && reply.getMember().getDept() != null ? reply.getMember().getDept().getDeptName() : null) // 부서명 설정
                .board_no(reply.getBoard() != null ? reply.getBoard().getBoardNo() : null)
                .parent_reply_no(reply.getParentReply() != null ? reply.getParentReply().getReplyNo() : null)
                .reply_content(reply.getReplyContent())
                .reply_status(reply.getReplyStatus())
                .reg_date(reply.getRegDate())
                .mod_date(reply.getModDate())
                .regDateFormatted(reply.getRegDate() != null ? formatDate(reply.getRegDate()) : null)
                .member(reply.getMember()) // Member 객체 설정
                .memberPhoto(reply.getMember() != null && reply.getMember().getMemberAttachs() != null && !reply.getMember().getMemberAttachs().isEmpty() ? reply.getMember().getMemberAttachs().get(0).getAttachPath() : null)
                .build();
    }
    // 년월일시간 추가
    private static String formatDate(LocalDateTime dateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        return dateTime.format(formatter);
    }
}