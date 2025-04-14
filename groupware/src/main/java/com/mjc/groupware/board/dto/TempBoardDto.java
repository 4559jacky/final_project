package com.mjc.groupware.board.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.mjc.groupware.board.entity.TempBoard;

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
public class TempBoardDto {

    private Long boardNo;
    private String boardTitle;
    private String boardSaveContent;
    private Long memberNo;
    private LocalDateTime regDate;
    private LocalDateTime modDate;

    public static TempBoardDto fromEntity(TempBoard entity) {
        return TempBoardDto.builder()
                .boardNo(entity.getBoardNo())
                .boardTitle(entity.getBoardTitle())
                .boardSaveContent(entity.getBoardSaveContent())
                .memberNo(entity.getMemberNo())
                .regDate(entity.getRegDate())
                .modDate(entity.getModDate())
                .build();
    }
}