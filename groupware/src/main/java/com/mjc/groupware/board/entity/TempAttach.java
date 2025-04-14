package com.mjc.groupware.board.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "temp_attach")
@Getter
@Setter
public class TempAttach {

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
    @JoinColumn(name = "board_no", foreignKey = @ForeignKey(name = "temp_attach_board_no_fk"))
    private TempBoard tempBoard;

    @Column(name = "reg_date")
    private LocalDateTime regDate;

    @Column(name = "mod_date")
    private LocalDateTime modDate;
}