package com.mjc.groupware.board.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.mjc.groupware.common.converter.BooleanYNConverter;
import com.mjc.groupware.member.entity.Member;
import com.mjc.groupware.vote.entity.Vote;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
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
    private String boardStatus = "N";

    @CreationTimestamp
    @Column(updatable = false, name = "reg_date")
    private LocalDateTime regDate;

    @UpdateTimestamp
    @Column(insertable = false, name = "mod_date")
    private LocalDateTime modDate;

    @ManyToOne
    @JoinColumn(name = "member_no")
    private Member member;

    @OneToMany(mappedBy = "board", cascade = CascadeType.ALL)
    private List<BoardAttach> attachList = new ArrayList<>();

    @Column(name = "is_fixed", nullable = false)
    @Convert(converter = BooleanYNConverter.class)
    private Boolean isFixed;

    // 변경된 부분: vote는 Vote 테이블에서 board_no를 참조하고 있음
    @OneToOne(mappedBy = "board", cascade = CascadeType.ALL, orphanRemoval = true)
    private Vote vote;

    @PrePersist
    public void prePersist() {
        if (isFixed == null) {
            isFixed = false;
        }
    }

    @PreUpdate
    public void preUpdate() {
        if (isFixed == null) {
            isFixed = false;
        }
    }
}