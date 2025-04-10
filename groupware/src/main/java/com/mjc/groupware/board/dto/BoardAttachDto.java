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
public class BoardAttachDto {

    private Long attach_no; // 파일 번호
    private String ori_name; // 기존 파일명
    private String new_name; // 변경 파일명
    private String attach_path; // 파일 경로
    private LocalDateTime reg_date; // 등록일 추가
    private LocalDateTime mod_date; // 수정일 추가

    public static BoardAttachDto fromEntity(BoardAttach attach) {
        return BoardAttachDto.builder()
                .attach_no(attach.getAttachNo()) // 파일 번호
                .ori_name(attach.getOriName()) // 기존 파일명
                .new_name(attach.getNewName()) // 변경 파일명
                .attach_path(attach.getAttachPath()) // 파일 경로
                .reg_date(attach.getRegDate()) // 등록일 추가
                .mod_date(attach.getModDate()) // 수정일 추가
                .build();
    }
}