package com.mjc.groupware.board.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.mjc.groupware.member.entity.Member;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
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

    // EAGER 로딩으로 Member를 즉시 로드
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "member_no", foreignKey = @ForeignKey(name = "board_member_no_fk"))
    private Member member;

    @Column(name = "board_title", nullable = false, length = 100)
    private String boardTitle;

    @Column(name = "board_content", columnDefinition = "LONGTEXT", nullable = false)
    private String boardContent;

    @Column(name = "views", nullable = false)
    private int views = 0;

    @Column(name = "board_status", length = 1)
    private String boardStatus = "N"; // 기본값 'N'을 'N'으로 설정 (Active/Inactive 상태에 대한 의미일 가능성 있음)

    @Column(name = "reg_date")
    private LocalDateTime regDate;

    @Column(name = "mod_date")
    private LocalDateTime modDate;

    @Column(name = "is_deleted")
    private Boolean isDeleted = false; // Soft Delete를 위한 플래그

    @OneToMany(mappedBy = "board", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BoardAttach> attachments = new ArrayList<>();

    // 생성자에서 Member가 반드시 있어야 한다는 유효성 검사를 추가
    public Board(Member member, String boardTitle, String boardContent) {
        if (member == null) {
            throw new IllegalArgumentException("Member cannot be null");
        }
        this.member = member;
        this.boardTitle = boardTitle;
        this.boardContent = boardContent;
        this.regDate = LocalDateTime.now();
    }

    // 논리 삭제를 위한 메소드
    public void deleteBoard() {
        this.isDeleted = true;
        this.modDate = LocalDateTime.now();
    }
}