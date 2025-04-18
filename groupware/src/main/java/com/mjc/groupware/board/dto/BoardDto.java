package com.mjc.groupware.board.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.mjc.groupware.board.entity.Board;
import com.mjc.groupware.board.entity.BoardAttach;
import com.mjc.groupware.member.entity.Member;

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
public class BoardDto {
    private Long board_no;
    private Long member_no;
    private String board_title;
    private String board_content; 
    private int views;
    private String board_status; 
    private LocalDateTime reg_date;  
    private LocalDateTime mod_date; 
    
    // 파일 첨부
    private List<BoardAttach> attachList;
    // 삭제할 파일 ID 리스트 변수 추가
    private List<Long> delete_files;
    
    public Board toEntity() {
    	return Board.builder()
    			.boardTitle(board_title)
    			.boardContent(board_content)
    			.boardNo(board_no)
    			.member(Member.builder().memberNo(member_no).build())
    			.build();
    }
    
    public BoardDto toDto(Board board) {
    	return BoardDto.builder()
    			.board_title(board.getBoardTitle())
    			.board_content(board.getBoardContent())
    			.board_no(board.getBoardNo())
    			.reg_date(board.getRegDate())
    			.mod_date(board.getModDate())
    			.build();
    }
    
}