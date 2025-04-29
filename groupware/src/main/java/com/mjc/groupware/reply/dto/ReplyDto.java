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

    // 댓글의 고유 번호
    private Long reply_no;
    // 댓글 작성자의 회원 번호
    private Long member_no;
    // 댓글 작성자의 이름
    private String memberName;
    // 댓글 작성자의 부서 이름
    private String deptName;
    // 댓글이 달린 게시글의 고유 번호
    private Long board_no;
    // 부모 댓글의 번호 (대댓글을 위해 사용)
    private Long parent_reply_no;
    // 댓글 내용
    private String reply_content;
    // 댓글 상태 (Y: 삭제, N: 정상)
    private String reply_status;
    // 댓글 등록일
    private LocalDateTime reg_date;
    // 댓글 수정일 (수정이 된 경우만 존재)
    private LocalDateTime mod_date;
    
    // 새로운 내용 (수정할 때 사용)
    private String newContent;
    // 작성자의 사진 (첨부파일이 있을 경우)
    private String memberPhoto;
    // 등록일을 포맷팅한 문자열 (수정된 날짜도 이 방식으로 처리)
    private String regDateFormatted;
    // 대댓글 목록
    private List<ReplyDto> subReplies = new ArrayList<>();
    
    // 댓글 작성자 객체 (회원 정보)
    private Member member;
    // 대댓글 수
    private int subReplyCount;

    /**
     * 댓글 작성자의 부서 이름과 이름을 포맷해서 반환
     * 예: "[부서명]작성자명"
     */
    public String getFormattedMemberInfo() {
        return String.format("[%s]%s", deptName != null ? deptName : "부서 미정", memberName);
    }

    /**
     * 댓글이 수정되었는지 확인하여 "(수정됨)"을 반환
     * 수정되었으면 "(수정됨)", 아니면 빈 문자열 반환
     */
    public String getModifiedStatus() {
        return isModified() ? "(수정됨)" : "";
    }

    /**
     * 댓글 등록일을 포맷하여 반환 (yyyy-MM-dd HH:mm 형식)
     */
    public String getRegDateFormatted() {
        return reg_date != null ? formatDate(reg_date) : "";
    }

    /**
     * DTO -> Entity 변환
     * ReplyDto 객체를 Reply 엔티티로 변환하여 반환
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
     * Reply 엔티티 객체를 ReplyDto 객체로 변환하여 반환
     */
    public static ReplyDto toDto(Reply reply) {
        // Reply 엔티티를 기반으로 ReplyDto 객체를 생성
        ReplyDto dto = ReplyDto.builder()
                .reply_no(reply.getReplyNo())
                .member_no(reply.getMember() != null ? reply.getMember().getMemberNo() : null)
                .memberName(reply.getMember() != null ? reply.getMember().getMemberName() : null)
                .deptName(reply.getMember() != null && reply.getMember().getDept() != null ? reply.getMember().getDept().getDeptName() : null)
                .board_no(reply.getBoard() != null ? reply.getBoard().getBoardNo() : null)
                .parent_reply_no(reply.getParentReply() != null ? reply.getParentReply().getReplyNo() : null)
                .reply_content(reply.getReplyContent())
                .reply_status(reply.getReplyStatus())
                .reg_date(reply.getRegDate()) // 등록일 그대로 복사
                .mod_date(reply.getModDate()) // 수정일 그대로 복사
                .regDateFormatted(reply.getRegDate() != null ? formatDate(reply.getRegDate()) : null)
                .member(reply.getMember())
                .memberPhoto(reply.getMember() != null && reply.getMember().getMemberAttachs() != null && !reply.getMember().getMemberAttachs().isEmpty() ? reply.getMember().getMemberAttachs().get(0).getAttachPath() : null)
                .build();
        
        dto.setSubReplyCount(0); // 초기 대댓글 수는 0
        return dto;
    }

    // 대댓글 수를 증가시키는 코드
    public void incrementSubReplyCount() {
        this.subReplyCount++;
    }

    
     // LocalDateTime을 "yyyy-MM-dd HH:mm" 형식의 문자열로 변환하는 메소드
     
    private static String formatDate(LocalDateTime dateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        return dateTime.format(formatter);
    }

    
     // 댓글이 수정되었는지 여부를 반환
     // 수정일이 존재하면 수정된 것으로 간주
     
    public boolean isModified() {
        return mod_date != null;
    }

    
     // 수정된 날짜를 반환하는 메소드 (중복된 코드 처리용)
     
    public Object getModDateFormatted() { 
        return null; // 중복 코드 처리용
    }
}
