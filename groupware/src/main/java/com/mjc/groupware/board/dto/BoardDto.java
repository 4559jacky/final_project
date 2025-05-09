package com.mjc.groupware.board.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.mjc.groupware.board.entity.Board;
import com.mjc.groupware.board.entity.BoardAttach;
import com.mjc.groupware.member.entity.Member;
import com.mjc.groupware.vote.dto.VoteDto;

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
    
    private Boolean is_fixed; // 기본값 false로 초기화

    private List<BoardAttach> attachList;
    private List<Long> delete_files;
    private VoteDto vote;

    // Board 엔티티로 변환
    public Board toEntity() {
        return Board.builder()
                .boardNo(board_no)
                .boardTitle(board_title)
                .boardContent(board_content)
                .boardStatus(board_status)
                .views(views)
                .isFixed(is_fixed != null ? is_fixed : false)
                .member(Member.builder().memberNo(member_no).build())
                .build();
    }

    // Board 엔티티에서 DTO로 변환
    public static BoardDto fromEntity(Board board) {

        return BoardDto.builder()
                .board_no(board.getBoardNo())
                .board_title(board.getBoardTitle())
                .board_content(board.getBoardContent())
                .board_status(board.getBoardStatus())
                .is_fixed(board.getIsFixed())
                .reg_date(board.getRegDate())
                .mod_date(board.getModDate())
                .views(board.getViews())
                .member_no(board.getMember() != null ? board.getMember().getMemberNo() : null)
                .attachList(board.getAttachList())
                .build();
    }
}