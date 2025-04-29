package com.mjc.groupware.reply.dto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

import com.mjc.groupware.board.entity.Board;
import com.mjc.groupware.member.entity.Member;
import com.mjc.groupware.member.entity.MemberAttach;
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
    // 부모 댓글 번호 (대댓글일 경우)
    private Long parent_reply_no;
    private String reply_content;
    // 댓글 상태 (ex. 정상 N, 삭제 D 등)
    private String reply_status;
    private LocalDateTime reg_date;
    private LocalDateTime mod_date;
    // 새로 수정될 댓글 내용 (수정 시 사용)
    private String newContent;
    // 프로필 이미지 경로
    private String attachPath;
    // 등록일 포맷된 문자열 (ex. 2024-04-29 14:30)
    private String regDateFormatted;
    // 대댓글 리스트
    private List<ReplyDto> subReplies = new ArrayList<>();
    private Member member;
    // 대댓글 수
    private int subReplyCount;
    // 프로필 이미지 경로 캐시 (회원 번호 기반)
    private static final Map<Long, String> profileImageCache = new ConcurrentHashMap<>();

    // 부서명과 이름을 포맷해서 반환 (ex. [총무팀]홍길동)
    public String getFormattedMemberInfo() {
        return String.format("[%s]%s", deptName != null ? deptName : "부서 미정", memberName);
    }

    // 댓글이 수정되었는지 여부 표시 문자열
    public String getModifiedStatus() {
        return isModified() ? "(수정됨)" : "";
    }

    // 포맷된 등록일 반환
    public String getRegDateFormatted() {
        return reg_date != null ? formatDate(reg_date) : "";
    }

    // DTO → Entity 변환 메서드
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

    // Entity → DTO 변환 메서드
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
                .member(reply.getMember())
                .subReplyCount(reply.getChildReplies().size())
                .attachPath(getCachedProfileImagePath(reply))
                .build();
        return dto;
    }

    // 캐시된 프로필 이미지 경로를 가져오거나 새로 생성
    private static String getCachedProfileImagePath(Reply reply) {
        if (reply == null || reply.getMember() == null) {
            return "/img/default_profile.png";
        }
        Long memberNo = reply.getMember().getMemberNo();
        if (profileImageCache.containsKey(memberNo)) {
            String cachedPath = profileImageCache.get(memberNo);
            return cachedPath;
        }
        String attachPath = getProfileImagePath(reply);
        profileImageCache.put(memberNo, attachPath);
        return attachPath;
    }

    // 실제 프로필 이미지 경로를 추출
    private static String getProfileImagePath(Reply reply) {
        try {
            if (reply.getMember() == null || reply.getMember().getMemberAttachs() == null || reply.getMember().getMemberAttachs().isEmpty()) {
                return "/img/default_profile.png";
            }
            List<MemberAttach> attaches = reply.getMember().getMemberAttachs();
            attaches.sort((a, b) -> b.getRegDate().compareTo(a.getRegDate())); // 최신 등록 순 정렬
            String attachPath = attaches.get(0).getAttachPath();
            if (attachPath == null || attachPath.isEmpty()) {
                return "/img/default_profile.png";
            }
            // 윈도우 경로를 웹 접근 경로로 변환
            if (attachPath.startsWith("C:/upload/groupware/")) {
                attachPath = attachPath.replace("C:/upload/groupware/", "/uploads/");
            }
            return attachPath;
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return "/img/default_profile.png";
        }
    }

    // 대댓글 수 증가 메서드
    public void incrementSubReplyCount() {
        this.subReplyCount++;
    }

    // 날짜 포맷 공통 처리
    private static String formatDate(LocalDateTime dateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        return dateTime.format(formatter);
    }

    // 댓글이 수정된 경우 true 반환
    public boolean isModified() {
        return mod_date != null;
    }

    // 포맷된 수정일 반환
    public String getModDateFormatted() {
        return mod_date != null ? formatDate(mod_date) : "";
    }
}