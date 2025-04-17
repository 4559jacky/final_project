package com.mjc.groupware.board.dto;

import java.time.LocalDateTime;

import com.mjc.groupware.board.entity.Board;
import com.mjc.groupware.board.entity.BoardAttach;

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
@ToString
public class BoardAttachDto {
    private Long attach_no;
    private Long board_no;
    private String ori_name;
    private String new_name;
    private String attach_path;
    private LocalDateTime reg_date;
    private LocalDateTime mod_date;
    
    public BoardAttach toEntity() {
    	return BoardAttach.builder()
    				.attachNo(attach_no)
    				.oriName(ori_name)
    				.newName(new_name)
    				.attachPath(attach_path)
    				.board(Board.builder().boardNo(board_no).build())
    				.build();
    }
    public BoardAttachDto toDto(BoardAttach boardAttach) {
        return BoardAttachDto.builder()
            .attach_no(boardAttach.getAttachNo())
            .board_no(boardAttach.getBoard().getBoardNo())  // Board와 연결된 boardNo
            .ori_name(boardAttach.getOriName())
            .new_name(boardAttach.getNewName())
            .attach_path(boardAttach.getAttachPath())
            .reg_date(boardAttach.getRegDate())
            .mod_date(boardAttach.getModDate())
            .build();
    }
}