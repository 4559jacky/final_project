package com.mjc.groupware.board.dto;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

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
    
    private List<MultipartFile> files; // 파일 관련 필드 추가

    public static Board toEntity(BoardDto dto) {
        return Board.builder()
                .board_no(dto.getBoard_no())
                .board_title(dto.getBoard_title())
                .board_content(dto.getBoard_content())
                .member_no(dto.getMember_no())
                .views(dto.getViews())
                .build();
    }

    public static BoardDto toDto(Board entity) {
        return BoardDto.builder()
                .board_no(entity.getBoard_no())
                .board_title(entity.getBoard_title())
                .board_content(entity.getBoard_content())
                .member_no(entity.getMember_no())
                .views(entity.getViews())
                .reg_date(entity.getReg_date())
                .mod_date(entity.getMod_date())
                .build();
    }
}
