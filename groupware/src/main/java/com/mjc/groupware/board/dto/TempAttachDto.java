package com.mjc.groupware.board.dto;

import java.time.LocalDateTime;

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
class TempAttachDto {
    private Long attachNo;
    private String oriName;
    private String newName;
    private String attachPath;
    private LocalDateTime regDate;
    private LocalDateTime modDate;
}