package com.mjc.groupware.board.entity;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name="board_attach")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class BoardAttach {

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "attach_no")
    private Long attachNo;

    @Column(name = "ori_name")
    private String oriName;

    @Column(name = "new_name")
    private String newName;

    @Column(name = "attach_path")
    private String attachPath;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_no", foreignKey = @ForeignKey(name = "board_attach_board_no_fk"))
    private Board board;

    @Column(name = "reg_date")
    private LocalDateTime regDate;

    @Column(name = "mod_date")
    private LocalDateTime modDate;
}