package com.mjc.groupware.board.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "board")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Board {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long boardNo; // 게시판 번호

    private String boardTitle; // 게시판 제목

    @Column(columnDefinition = "TEXT")
    private String boardContent; // 게시판 내용

    private int views; // 조회수

    private LocalDateTime regDate; // 게시글 등록일

    private LocalDateTime modDate; // 게시글 수정일

    private Long memberNo; // 사원 번호
    
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "member_no", insertable = false, updatable = false)
//    private Member member;
}