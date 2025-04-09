package com.mjc.groupware.board.dto;

import java.time.LocalDateTime;

import com.mjc.groupware.board.entity.Board;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;


/**
 * 클라이언트와의 데이터 전송을 위한 DTO 클래스입니다.
 */
@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BoardDto {

    private Long board_no;
    private String board_title;
    private String board_content;
    private Long member_no;
    private int views;
    private LocalDateTime reg_date;
    private LocalDateTime mod_date;
    
    public BoardDto toDto(Board board) {
    	return BoardDto.builder()
    			.board_title(board.getBoard_title())
    			.board_content(board.getBoard_content())
    			.board_no(board.getBoard_no())
    			.views(board.getViews())
    			.member_no(board.getMember_no())
    			.reg_date(board.getRegDate())
    			.mod_date(board.getModDate())    			
    			.build();
    }
	

}