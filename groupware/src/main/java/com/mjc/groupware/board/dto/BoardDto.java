package com.mjc.groupware.board.dto;


import lombok.*;

import com.mjc.groupware.board.entity.Board;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BoardDto {

    private Long board_no; // 게시판 번호
    private Long member_no; // 작성자 사번
    private String board_title; // 게시글 제목
    private String board_content; // 게시글 내용
    private int views; // 조회수
    private String board_status; // 게시글 삭제 여부
    private LocalDateTime reg_date; // 게시글 등록일
    private LocalDateTime mod_date; // 게시글 수정일

    // 파일 첨부용 필드 (필요 시 분리해도 OK)
    private Long attach_no; // 첨부 번호
    private String ori_name; // 기존 파일명
    private String new_name; // 변경 파일명
    private String attach_path; // 파일 경로

    public static BoardDto fromEntity(Board entity) {
        return BoardDto.builder()
                .board_no(entity.getBoardNo())
                .member_no(entity.getMemberNo())
                .board_title(entity.getBoardTitle())
                .board_content(entity.getBoardContent())
                .views(entity.getViews())
                .board_status(entity.getBoardStatus())
                .reg_date(entity.getRegDate())
                .mod_date(entity.getModDate())
                .build();
    }

    public Board toEntity() {
        return Board.builder()
                .boardNo(board_no)
                .memberNo(member_no)
                .boardTitle(board_title)
                .boardContent(board_content)
                .views(views)
                .boardStatus(board_status != null ? board_status : "N") // 게시글 상태가 null이 아닐 경우 해당 상태를 사용하고, null이면 기본값 "N"을 사용
                .regDate(reg_date != null ? reg_date : LocalDateTime.now()) // 등록일이 null이 아닐 경우 해당 등록일을 사용하고, null이면 현재 시간을 사용
                .modDate(mod_date)
                .build();
    }
}