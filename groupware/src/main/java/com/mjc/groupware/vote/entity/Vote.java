package com.mjc.groupware.vote.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.mjc.groupware.board.entity.Board;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
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
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(name = "vote")
public class Vote {
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "vote_no")
    private Long voteNo; // 투표 번호

    @ManyToOne(fetch = FetchType.LAZY) // 지연 로딩(즉시 로딩하면 느려질수있어서 쓸수있다)
    @JoinColumn(name = "board_no", nullable = false)
    private Board board; // 게시글 번호

    @Column(name = "vote_title", nullable = false)
    private String voteTitle; // 투표 제목

    @Column(name = "is_multiple", nullable = false, length = 1)
    private String isMultiple = "N"; // 복수 선택 가능 여부(Y/N)

    @Column(name = "is_anonymous", nullable = false, length = 1)
    private String isAnonymous = "N"; // 익명 투표 여부(Y/N)

    @Column(name = "end_date", nullable = false)
    private LocalDateTime endDate; // 투표 마감일

    @Column(name = "reg_date", nullable = false)
    private LocalDateTime regDate; // 투표 등록일

    @OneToMany(mappedBy = "vote", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<VoteOption> voteOptions = new ArrayList<>();

    @OneToMany(mappedBy = "vote", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<VoteResult> voteResults = new ArrayList<>();
}
		