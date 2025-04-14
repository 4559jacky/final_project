package com.mjc.groupware.board.dto;

import java.time.LocalDateTime;

import com.mjc.groupware.board.entity.BoardAttach;

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
class BoardAttachDto {
    private Long attachNo;
    private String oriName;
    private String newName;
    private String attachPath;
    private LocalDateTime regDate;
    private LocalDateTime modDate;
}