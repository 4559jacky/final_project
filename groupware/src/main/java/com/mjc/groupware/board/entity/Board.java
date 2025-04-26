package com.mjc.groupware.board.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.mjc.groupware.common.converter.BooleanYNConverter;
import com.mjc.groupware.member.entity.Member;

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
    private String boardStatus = "N"; // 삭제되지 않은 상태 기본값 'N'

    @CreationTimestamp
    @Column(updatable = false, name = "reg_date")
    private LocalDateTime regDate;

    @UpdateTimestamp
    @Column(insertable = false, name = "mod_date")
    private LocalDateTime modDate;

    @ManyToOne
    @JoinColumn(name = "member_no")
    private Member member;

    @OneToMany(mappedBy = "board", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BoardAttach> attachList = new ArrayList<>();

    @Column(name = "is_fixed", nullable = false)
    @Convert(converter = BooleanYNConverter.class)
    private Boolean isFixed;

    @PrePersist
    public void prePersist() {
        if (isFixed == null) {
            isFixed = false;  // 기본값 false로 설정
        }
    }

    @PreUpdate
    public void preUpdate() {
        if (isFixed == null) {
            isFixed = false;  // 업데이트 전 null 값을 false로 설정
        }
    }
}