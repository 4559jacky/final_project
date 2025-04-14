package com.mjc.groupware.board.entity;

import java.time.LocalDateTime;

import com.mjc.groupware.board.dto.BoardDto;
import com.mjc.groupware.member.entity.Member;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "temp_board")
@Getter
@Setter
@Builder
public class TempBoard {

		@Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    @Column(name = "board_no")
	    private Long boardNo;

	    @Column(name = "board_title", nullable = false)
	    private String boardTitle;

	    @Column(name = "board_save_content", columnDefinition = "LONGTEXT", nullable = false)
	    private String boardSaveContent;

	    @Column(name = "member_no")
	    private Long memberNo;

	    @Column(name = "reg_date")
	    private LocalDateTime regDate;

	    @Column(name = "mod_date")
	    private LocalDateTime modDate;
	    
	    @ManyToOne(fetch = FetchType.LAZY)
	    @JoinColumn(name = "member_no", referencedColumnName = "member_no", insertable = false, updatable = false)  // 중복 매핑 방지
	    private Member member;

		public BoardDto getMember() {
			return null;
		}


		
	}