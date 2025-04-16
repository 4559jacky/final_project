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
    private Long boardattach_no;
    private Long board_no;
    private String ori_name;
    private String new_name;
    private String attach_path;
    private LocalDateTime reg_date;
    private LocalDateTime mod_date;
    
    public BoardAttach toEntity() {
    	return BoardAttach.builder()
    				.attachNo(boardattach_no)
    				.oriName(ori_name)
    				.newName(new_name)
    				.attachPath(attach_path)
    				.board(Board.builder().boardNo(board_no).build())
    				.build();
    }
}