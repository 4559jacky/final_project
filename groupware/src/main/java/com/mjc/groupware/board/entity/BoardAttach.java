package com.mjc.groupware.board.entity;

import java.net.URI;
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
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name="board_attach")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
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
    
    @Column(name = "board_attach_status")
    private String boardAttachStatus = "N"; // 서버에서 사진 삭제

	public URI getFile_path() {
		return null;
	}

}