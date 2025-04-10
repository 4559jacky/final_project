package com.mjc.groupware.board.entity;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "board_attach")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BoardAttach {

    @Id // 기본 키
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "attach_no")
    private Long attachNo; // 첨부 번호

    @Column(name = "ori_name")
    private String oriName; // 기존 파일명

    @Column(name = "new_name")
    private String newName; // 변경 파일명

    @Column(name = "attach_path")
    private String attachPath; // 파일 경로

    @Column(name = "board_no")
    private Long boardNo; // 게시판 번호

    @Column(name = "reg_date")
    private LocalDateTime regDate; // 등록일

    @Column(name = "mod_date")
    private LocalDateTime modDate; // 수정일
}