package com.mjc.groupware.board.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * 게시글 정보를 나타내는 엔티티 클래스입니다.
 * 'board' 테이블과 매핑됩니다.
 */
@Entity
@Table(name = "board")
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Board {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="board_no")
    private Long board_no; // 게시글 번호

    @Column(name="board_title")
    private String board_title; // 제목

    @Column(name="board_content")
    private String board_content; // 내용

    @Column(name="member_no")
    private Long member_no; // 작성자 번호

    @Column(name="views")
    private int views = 0; // 조회수

    // 날짜 정보 관리
	@CreationTimestamp
	@Column(updatable=false,name="reg_date")
	private LocalDateTime regDate;
	
	@UpdateTimestamp
	@Column(insertable=false,name="mod_date")
	private LocalDateTime modDate;

    // 조회수 추가
    public void incrementViews() {
        this.views++;
    }

}