package com.mjc.groupware.board.dto;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BoardDto {
    private Long board_no;  // 스네이크 케이스로 변경
    private Long member_no;  // 스네이크 케이스로 변경
    private String board_title;  // 스네이크 케이스로 변경
    private String board_content;  // 스네이크 케이스로 변경
    private int views;
    private String board_status;  // 스네이크 케이스로 변경
    private Boolean is_deleted;  // 스네이크 케이스로 변경
    private LocalDateTime reg_date;  // 스네이크 케이스로 변경
    private LocalDateTime mod_date;  // 스네이크 케이스로 변경
    private List<BoardAttachDto> attachments;
}