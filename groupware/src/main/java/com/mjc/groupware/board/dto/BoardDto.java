package com.mjc.groupware.board.dto;

import java.time.LocalDateTime;

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
public class BoardDto {
    private Long board_no;        // 게시판 번호
    private String board_title;   // 게시판 제목
    private String board_content; // 게시판 내용
    private int views;            // 조회수
    private LocalDateTime reg_date; // 게시판 등록일
    private LocalDateTime mod_date; // 게시판 수정일
    private Long member_no;       // 사원 번호
}
