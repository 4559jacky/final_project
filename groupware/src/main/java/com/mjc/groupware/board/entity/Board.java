package com.mjc.groupware.board.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

import com.mjc.groupware.board.dto.BoardDto;

@Entity
@Table(name = "board")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Board {

	 @Id // 기본 키
	 @GeneratedValue(strategy = GenerationType.IDENTITY)
	 @Column(name = "board_no")
	 private Long boardNo; // 게시글 번호

	 @Column(name = "member_no")
	 private Long memberNo; // 작성자 회원 번호

	 @Column(name = "board_title", nullable = false) // null 허용 여부
	 private String boardTitle; // 게시글 제목

	 @Column(name = "board_content", columnDefinition = "LONGTEXT", nullable = false)
	 private String boardContent; // 게시글 내용

	 @Column(name = "views", nullable = false, columnDefinition = "INT UNSIGNED DEFAULT 0")
	 private int views; // 조회수

	 @Column(name = "reg_date") 
	 private LocalDateTime regDate; // 등록일

	 @Column(name = "mod_date")
	 private LocalDateTime modDate; // 수정일

	 @Column(name = "board_status", columnDefinition = "CHAR(1) DEFAULT 'N'")
	 private String boardStatus = "N"; // 게시글 상태 (기본값 'N')

	 // DTO로부터 게시글 정보를 업데이트하는 메소드
	 public void updateFromDto(BoardDto dto) {
	      this.boardTitle = dto.getBoard_title();
	      this.boardContent = dto.getBoard_content();
	      this.modDate = LocalDateTime.now(); // 수정일을 현재 시간으로 설정
	    }
	}