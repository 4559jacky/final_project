package com.mjc.groupware.board.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.mjc.groupware.member.entity.Member;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "board")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Board {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "board_no")
    private Long boardNo;

    @Column(name = "board_title", nullable = false, length = 100)
    private String boardTitle;

    @Column(name = "board_content", columnDefinition = "LONGTEXT", nullable = false)
    private String boardContent;

    @Column(name = "views", nullable = false)
    private int views = 0;

    @Column(name = "board_status", nullable = false)
    private String boardStatus = "Y"; // 삭제 되지 않는 상태

    @CreationTimestamp
    @Column(updatable=false, name = "reg_date")
    private LocalDateTime regDate;
    
    @UpdateTimestamp
    @Column(insertable=false, name = "mod_date")
    private LocalDateTime modDate;
    
    @ManyToOne
    @JoinColumn(name = "member_no")
    private Member member;
    
    // 파일 첨부
    @OneToMany(mappedBy = "board", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BoardAttach> attachList = new ArrayList<>();
}